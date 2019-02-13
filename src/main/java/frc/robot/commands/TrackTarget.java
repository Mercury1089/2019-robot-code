/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;

import frc.robot.Robot;
import frc.robot.commands.drivetrain.MoveHeading;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMath;

public class TrackTarget extends MoveHeading {

  public TrackTarget() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    super(0, 0);    
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
    Robot.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double adjustedDistance = MercMath.feetToEncoderTicks(Robot.limelightAssembly.getLimeLight().getVertDistance());
    System.out.println(adjustedDistance);
    double adjustedHeading = -MercMath.degreesToPigeonUnits(Robot.limelightAssembly.getLimeLight().getTargetCenterXAngle());
    right.set(ControlMode.Position, adjustedDistance, DemandType.AuxPID, adjustedHeading);
    left.follow(right, FollowerType.AuxOutput1);
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
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }
}
