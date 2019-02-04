/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;

/**
 * Add your docs here.
 */
public class CargoIntake extends Subsystem {
  MercVictorSPX intake;
  MercTalonSRX articulator;
  ArticulationPosition currentState;

  public enum IntakeSpeed {
    FAST_REVERSE(-1.0),
    SLOW_REVERSE(-0.5),
    SLOW_FORWARD(0.5),
    FAST_FORWARD(1.0);
    public final double SPEED;

    IntakeSpeed(double speed) {
      SPEED = speed;
    }
  }

  public enum ArticulationPosition {
    IN,
    OUT;
  }

  public CargoIntake() {
    intake = new MercVictorSPX(CAN.INTAKE);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void setIntakeSpeed(double x) {
    intake.setSpeed(x);
  }

  public void setArticulatorSpeed(double speed) {
    articulator.setSpeed(speed);
  }

  public MercTalonSRX getArticulator() {
    return articulator;
  }

  public ArticulationPosition getArticulatorPosition() {
    return this.currentState;
  }

  public void setArticulatorState(ArticulationPosition state) {
    this.currentState = state;
  }
}
