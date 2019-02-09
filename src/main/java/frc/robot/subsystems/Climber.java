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

 */
public class Climber extends Subsystem implements PIDOutput {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private IMercMotorController drive, 
                               liftFrontLeft, 
                               liftFrontRight, 
                               liftBack;

  public static enum ClimberPosition{
    GROUNDED,
    RAISED,
    RAISED_BACK,
  }
  private ClimberPosition currentPosition; 

  public Climber(){
    currentPosition = ClimberPosition.GROUNDED;
    drive = new MercTalonSRX(CAN.CLIMB_DRIVE);
    liftFrontLeft = new MercTalonSRX(CAN.CLIMB_FL);
    liftFrontRight = new MercTalonSRX(CAN.CLIMB_FR);
    liftBack = new MercTalonSRX(CAN.CLIMB_BACK);
  }

  @Override
  public void initDefaultCommand() {
    //.- .-.  -.  .-  ...-      ..  ...       --.  .-  -.--
  }

  @Override
  public void pidWrite(double output) {
    
  }

  public IMercMotorController getClimberLiftDrive(){
    return this.drive;
  }

  public IMercMotorController getClimberFrontLeftLift(){
    return this.liftFrontLeft;
  }

  public IMercMotorController getClimberFrontRightLift(){
    return this.liftFrontRight;
  }

  public IMercMotorController getClimberBackLift(){
    return this.liftBack;
  }

  public ClimberPosition getClimberPosition(){
    return this.currentPosition;
  }

  public void setPosition(ClimberPosition pos){
    
    this.currentPosition = pos;
  }

  public boolean isDriveEnabled(){
    return (currentPosition == ClimberPosition.RAISED || 
           currentPosition == ClimberPosition.RAISED_BACK) ? true: false;     
  }
}
