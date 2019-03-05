/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import java.io.FileNotFoundException;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.SwitchDriveDirection;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;
import frc.robot.util.DriveAssist.DriveDirection;

public class AutonMove extends CommandGroup {
  /**
   * Add your docs here.
   */
  public AutonMove(MoveOnPath mop, DriveDirection driveDirection) {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.

    addSequential(mop);
    if(Robot.driveTrain.getDirection() != driveDirection) {
      addSequential(new SwitchDriveDirection(driveDirection));
    }

    if(mop.getFilename().indexOf("Station") > 0) {
      try {
        addSequential(new MoveOnPath(mop.getFilename(), MPDirection.BACKWARD));
      } catch (FileNotFoundException fnfe) {
        System.out.println("Not a file!");
        fnfe.printStackTrace();
      }
      addSequential(new DegreeRotate(180));
    }
  }
}
