/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class GetCargo extends CommandGroup {
  
  private Logger log = LogManager.getLogger(GetCargo.class);

  private RotateToTarget
  private DriveDistance distanceOriginator = new DriveWithLIDAR(.6, .6)
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
