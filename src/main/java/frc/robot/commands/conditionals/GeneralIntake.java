/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.hatchpanel.AcquireHatchPanel;
import frc.robot.commands.cargo.RunCargoIntake;
import frc.robot.subsystems.CargoIntake.IntakeSpeed;
import frc.robot.util.DriveAssist.DriveDirection;

public class GeneralIntake extends ConditionalCommand {

  public GeneralIntake() {
    super(new AcquireHatchPanel(), new RunCargoIntake(IntakeSpeed.FAST_IN));
  }

  @Override
  public boolean condition() {
    return Robot.driveTrain.getDirection() == DriveDirection.FORWARD;
  }
}