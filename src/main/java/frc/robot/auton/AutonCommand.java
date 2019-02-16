package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command group that specifies the commands to be run
 * during the autonomous period.
 */
public class AutonCommand extends CommandGroup {
    private static Logger log = LogManager.getLogger(AutonCommand.class);

    public enum AutonStartSide {
        Left, Mid, Right
    }

    public enum MoveLocation {
        Left, Right, Close, Middle, Far, ToStation
    }

    public AutonCommand(AutonStartSide autonSide, MoveLocation[] moveLocations) {
        // FIRST MOVE
          // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
        try {
            addSequential(new MoveOnPath(autonSide.name() + moveLocations[0].name(), MPDirection.FORWARD));
        } catch(FileNotFoundException fnfe) {
            System.out.println("Invalid move, aborting!!!");
            return;
            // In the future we COULD do a stupid check to make sure user didn't say " Right Middle " or so
        }
    }
    public AutonCommand(){
        log.info("No args constructor with default auton running");
        //use addSequential() for a set DriveDistance or other simple commands
    }
}
