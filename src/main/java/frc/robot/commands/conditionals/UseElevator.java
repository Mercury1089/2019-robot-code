package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.Robot;

/**
 * The command that should be used to run the elevator. 
 * Runs the correct Safe elevator
 */
public class UseElevator extends ConditionalCommand {

  private ElevatorPosition targetPosition;

  public UseElevator(ElevatorPosition targetPosition) {
    super(new SmartElevatorUp(targetPosition), new SmartElevatorDown(targetPosition));
    this.targetPosition = targetPosition;
  }

  @Override
  public boolean condition() {
    return targetPosition.encPos - Robot.elevator.getCurrentHeight() > 0; //true = up; false = down
  }
}
