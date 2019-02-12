/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.Robot;

public class DriveWithLIDAR extends MoveHeading {
  private final Logger LOG = LogManager.getLogger(DriveWithLIDAR.class);

  public double minimumDistance;

  /**
   * @param minDist The distance for the robot to be away from the LIDAR's target when it reaches said target.
   */
  public DriveWithLIDAR(double minDist, double percentVoltage) {
    super(0, percentVoltage);
    LOG.info(getName() + " Beginning constructor");
    requires(Robot.driveTrain);
    minimumDistance = minDist;
    LOG.info("DriveWithLIDAR constructed with minimum distance of: "  + minimumDistance);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
    LOG.info(getName() + " initialized");
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
    super.end();
    LOG.info(getName() + " ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " interrupted");
    this.end();
  }
}
