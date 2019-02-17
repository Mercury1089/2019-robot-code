/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.testing;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.commands.testing.RunTalonWithGamepad;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 */
public class OneTalonTest extends Subsystem {
  
  public static final double NOMINAL_OUT = 0,
                             PEAK_OUT = 1.0;
                           
  public IMercMotorController testTalon;

  public OneTalonTest() {
    testTalon = new MercTalonSRX(1);
  }

  public void configVoltage(double nominalOutput, double peakOutput) {
    testTalon.configVoltage(nominalOutput, peakOutput);
  }

  public void stop() {
    testTalon.stop();
  }

  public void setNeutralMode(NeutralMode neutralMode) {
    testTalon.setNeutralMode(neutralMode);
}

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new RunTalonWithGamepad());
  }
}
