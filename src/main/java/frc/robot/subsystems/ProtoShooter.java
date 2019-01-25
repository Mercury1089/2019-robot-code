/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.sensors.LIDAR;
import frc.robot.util.MercVictorSPX;

/**
 * Add your docs here.
 */
public class ProtoShooter extends Subsystem {
  MercVictorSPX shooterLeft;
  MercVictorSPX shooterRight;
  CANifier canifier;
  LIDAR lidar;

  public enum ShooterSpeed {
    FAST_INTAKE(-1.0),
    SLOW_INTAKE(-0.5),
    STOP(0.0),
    SLOW_EJECT(0.5),
    FAST_EJECT(1.0);
    public final double SPEED;

    ShooterSpeed(double speed) {
      SPEED = speed;
    }
  }

  public ProtoShooter() {
    shooterLeft = new MercVictorSPX(CAN.SHOOTER_LEFT);
    shooterRight = new MercVictorSPX(CAN.SHOOTER_RIGHT);
    shooterLeft.setInverted(true);
    shooterRight.setInverted(false);
    shooterRight.follow(shooterLeft);

    canifier = new CANifier(CAN.CANIFIER);
    this.lidar = new LIDAR(canifier, CANifier.PWMChannel.valueOf(0), LIDAR.PWMOffset.EQUATION_C);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  @Override
  public void periodic() {
    lidar.updatePWMInput();
  }

  public void setSpeed(double x) {
    shooterLeft.setSpeed(x);
    shooterRight.setSpeed(x);
  }

  public LIDAR getLidar() {
    return lidar;
  }

  public void setClawState(ProtoShooter.ShooterSpeed state) {
    shooterLeft.setSpeed(state.SPEED);
    shooterRight.setSpeed(state.SPEED);
  }
}
