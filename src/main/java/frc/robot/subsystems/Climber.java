/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* .-  ..  -..  .-  -.      .--  .-.  ---  -  .        -  ....  ..  ...       */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.util.DriveAssist;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.PIDGain;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 * 
 */
public class Climber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private IMercMotorController drive, liftBackLeft, liftBackRight, liftFront;

  public static final int LIFT_BR_RUN = 0, 
                          LIFT_BR_ADJUST = 1, 
                          LIFT_FRONT_RUN = 0,
                          LIFT_FRONT_ADJUST = 1;

  public static final int REMOTE_DEVICE_0 = 0, 
                          REMOTE_DEVICE_1 = 1;

  public static final int PRIMARY_LOOP = 0,
                          AUXILIARY_LOOP = 1;

  private final PIDGain LIFT_BR_RUN_GAINS, LIFT_BR_ADJUST_GAINS, LIFT_FRONT_RUN_GAINS, LIFT_FRONT_ADJUST_GAINS;

  private boolean isInLiftMode;

  private ClimberPosition currentPosition; 

  public enum ClimberPosition {
    GROUNDED,
    RAISED_BACK,
    RAISED
  }

  public enum ScrewMotor {
    BACK_RIGHT,
    BACK_LEFT,
    FRONT
  } 

  public Climber(){
    currentPosition = ClimberPosition.GROUNDED;
    drive = new MercTalonSRX(CAN.SCREW_DRIVE);
    liftBackLeft = new MercTalonSRX(CAN.LIFT_BL);
    liftBackRight = new MercTalonSRX(CAN.LIFT_BR);
    liftFront = new MercTalonSRX(CAN.LIFT_FRONT);

    LIFT_BR_RUN_GAINS = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);
    LIFT_BR_ADJUST_GAINS = new PIDGain(1.0, 0.0, 2.0, 0.0, 1.0); //CALCULATE FF
    LIFT_FRONT_RUN_GAINS = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);
    LIFT_FRONT_ADJUST_GAINS = new PIDGain(1.0, 0.0, 2.0, 0.0, 1.0); //CALCULATE FF

    initializeLiftFeedback();
  }

  public void initializeLiftFeedback() {
    /* Configure primary feedback as the average of the two encoders */
    liftBackLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Climber.PRIMARY_LOOP);

    liftBackRight.configRemoteFeedbackFilter(liftBackLeft.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, Climber.REMOTE_DEVICE_0);

    liftBackRight.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
    liftBackRight.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

    liftBackRight.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, Climber.PRIMARY_LOOP);

    liftBackRight.configSelectedFeedbackCoefficient(0.5, Climber.PRIMARY_LOOP);
    
    /* Setup Difference signal to be used for Turn */
    liftBackRight.configSensorTerm(SensorTerm.Diff1, FeedbackDevice.RemoteSensor0);
    liftBackRight.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.CTRE_MagEncoder_Relative);
    
    /* Configure Difference [Difference between both encoders] to be used for Auxiliary PID Index */
		liftBackRight.configSelectedFeedbackSensor(FeedbackDevice.SensorDifference, Climber.AUXILIARY_LOOP);

    /* Aux loop coefficient */
    liftBackRight.configSelectedFeedbackCoefficient(1, Climber.AUXILIARY_LOOP);

    /* Set status frame periods to ensure we don't have stale data */
    liftBackRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20);
    liftBackRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20);
    liftBackRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20);
    liftBackRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20);
    liftBackLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20);

    isInLiftMode = true;

    /*\_/_\_/_\_/_\*/

    liftFront.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DriveTrain.PRIMARY_LOOP);
    liftFront.configRemoteFeedbackFilter(liftBackRight.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, DriveTrain.REMOTE_DEVICE_0);
    
    liftFront.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
    liftFront.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

    liftFront.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, DriveTrain.PRIMARY_LOOP);

    liftFront.configSelectedFeedbackCoefficient(0.5, DriveTrain.PRIMARY_LOOP);

    /* Setup Difference signal to be used for Turn */
    liftFront.configSensorTerm(SensorTerm.Diff1, FeedbackDevice.RemoteSensor0);
    liftFront.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.CTRE_MagEncoder_Relative);
    
    /* Configure Difference [Difference between both encoders] to be used for Auxiliary PID Index */
		liftFront.configSelectedFeedbackSensor(FeedbackDevice.SensorDifference, Climber.AUXILIARY_LOOP);

    /* Aux loop coefficient */
    liftFront.configSelectedFeedbackCoefficient(1, DriveTrain.AUXILIARY_LOOP);
  }

  @Override
  public void initDefaultCommand() {
    //.- .-.  -.  .-  ...-      ..  ...       --.  .-  -.--
    //-.-- . ...   ..   .- --. .-. . .
  }

  public void configPIDSlots(ScrewMotor sm, int primaryPIDSlot, int auxiliaryPIDSlot) {
    if (primaryPIDSlot >= 0) {
      if (sm == ScrewMotor.BACK_RIGHT)
        liftBackRight.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
      else if (sm == ScrewMotor.FRONT)
        liftFront.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
      else
        liftBackLeft.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
    }
    if (auxiliaryPIDSlot >= 0) {
      if (sm == ScrewMotor.BACK_RIGHT)
        liftBackRight.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
      else if (sm == ScrewMotor.FRONT)
        liftFront.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
      else
        liftBackLeft.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
    }
  }

  /**
     * Sets both of the front talons to have a forward output of nominalOutput and peakOutput with the reverse output setClawState to the negated outputs.
     *
     * @param nominalOutput The desired nominal voltage output of the left and right talons, both forward and reverse.
     * @param peakOutput    The desired peak voltage output of the left and right talons, both forward and reverse
     */
  public void configVoltage(double nominalOutput, double peakOutput) {
    drive.configVoltage(nominalOutput, peakOutput);
  }
  
  public void resetEncoders() {
    liftFront.resetEncoder();
    liftBackLeft.resetEncoder();
    liftBackRight.resetEncoder();
  }

  public IMercMotorController getBackLeft(){
    return liftBackLeft;
  }

  public IMercMotorController getBackRight(){
    return liftBackRight;
  }

  public IMercMotorController getFront(){
    return liftFront;
  }

  public double getBackLeftHeightInTicks(){
    return liftBackLeft.getEncTicks();
  }

  public double getBackRightHeightInTicks(){
    return liftBackRight.getEncTicks();
  }

  public double getFrontHeightInTicks(){
    return liftFront.getEncTicks();
  }

  public ClimberPosition getClimberPosition(){
    return this.currentPosition;
  }

  public void setPosition(ClimberPosition pos){
    
    this.currentPosition = pos;
  }

  public boolean isInLiftMode(){
    return isInLiftMode;
  }

  public boolean isDriveEnabled(){
    return currentPosition != ClimberPosition.GROUNDED;     
  }
}
