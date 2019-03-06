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
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;

/**
 * Add your docs here.
 */
public class CargoIntake extends Subsystem {
  MercVictorSPX intake;
  MercTalonSRX articulator;
  ArticulationPosition currentState;

  public enum ArticulationSpeed {
    FAST_REVERSE(-1.0),
    SLOW_REVERSE(-0.5),
    SLOW_FORWARD(0.5),
    FAST_FORWARD(1.0);
    public final double SPEED;

    ArticulationSpeed(double speed) {
      SPEED = speed;
    }
  }

  public enum IntakeSpeed {
    FAST_IN(1.0),
    SLOW_IN(0.5),
    SLOW_OUT(-0.5),
    FAST_OUT(-1.0);

    public double speed;

    IntakeSpeed(double speed) {
      this.speed = speed;
    }
  }

  public enum ArticulationPosition {
    IN(0.0),
    ANGLED45(0.5),
    OUT(1.0);

    private double ticks;

    ArticulationPosition(double ticks) {
      this.ticks = ticks;
    }

    public double getTicks() {
      return ticks;
    }
  }

  public CargoIntake() {
    intake = new MercVictorSPX(CAN.CARGO_INTAKE);
    articulator = new MercTalonSRX(CAN.CARGO_MANIPULATOR);
    articulator.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void setIntakeSpeed(IntakeSpeed intakeSpeed) {
    intake.setSpeed(intakeSpeed.speed);
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
