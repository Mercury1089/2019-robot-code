/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.limelight;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.LimelightAssembly.LimelightPosition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotateLimelight extends Command {
  private final Logger LOG = LogManager.getLogger(RotateLimelight.class);
  private LimelightPosition limePos;
  private double position;

  public RotateLimelight(double position) {
    requires(Robot.limelightAssembly);
    setName("Rotate Limelight Command");
    LOG.info(getName() + " Constructed");
    this.position = position;
  }

  public RotateLimelight(LimelightPosition pos) {
    this(pos.servoPosition);
    limePos = pos;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    LOG.info(getName() + " Initialized");
    Robot.limelightAssembly.setServoPosition(position);
    Robot.limelightAssembly.getLimeLight().setPipeline(limePos);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
