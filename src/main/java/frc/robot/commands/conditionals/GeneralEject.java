/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.hatchpanel.EjectHatchPanel;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import frc.robot.commands.cargo.RunCargoManipulator;
import frc.robot.util.DriveAssist.DriveDirection;

public class GeneralEject extends ConditionalCommand {

  public GeneralEject() {
    super(new EjectHatchPanel(), new RunCargoManipulator(ShooterSpeed.FAST_EJECT));
  }

  @Override
  public boolean condition() {
    return Robot.driveTrain.getDirection() == DriveDirection.FORWARD;
  }
}
