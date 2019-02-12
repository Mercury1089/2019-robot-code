/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.interfaces.IMercMotorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EjectHatchPanel extends Command {
  private final Logger LOG = LogManager.getLogger(EjectHatchPanel.class);
  private int targetPosition = 4096;
  private IMercMotorController ejector;

  private int onTargetCount;
  private final int ON_TARGET_MIN_COUNT = 5;

  public EjectHatchPanel() {
    requires(Robot.hatchManipulator);
    setName("EjectHatchPanel Command");
    LOG.info(getName() + " Constructed");

    onTargetCount = 0;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.hatchManipulator.getEjector().setPosition(targetPosition);

    LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    double error = ejector.getClosedLoopError();
    boolean isFinished = false;
    boolean isOnTarget = (Math.abs(error) < Robot.hatchManipulator.EJECTOR_THRESHOLD);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > ON_TARGET_MIN_COUNT) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    ejector.stop();
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
