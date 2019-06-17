/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.commands.drivetrain.MoveHeading;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;
import frc.robot.Robot;

public class PigeonFollower extends MoveHeading {
  private double currDistAngle;
  private double[][] distAngle;

  public PigeonFollower(double distance, double[][] distAngle) {
    super(distance, 0);
    requires(Robot.driveTrain);

    this.distAngle = distAngle;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //targetHeading = 
    //right.set(ControlMode.Position, distance, DemandType.AuxPID, targetHeading);
    //left.follow(right, FollowerType.AuxOutput1);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
