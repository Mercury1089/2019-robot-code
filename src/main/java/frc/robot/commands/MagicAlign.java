/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

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

    /* Configure the Podge as a remote sensor for the left Talon */
		left.configRemoteFeedbackFilter(Robot.driveTrain.getPigeon().getDeviceID(),					// Device ID of Source
        RemoteSensorSource.Pigeon_Yaw,	        // Remote Feedback Source
        DriveTrain.REMOTE_DEVICE_1,							// Source number [0, 1]
        DriveTrain.TIMEOUT_MS);						      // Configuration Timeout

    /* Configure the Podge as a remote sensor for the right Talon */
		right.configRemoteFeedbackFilter(Robot.driveTrain.getPigeon().getDeviceID(),					// Device ID of Source
        RemoteSensorSource.Pigeon_Yaw,	        // Remote Feedback Source
        DriveTrain.REMOTE_DEVICE_1,							// Source number (you can have up to 2 for 1 talon/victor)
        DriveTrain.TIMEOUT_MS);						      // Configuration Timeout

    right.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, DriveTrain.TIMEOUT_MS);
    right.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, DriveTrain.TIMEOUT_MS);
    right.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, DriveTrain.TIMEOUT_MS);
    right.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20, DriveTrain.TIMEOUT_MS);
    
    left.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, DriveTrain.TIMEOUT_MS);
    left.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, DriveTrain.TIMEOUT_MS);
    left.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, DriveTrain.TIMEOUT_MS);
    left.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20, DriveTrain.TIMEOUT_MS);

    Robot.driveTrain.getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR , 5, DriveTrain.TIMEOUT_MS);
    
    left.selectProfileSlot(DriveTrain.DRIVE_PID_SLOT, DriveTrain.PRIMARY_LOOP);
    left.selectProfileSlot(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT, DriveTrain.AUXILIARY_LOOP);
    right.selectProfileSlot(DriveTrain.DRIVE_PID_SLOT, DriveTrain.PRIMARY_LOOP);
    right.selectProfileSlot(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT, DriveTrain.AUXILIARY_LOOP);
    
			
		/* Calculate targets from gamepad inputs */
		//double target_sensorUnits = forward * Constants.kSensorUnitsPerRotation * Constants.kRotationsToTravel;
		//double target_turn = _targetAngle;
		
		/* Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon */
		//_rightMaster.set(ControlMode.MotionMagic, target_sensorUnits, DemandType.AuxPID, target_turn);
		//_leftMaster.follow(_rightMaster, FollowerType.AuxOutput1);
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
