/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 */
public class HatchManipulator extends Subsystem {
  IMercMotorController ejector;
  IMercMotorController articulator;

  public HatchManipulator() {
    //ejector = new MercVictorSPX(CAN.)
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

}