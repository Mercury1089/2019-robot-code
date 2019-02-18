/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import java.util.logging.LogManager;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;

import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class DriveWithLidar extends MoveHeading {
  //private final Logger LOG = LogManager.getLogger(DriveWithLidar.class);
  private double inchThreshold;
  private double startingDistance;

  /**
   * Construct Drive Distance w / Motion Magic
   * @param distance in inches
   */
  public DriveWithLidar(double distance) {
    super(distance, 0);

    moveThresholdTicks = 500;
    inchThreshold = 0.5;
    angleThresholdDeg = 2;
    onTargetMinCount = 10;
    setName("DriveWithLidar MoveHeading Command");
    //LOG.info(getName() + " Constructed");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
    //LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    right.set(ControlMode.Position, distance, DemandType.AuxPID, targetHeading);
    left.follow(right, FollowerType.AuxOutput1);
    //LOG.info(getName() + " Executed");
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (initialCheckCount < checkThreshold) {
      initialCheckCount++;
      return false;
    }

    double distError = Math.abs(Robot.driveTrain.getLidar().getDistance() - (startingDistance - distance));

    boolean isFinished = false;

    boolean isOnTarget = (distError < inchThreshold);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > onTargetMinCount) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    super.end();
    //LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    //LOG.info(getName() + " Interrupted");
    this.end();
  }
}
