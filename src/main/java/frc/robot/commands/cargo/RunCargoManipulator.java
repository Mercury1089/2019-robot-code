/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.CargoManipulator;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunCargoManipulator extends Command {
  private final Logger LOG = LogManager.getLogger(RunCargoManipulator.class);
  private ShooterSpeed targetState;
  private int timeThreshold = 550;
  private long startTimeMillis;

  public RunCargoManipulator(CargoManipulator.ShooterSpeed targetState) {
    requires(Robot.cargoShooter);
    setName("RunCargoManipulator Command");
    LOG.info(getName() + " Constructed");
    this.targetState = targetState;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    startTimeMillis = System.currentTimeMillis();
    LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.cargoShooter.setClawState(targetState);
    LOG.info(getName() + " Executed");
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (targetState == CargoManipulator.ShooterSpeed.FAST_INTAKE || targetState == CargoManipulator.ShooterSpeed.SLOW_INTAKE) {
      return Robot.cargoShooter.getLidar().getDistance() - CargoManipulator.CARGO_IN_ROBOT_THRESH <= 0;
    }
    return System.currentTimeMillis() - startTimeMillis > timeThreshold;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.cargoShooter.setClawState(ShooterSpeed.STOP);
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
