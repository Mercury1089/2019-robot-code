/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.MercMath;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercTalonSRX;

public class TrackTarget extends MoveHeading {

  public TrackTarget(double initDistance, double initHeading) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    super(initDistance, initHeading);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
    right.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
    left.follow(right, FollowerType.AuxOutput1);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    right.set(ControlMode.MotionMagic, Robot.driveTrain.getLimeLight().getVertDistance(), DemandType.AuxPID, Robot.driveTrain.getLimeLight().getTargetCenterXAngle());
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
