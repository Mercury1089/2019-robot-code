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

/**
 * Add your docs here.
 */
public class ProtoIntake extends Subsystem {
  MercVictorSPX intake;

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

  public ProtoIntake() {
    intake = new MercVictorSPX(CAN.INTAKE);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void setSpeed(double x) {
    intake.setSpeed(x);
  }
}
