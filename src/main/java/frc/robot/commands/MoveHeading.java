/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
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

  private double distance, targetHeading;

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
    this.targetHeading = MercMath.degreesToPigeonUnits(heading);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.driveTrain.resetEncoders();

    Robot.driveTrain.initializeMotionMagicFeedback();

    /* Motion Magic Configurations */
    right.configMotionAcceleration(1000, DriveTrain.TIMEOUT_MS);
    right.configMotionCruiseVelocity((int)MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM), DriveTrain.TIMEOUT_MS);

    int closedLoopTimeMs = 1;
    right.configClosedLoopPeriod(0, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);
    right.configClosedLoopPeriod(1, closedLoopTimeMs, DriveTrain.TIMEOUT_MS);

    right.configAuxPIDPolarity(false, DriveTrain.TIMEOUT_MS);

    right.selectProfileSlot(DriveTrain.DRIVE_PID_SLOT, DriveTrain.PRIMARY_LOOP);
    right.selectProfileSlot(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT, DriveTrain.AUXILIARY_LOOP);
    
    Robot.driveTrain.resetPigeonYaw();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    /* Configured for MotionMagic on Quad Encoders and Auxiliary PID on Pigeon */
    right.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
    left.follow(right, FollowerType.AuxOutput1);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override 
  protected boolean isFinished() {
    //return Robot.driveTrain.getLeftEncPositionInTicks() >= distance && Robot.driveTrain.getRightEncPositionInTicks() >= distance;
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.driveTrain.initializeNormalFeedback();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }
}
