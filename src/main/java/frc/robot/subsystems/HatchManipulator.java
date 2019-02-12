/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercVictorSPX;
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
  private ArticulatorPosition position;

  public final int ARTICULATOR_PID_SLOT = 0;
  public final int EJECTOR_PID_SLOT = 0;

  //Config these    v
  public final int EJECTOR_THRESHOLD = 100;
  public final int ARTICULATOR_THRESHOLD = 100;
  
  public enum ArticulatorPosition{
    //Temporary encoder tick values
    ACQUIRE(2500),
    READY_TO_PICK(1100),
    LOAD_STATION(500),
    IN_BOT(-2000);

    public int encPos;

    ArticulatorPosition(int encPos){
      this.encPos = encPos;
    }
    
    public int getEncoderTicks(){
      return encPos;
    }
  }

  public HatchManipulator() {
    ejector = new MercVictorSPX(CAN.HATCH_EJECTOR);
    articulator = new MercTalonSRX(CAN.HATCH_INTAKE);

    ejector.configAllowableClosedLoopError(EJECTOR_PID_SLOT, EJECTOR_THRESHOLD);
    articulator.configAllowableClosedLoopError(ARTICULATOR_PID_SLOT, ARTICULATOR_THRESHOLD);
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

  public ArticulatorPosition getArticulatorPosition(){
    return position;
  }

  public void setArticulatorPosition(ArticulatorPosition newState){
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