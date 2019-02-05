/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.MercMath;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import frc.robot.util.MercTalonSRX;

public class MoveHeading extends Command {
  IMercMotorController leftI, rightI;
  WPI_TalonSRX left, right;

  private double distance, heading, targetHeading;

  public final int closedLoopTimeMs = 1;

  /**
   * Move with heading assist from pigeon
   * 
   * @param distance distance to move in feet
   * @param heading heading to turn to for the pigeon
   */
  public MoveHeading(double distance, double heading) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.driveTrain);

    leftI = Robot.driveTrain.getLeft();
    rightI = Robot.driveTrain.getRight();

    left = ((MercTalonSRX)(leftI)).get();
    right = ((MercTalonSRX)(rightI)).get();

    this.distance = MercMath.feetToEncoderTicks(distance);
    this.heading = this.targetHeading = heading;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

    targetHeading = heading + Robot.driveTrain.getPigeonYaw();
    System.out.println(targetHeading);

    left.configMotionAcceleration(1000, 40);
    left.configMotionCruiseVelocity((int)MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM), 40);
    right.configMotionAcceleration(1000, 40);
    right.configMotionCruiseVelocity((int)MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM), 40);
    
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

    Robot.driveTrain.getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, DriveTrain.TIMEOUT_MS);
    
    left.selectProfileSlot(DriveTrain.DRIVE_PID_SLOT, DriveTrain.PRIMARY_LOOP);
    left.selectProfileSlot(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT, DriveTrain.AUXILIARY_LOOP);
    right.selectProfileSlot(DriveTrain.DRIVE_PID_SLOT, DriveTrain.PRIMARY_LOOP);
    right.selectProfileSlot(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT, DriveTrain.AUXILIARY_LOOP);

    right.configClosedLoopPeriod(0, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);
		left.configClosedLoopPeriod(0, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);
    right.configClosedLoopPeriod(1, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);
		left.configClosedLoopPeriod(1, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);

    Robot.driveTrain.resetEncoders();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    /* Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon */
		right.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
    left.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override 
  protected boolean isFinished() {
    return Robot.driveTrain.getLeftEncPositionInTicks() >= distance && Robot.driveTrain.getRightEncPositionInTicks() >= distance;
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
