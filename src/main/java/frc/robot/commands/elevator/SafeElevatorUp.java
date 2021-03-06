package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateMouth;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.MouthArticulator.MouthPosition;

/**
 * It has been determined the elevator is moving up and may collide with the cargo intake.
 * Therefore, we will move the intake and the elevator in such a way they don't do that.
 */
public class SafeElevatorUp extends CommandGroup {

    public SafeElevatorUp(ElevatorPosition targetPosition) {
        addSequential(new ArticulateMouth(MouthPosition.OUT));
        addSequential(new AutomaticElevator(ElevatorPosition.ROCKET_1_C, true));
        addParallel(new ArticulateMouth(MouthPosition.IN));
        addSequential(new AutomaticElevator(targetPosition, true));
    }
}
