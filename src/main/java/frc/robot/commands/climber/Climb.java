/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.Climber;

public class Climb extends CommandGroup {
  /**
   * Add your docs here.
   */
  public Climb() {
   addSequential(new RaiseOrLowerRobot(Climber.ClimberPosition.RAISED));
   // drive robot while raised      addSequential();
   addSequential(new RaiseOrLowerRobot(Climber.ClimberPosition.RAISED_BACK));
   // drive robot while raised      addSequential();
   addSequential(new RaiseOrLowerRobot(Climber.ClimberPosition.GROUNDED));
  }
}
