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
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.commands.cargo.ManuallyArticulateCargoIntake;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.PIDGain;

public class CargoIntake extends Subsystem {

  MercVictorSPX intake;
  MercTalonSRX articulator;
  CargoArticulatorPosition currentState;

  public enum IntakeSpeed {
    FAST_IN(-1.0),
    SLOW_IN(-0.5),
    OFF(0.0),
    SLOW_OUT(0.5),
    FAST_OUT(1.0);

    public double speed;

    IntakeSpeed(double speed) {
      this.speed = speed;
    }
  }
        
  public enum CargoArticulatorPosition {
    IN(100000, 0.15),
    ANGLED45(-36300.0, 0.07),
    OUT(-45000.0, 0.07);

    private double ticks;
    private double p;

    CargoArticulatorPosition(double ticks, double p) {
      this.ticks = ticks;
      this.p = p;
    }

    public double getTicks() {
      return ticks;
    }

    public double getP() {
      return p;
    }
  }

  public CargoIntake() {
    intake = new MercVictorSPX(CAN.CARGO_INTAKE);
    articulator = new MercTalonSRX(CAN.CARGO_MANIPULATOR);

    articulator.configFactoryReset();
    intake.configFactoryReset();

    articulator.setSensorPhase(false);

    articulator.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0);
    articulator.configClosedLoopPeakOutput(0, 0.6);
  }

  public void updateArticulatorP(double p) {
    articulator.configPID(0, new PIDGain(p, 0.0, 0.0, 0));
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new ManuallyArticulateCargoIntake());
  }

  public void setIntakeSpeed(IntakeSpeed intakeSpeed) {
    intake.setSpeed(intakeSpeed.speed);
  }

  public MercTalonSRX getArticulator() {
    return articulator;
  }
}
