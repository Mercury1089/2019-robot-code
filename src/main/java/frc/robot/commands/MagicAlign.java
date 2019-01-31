/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;

public class MagicAlign extends Command {
  IMercMotorController leftI, rightI;
  WPI_TalonSRX left, right;

  double distance, heading;

  public MagicAlign() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.driveTrain);

    leftI = Robot.driveTrain.getLeft();
    rightI = Robot.driveTrain.getRight();

    left = (WPI_TalonSRX)(leftI);
    right = (WPI_TalonSRX)(rightI);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    left.configMotionAcceleration(2000, 40);
    left.configMotionCruiseVelocity(2000, 40);
    right.configMotionAcceleration(2000, 40);
    right.configMotionCruiseVelocity(2000, 40);
    
    left.configPeakOutputForward(+1.0, 10);
		left.configPeakOutputReverse(-1.0, 10);
		right.configPeakOutputForward(+1.0, 10);
    right.configPeakOutputReverse(-1.0, 10);
    
    left.getSensorCollection().setQuadraturePosition(0, 10);
    right.getSensorCollection().setQuadraturePosition(0, 10);
    
    left.selectProfileSlot(Constants.kSlot_Distanc, Constants.PID_PRIMARY);
    left.selectProfileSlot(Constants.kSlot_Turning, Constants.PID_TURN);
    right.selectProfileSlot(Constants.kSlot_Distanc, Constants.PID_PRIMARY);
    right.selectProfileSlot(Constants.kSlot_Turning, Constants.PID_TURN);
    
			
		/* Calculate targets from gamepad inputs */
		double target_sensorUnits = forward * Constants.kSensorUnitsPerRotation * Constants.kRotationsToTravel;
		double target_turn = _targetAngle;
		
		/* Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon */
		_rightMaster.set(ControlMode.MotionMagic, target_sensorUnits, DemandType.AuxPID, target_turn);
		_leftMaster.follow(_rightMaster, FollowerType.AuxOutput1);
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
