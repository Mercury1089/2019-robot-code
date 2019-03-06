/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import frc.robot.subsystems.Elevator.ElevatorPosition;

/**
 * It has been determined the elevator is moving down and may collide with the cargo intake.
 * Therefore, we will move the intake and the elevator in such a way they don't do that.
 */
public class SafeElevatorDown extends CommandGroup {
  
  public SafeElevatorDown(ElevatorPosition targetPosition) {
    addSequential(new ArticulateCargoIntake(ArticulationPosition.ANGLED45));
    addParallel(new AutomaticElevator(ElevatorPosition.ROCKET_1_C));
    addSequential(new AutomaticElevator(targetPosition));
    addSequential(new ArticulateCargoIntake(ArticulationPosition.IN));
  }
}
