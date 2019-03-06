/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.Robot;

public class UseElevator extends ConditionalCommand {

  private ElevatorPosition targetPosition;

  public UseElevator(ElevatorPosition targetPosition) {
    super(new SmartElevatorUp(targetPosition), new SmartElevatorDown(targetPosition));
    this.targetPosition = targetPosition;
  }

  @Override
  public boolean condition() {
    return Robot.elevator.getCurrentHeight() - targetPosition.encPos < 0; //true = up; false = down
  }
}
