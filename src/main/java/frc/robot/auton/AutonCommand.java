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
    private int rotationFactor;
    private final double CARGO_PICKUP_X_OFFSET = 0,
        //Change these values, just placeholders
        CARGO_PICKUP_X_CONSTANT_OFFSET = 0,
        CARGO_PICKUP_Y_CHANGING_OFFSET = 0,
        CARGO_PICKUP_Y_CONSTANT_OFFSET = 0,
        LEFT_CARGO_SHIP_OFFSET = 0,
        RIGHT_CARGO_SHIP_OFFSET = 0,
        LEFT_ROCKET_OFFSET = 0,
        RIGHT_ROCKET_OFFSET = 0,
        OFF_HAB = 0;
        
    public enum AutonStartSide {
        Left, Mid, Right
    }

    public enum MoveLocation {
        Left, Right, Close, Middle, Far, ToStation
    }

    public AutonCommand(AutonStartSide autonSide, MoveLocation[] moveLocations) {
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
        // FIRST MOVE
        try {
            addSequential(new MoveOnPath(autonSide.name() + moveLocations[0].name(), MPDirection.FORWARD));
        } catch(FileNotFoundException fnfe) {
            System.out.println("Invalid move, aborting!!!");
            return;
            // In the future we COULD do a stupid check to make sure user didn't say " Right Middle " or so
        }
        //make a switch case with cases for what side the robot started on ex. middle
    }
}
