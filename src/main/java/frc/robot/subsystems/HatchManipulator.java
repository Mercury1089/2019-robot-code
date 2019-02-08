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

/**
 * Add your docs here.
 */
public class HatchManipulator extends Subsystem {
  IMercMotorController ejector;
  IMercMotorController articulator;
  ArticulatorPosition position;

  public enum HatchIntakeSpeed {
    FAST_REVERSE(-1.0),
    SLOW_REVERSE(-0.5),
    SLOW_FORWARD(0.5),
    FAST_FORWARD(1.0);
    public final double HATCH_ARTICULATOR_SPEED;

    HatchIntakeSpeed(double speed) {
      HATCH_ARTICULATOR_SPEED = speed;
    }
  }

  public enum HatchEjectorSpeed {
    FAST_REVERSE(-1.0),
    SLOW_REVERSE(-0.5),
    SLOW_FORWARD(0.5),
    FAST_FORWARD(1.0);
    public final double HATCH_EJECTOR_SPEED;

    HatchEjectorSpeed(double speed) {
      HATCH_EJECTOR_SPEED = speed;
    }
  }

  public enum ArticulatorPosition{
    ACQUIRE,
    READY_TO_PICK,
    LOAD_STATION,
    IN_BOT
  }

  public HatchManipulator() {
    ejector = new MercVictorSPX(CAN.HATCH_EJECTOR);
    articulator = new MercTalonSRX(CAN.HATCH_INTAKE);
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
    this.position = newState;
  }

  public void setEjectorSpeed(double speed){
    ejector.setSpeed(speed);
  }

  public IMercMotorController getEjector(){
    return ejector;
  }

}