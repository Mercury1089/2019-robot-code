/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.conditionals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.util.DriveAssist.DriveDirection;

public class ConditionalLevel2Elevator extends ConditionalCommand {
  private final Logger LOG = LogManager.getLogger(GeneralEject.class);

    public ConditionalLevel2Elevator() {
        super(new AutomaticElevator(ElevatorPosition.ROCKET_2_HP), new AutomaticElevator(ElevatorPosition.ROCKET_2_C));
        setName("GeneralEject ConditionalCommand");
        LOG.info(getName() + " Constructed");
    }

  @Override
  public boolean condition() {
    return Robot.driveTrain.getDirection() == DriveDirection.HATCH;
  }
}
