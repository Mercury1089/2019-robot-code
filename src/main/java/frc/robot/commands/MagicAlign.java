/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class MagicAlign extends Command {
  WPI_TalonSRX left, right;

  public MagicAlign() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.driveTrain);

    left = (WPI_TalonSRX)Robot.driveTrain.getLeft();
    right = (WPI_TalonSRX)Robot.driveTrain.getRight();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    left.configMotionAcceleration(2000, 40);
    left.configMotionCruiseVelocity(2000, 40);
    right.configMotionAcceleration(2000, 40);
		right.configMotionCruiseVelocity(2000, 40);
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
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
