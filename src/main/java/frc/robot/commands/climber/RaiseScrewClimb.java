/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.climber;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;

public class RaiseScrewClimb extends Command {

  private WPI_TalonSRX frontRight, frontLeft, back;

  public RaiseScrewClimb() {
    requires(Robot.climber);

    frontRight = (WPI_TalonSRX)Robot.climber.getFrontRight();
    frontLeft = (WPI_TalonSRX)Robot.climber.getFrontLeft();
    back = (WPI_TalonSRX)Robot.climber.getBack();

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    frontLeft.set(ControlMode.MotionMagic, 4096, 
                  DemandType.AuxPID, Robot.climber.getFrontLeftHeightInTicks() - Robot.climber.getFrontRightHeightInTicks());
    frontRight.set(ControlMode.MotionMagic, 4096, DemandType.AuxPID, 
                   Robot.climber.getFrontLeftHeightInTicks()- Robot.climber.getFrontRightHeightInTicks());
    back.set(ControlMode.MotionMagic, 4096, 
             DemandType.AuxPID, 
             (Robot.climber.getFrontLeftHeightInTicks() + Robot.climber.getFrontRightHeightInTicks())/2 - Robot.climber.getBackHeightInTicks());
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
    this.end();
  }
}
