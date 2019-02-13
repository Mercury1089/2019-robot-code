/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.HatchManipulator.ArticulatorPosition;

public class AcquireHatchPanel extends CommandGroup {
  /**
   * Moves the Hatch Panel Pickup to the floor and then up slightly
   */
  public AcquireHatchPanel() {
    addSequential(new ArticulateHatchPanel(ArticulatorPosition.ACQUIRE, true));
    addSequential(new ArticulateHatchPanel(ArticulatorPosition.READY_TO_PICK));
  }
}
