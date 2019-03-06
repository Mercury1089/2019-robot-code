/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.subsystems.CargoIntake.IntakeSpeed;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntakeCargo extends CommandGroup {

  private final Logger LOG = LogManager.getLogger(IntakeCargo.class);
  
  public IntakeCargo() {
    addParallel(new ArticulateCargoIntake(ArticulationPosition.OUT));
    addParallel(new RunCargoIntake(IntakeSpeed.FAST_IN));
    addSequential(new RunCargoManipulator(ShooterSpeed.FAST_INTAKE));
    addSequential(new ArticulateCargoIntake(ArticulationPosition.IN));
    setName("IntakeCargo CommandGroup");
    LOG.info(getName() + " Constructed");
  }
}
