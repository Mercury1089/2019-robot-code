/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.limelight;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.util.MercMath;

public class ManualLimelightRotation extends Command {

  public ManualLimelightRotation() {
    requires(Robot.limelightAssembly);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Servo servo = Robot.limelightAssembly.getServo();
    servo.setPosition(MercMath.clamp(servo.get() + MercMath.applyDeadzone(Robot.oi.getX(RobotMap.DS_USB.GAMEPAD), 0.1), 0.0, 1.0));
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
