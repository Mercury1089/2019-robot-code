/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;
import frc.robot.commands.drivetrain.SwitchDriveDirection;
import frc.robot.util.DriveAssist.DriveDirection;

import java.io.FileNotFoundException;

public class AutonMove extends CommandGroup {
    /**
     * Add your docs here.
     */

    public AutonMove(String pathname) {
        this(pathname, Robot.driveTrain.getDirection());
    }

    public AutonMove(String pathname, DriveDirection driveDirection) {
        MoveOnPath mop = null;

        try {
            mop = new MoveOnPath(pathname);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Not a path!");
            fnfe.printStackTrace();
        }

        addSequential(mop);
        if (Robot.driveTrain.getDirection() != driveDirection) {
            addSequential(new SwitchDriveDirection(driveDirection));
        }

        if (mop.getFilename().indexOf("Station") > 0) {
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
