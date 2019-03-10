/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.subsystems.CargoIntake.CargoArticulatorPosition;
import frc.robot.subsystems.Elevator.ElevatorPosition;

/**
 * It has been determined the elevator is moving up and may collide with the cargo intake.
 * Therefore, we will move the intake and the elevator in such a way they don't do that.
 */
public class SafeElevatorUp extends CommandGroup {

  public SafeElevatorUp(ElevatorPosition targetPosition) {
    //addSequential(new ArticulateCargoIntake(CargoArticulatorPosition.ANGLED45));
    addSequential(new AutomaticElevator(ElevatorPosition.ROCKET_1_C));
    addSequential(new AutomaticElevator(targetPosition, true));
    //addParallel(new ArticulateCargoIntake(CargoArticulatorPosition.IN));
  }
}
