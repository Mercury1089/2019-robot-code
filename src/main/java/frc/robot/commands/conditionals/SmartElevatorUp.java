/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.commands.elevator.SafeElevatorUp;
import frc.robot.subsystems.Elevator.ElevatorPosition;

/**
 * Elevator is SAFE if: Elevator is currently below the danger line and it wants to go above the danger line
 * Otherwise, elevator is UNSAFE
 */
public class SmartElevatorUp extends ConditionalCommand {

	ElevatorPosition targetPosition;

	public SmartElevatorUp(ElevatorPosition targetPosition) {
        super(new SafeElevatorUp(targetPosition), new AutomaticElevator(targetPosition));
		this.targetPosition = targetPosition;
	}

	@Override
	public boolean condition() {
		return Robot.elevator.getCurrentHeight() <= ElevatorPosition.ROCKET_1_C.encPos &&
			   targetPosition.encPos >= ElevatorPosition.ROCKET_1_C.encPos;
	}
}
