/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* .-  ..  -..  .-  -.      .--  .-.  ---  -  .        -  ....  ..  ...       */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.util.DriveAssist;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 * 
 */
public class Climber extends Subsystem implements PIDOutput {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private IMercMotorController drive, 
                               liftFrontLeft, 
                               liftFrontRight, 
                               liftBack;


  private boolean isInMotionMagicMode;

  public static enum ClimberPosition{
    GROUNDED,
    RAISED,
    RAISED_BACK
  }
  private ClimberPosition currentPosition; 

  public Climber(){
    currentPosition = ClimberPosition.GROUNDED;
    drive = new MercTalonSRX(CAN.SCREW_DRIVE);
    liftFrontLeft = new MercTalonSRX(CAN.LIFT_BL);
    liftFrontRight = new MercTalonSRX(CAN.LIFT_BR);
    liftBack = new MercTalonSRX(CAN.LIFT_FRONT);
  }

  @Override
  public void initDefaultCommand() {
    //.- .-.  -.  .-  ...-      ..  ...       --.  .-  -.--
    //-.-- . ...   ..   .- --. .-. . .
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

  @Override
  public void pidWrite(double output) {
    
  }

  public IMercMotorController getFrontLeft(){
    return liftFrontLeft;
  }

  public IMercMotorController getFrontRight(){
    return liftFrontRight;
  }

  public IMercMotorController getBack(){
    return liftBack;
  }

  public double getFrontLeftHeightInTicks(){
    return liftFrontLeft.getEncTicks();
  }

  public double getFrontRightHeightInTicks(){
    return liftFrontRight.getEncTicks();
  }

  public double getBackHeightInTicks(){
    return liftBack.getEncTicks();
  }

  public ClimberPosition getClimberPosition(){
    return this.currentPosition;
  }

  public void setPosition(ClimberPosition pos){
    
    this.currentPosition = pos;
  }

  public boolean isInMotionMagicMode(){
    return isInMotionMagicMode;
  }

  public boolean isDriveEnabled(){
    return currentPosition != ClimberPosition.GROUNDED;     
  }
}
