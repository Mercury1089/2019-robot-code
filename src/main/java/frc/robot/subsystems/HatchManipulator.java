/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.PIDGain;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;

/**
 * Subsystem to intake and eject hatch panels
 * articulator is the intake mechanism
 */
public class HatchManipulator extends Subsystem {
  private IMercMotorController ejector;
  private IMercMotorController articulator;
  private HatchArticulatorPosition position;

  public final int ARTICULATOR_PID_SLOT = 0;
  public final int EJECTOR_PID_SLOT = 0;

  public final PIDGain ejectorGain = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);
  public final PIDGain articulatorGain = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);

  //Config these   v
  public final int EJECTOR_THRESHOLD = 100;
  public final int ARTICULATOR_THRESHOLD = 100;
  
  public enum HatchArticulatorPosition{
    //Temporary encoder tick values
    ACQUIRE(0),
    READY_TO_PICK(1100),
    IN_BOT(2000);

    public int encPos;

    HatchArticulatorPosition(int encPos){
      this.encPos = encPos;
    }
    
    public int getEncoderTicks(){
      return encPos;
    }
  }

  public HatchManipulator() {
    ejector = new MercVictorSPX(CAN.HATCH_EJECTOR);
    articulator = new MercTalonSRX(CAN.HATCH_INTAKE);

    ejector.configPID(EJECTOR_PID_SLOT, ejectorGain);
    articulator.configPID(ARTICULATOR_PID_SLOT, articulatorGain);

    ejector.configAllowableClosedLoopError(EJECTOR_PID_SLOT, EJECTOR_THRESHOLD);
    articulator.configAllowableClosedLoopError(ARTICULATOR_PID_SLOT, ARTICULATOR_THRESHOLD);    

    articulator.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void setArticulatorSpeed(double speed) {
    articulator.setSpeed(speed);
  }

  public IMercMotorController getArticulator(){
    return articulator;
  }

  public HatchArticulatorPosition getArticulatorPosition(){
    return position;
  }

  public void setArticulatorPosition(HatchArticulatorPosition newState){
    position = newState;
    articulator.setPosition(newState.encPos);
  }

  public void setEjectorSpeed(double speed){
    ejector.setSpeed(speed);
  }

  public double getArticulatorPositionTicks() {
    return articulator.getEncTicks();
  }

  public IMercMotorController getEjector(){
    return ejector;
  }

  public boolean isArticulatorLimitSwitchClosedReverse() {
    return articulator.isLimitSwitchClosed(LimitSwitchDirection.REVERSE);
  }

  public boolean isArticulatorLimitSwitchClosedForward() {
    return articulator.isLimitSwitchClosed(LimitSwitchDirection.FORWARD);
  }

  public boolean isEjectorLimitSwitchClosed() {
    return ejector.isLimitSwitchClosed(LimitSwitchDirection.REVERSE);
  }
}