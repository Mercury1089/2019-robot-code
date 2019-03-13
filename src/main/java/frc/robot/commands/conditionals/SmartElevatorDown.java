package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.commands.elevator.SafeElevatorDown;
import frc.robot.subsystems.Elevator.ElevatorPosition;

/**
 * Elevator is SAFE if: Elevator is currently above the danger line and it wants to go to BOTTOM
 * Otherwise, elevator is UNSAFE
 */
public class SmartElevatorDown extends ConditionalCommand {

    ElevatorPosition targetPosition;

    public SmartElevatorDown(ElevatorPosition targetPosition) {
        super(new SafeElevatorDown(targetPosition), new AutomaticElevator(targetPosition));
        this.targetPosition = targetPosition;
    }

    @Override
    public boolean condition() {
        return Robot.elevator.getCurrentHeight() > ElevatorPosition.ROCKET_1_C.encPos &&
                targetPosition.encPos < ElevatorPosition.ROCKET_1_C.encPos;
    }
}
