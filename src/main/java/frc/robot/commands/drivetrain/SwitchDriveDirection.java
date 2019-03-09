/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.limelight.RotateLimelight;
import frc.robot.subsystems.LimelightAssembly.LimelightPosition;
import frc.robot.util.DriveAssist.DriveDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SwitchDriveDirection extends CommandGroup {
  private final Logger LOG = LogManager.getLogger(SwitchDriveDirection.class);
  /**
   * Add your docs here.
   */
  public SwitchDriveDirection(DriveDirection driveDir) {

    addParallel(new SwitchDrive(driveDir));
    addSequential(new RotateLimelight(driveDir == DriveDirection.HATCH ? 
            LimelightPosition.FACING_HATCH_PANEL : LimelightPosition.FACING_CARGO));
            
    setName("SwitchDriveDirection CommandGroup");
    LOG.info(getName() + " Constructed");
  }
}
