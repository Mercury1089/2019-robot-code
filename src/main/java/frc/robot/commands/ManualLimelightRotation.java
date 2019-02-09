/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.sensors.Limelight;
import frc.robot.util.MercMath;

public class ManualLimelightRotation extends Command {

  public ManualLimelightRotation() {
    requires(Robot.limelightRotate);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Servo servo = Robot.limelightRotate.getServo();
    double xpos = Robot.oi.getX(RobotMap.DS_USB.GAMEPAD);
    servo.setPosition(MercMath.clamp(servo.get() + (Math.abs(xpos) < 0.1 ? 0.0 : (xpos / 100)), 0.0, 1.0));
    System.out.println(servo.get());
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
