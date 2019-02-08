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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.MercMath;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercTalonSRX;

public class MoveHeading extends Command {

  protected int MOVE_THRESHOLD;   // ticks
  protected double ANGLE_THRESHOLD; // degrees 
  protected int ON_TARGET_MINIMUM_COUNT; // 100 millis
  protected int CHECK_THRESHOLD = 50;
  protected final int closedLoopTimeMs = 1;

  protected IMercMotorController leftI, rightI;
  protected WPI_TalonSRX left, right;

  protected double distance, targetHeading;

  protected int onTargetCount, initialCheckCount;

  /**
   * Move with heading assist from pigeon
   * 
   * @param distance distance to move in inches
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

    MOVE_THRESHOLD = 500;
    ANGLE_THRESHOLD = 5;
    ON_TARGET_MINIMUM_COUNT = 10;

    this.distance = MercMath.inchesToEncoderTicks(distance);
    this.targetHeading = MercMath.degreesToPigeonUnits(heading);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.driveTrain.resetEncoders();

    if (!Robot.driveTrain.isInMotionMagicMode())
      Robot.driveTrain.initializeMotionMagicFeedback();
    
    onTargetCount = 0;
    initialCheckCount = 0;

    /* Motion Magic Configurations */
    right.configMotionAcceleration(1000, RobotMap.CTRE_TIMEOUT);
    right.configMotionCruiseVelocity((int)MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM), RobotMap.CTRE_TIMEOUT);

    int closedLoopTimeMs = 1;
    right.configClosedLoopPeriod(0, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);
    right.configClosedLoopPeriod(1, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);

    right.configAuxPIDPolarity(false, RobotMap.CTRE_TIMEOUT);

    Robot.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    
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
    if (initialCheckCount < CHECK_THRESHOLD) {
      initialCheckCount++;
      return false;
    }

    double distError = right.getClosedLoopError(), angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

    angleError = MercMath.pigeonUnitsToDegrees(angleError);

    boolean isFinished = false;

    SmartDashboard.putNumber("dist error", distError);
    SmartDashboard.putNumber("ang error", angleError);

    boolean isOnTarget = (Math.abs(distError) < MOVE_THRESHOLD && 
                          Math.abs(angleError) < ANGLE_THRESHOLD);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > ON_TARGET_MINIMUM_COUNT) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.driveTrain.stop();
    Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }
}
