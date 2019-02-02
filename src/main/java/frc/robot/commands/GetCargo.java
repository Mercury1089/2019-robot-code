/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.robot.commands.DriveDistance;

public class GetCargo extends CommandGroup {
  
  private Logger log = LogManager.getLogger(GetCargo.class);

  /**
   * Add your docs here.
   */
  public GetCargo() {
    angleOriginator = new RotateToTarget();
    distanceOriginator = new DriveWithLIDAR(8, 0.7);
    log.info(getName() + " Beginning constructor");
    addSequential(angleOriginator);
    addParallel(new UseClaw(Claw.ClawState.GRAB));
    addSequential(distanceOriginator);
  }
}
