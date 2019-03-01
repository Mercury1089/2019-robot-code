/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.commands.hatchpanel.ArticulateHatchPanel;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.HatchManipulator.ArticulatorPosition;

public class TurtleMode extends CommandGroup {
  /**
   * Stows the robot to starting position
   */
  public TurtleMode() {
    addParallel(new AutomaticElevator(ElevatorPosition.BOTTOM));
    addParallel(new ArticulateCargoIntake(ArticulationPosition.IN));
    addSequential(new ArticulateHatchPanel(ArticulatorPosition.IN_BOT));
  }
}
