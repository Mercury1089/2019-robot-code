package frc.robot.commands;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMath;
import frc.robot.util.config.DriveTrainSettings;
import frc.robot.util.interfaces.IMercMotorController;

import java.io.File;

/**
 * Use motion profiling to move on a specified path
 */
public class MoveOnPath extends Command {
    private static Logger log = LogManager.getLogger(MoveOnPath.class);
	private IMercMotorController left;
	private IMercMotorController right;

	private int TRAJECTORY_SIZE;

    private Trajectory trajectoryR, trajectoryL;

    private MotionProfileStatus statusLeft, statusRight;
    private static Notifier trajectoryProcessor;

    private boolean isRunning;
    private int dir;

    public enum Direction {
        BACKWARD,
        FORWARD
    }

    private MoveOnPath(Trajectory l, Trajectory r, int size, Direction d) {
        requires(Robot.driveTrain);
        log.info(getName() + " Beginning constructor");

        left = Robot.driveTrain.getLeft();
        right = Robot.driveTrain.getRight();

        switch(d) {
            case BACKWARD:
                dir = -1;
                break;
            case FORWARD:
            default:
                dir = 1;
                break;
        }

        trajectoryL = l;
        trajectoryR = r;
        TRAJECTORY_SIZE = size;

        statusLeft = new MotionProfileStatus();
        statusRight = new MotionProfileStatus();

        if (trajectoryProcessor == null) {
            trajectoryProcessor = new Notifier(() -> {
                left.processMotionProfileBuffer();
                right.processMotionProfileBuffer();
            });
        }

        log.info(getName() + " construced: " + TRAJECTORY_SIZE);
    }

    /**
     * Creates this command using the file prefix to determine
     * the files to load.
     *
     * @param name name of the trajectory
     */
	public static MoveOnPath fromCSV (String name, Direction direction) {
        Trajectory tl = null, tr = null;
	    MoveOnPath cmd = null;

        tl = Pathfinder.readFromCSV(new File("/home/lvuser/trajectories/" + name + "_left_detailed.csv"));
        tr = Pathfinder.readFromCSV(new File("/home/lvuser/trajectories/" + name + "_right_detailed.csv"));

        // No need for a null check; if it doesn't load, the entire program will break due to an exception in the native;
        // it can't be stopped.
        cmd = new MoveOnPath(tl, tr, tl.length(), direction);
        cmd.setName("MoveOnPath-" + name);

        return cmd;
	}

    public static MoveOnPath fromTraj (String name, Direction direction) {
        Trajectory tl = null, tr = null;
        MoveOnPath cmd = null;

        tl = Pathfinder.readFromFile(new File("/home/lvuser/trajectories/" + name + "_left_detailed.traj"));
        tr = Pathfinder.readFromFile(new File("/home/lvuser/trajectories/" + name + "_right_detailed.traj"));

        // No need for a null check; if it doesn't load, the entire program will break due to an exception in the native;
        // it can't be stopped.
        cmd = new MoveOnPath(tl, tr, tl.length(), direction);
        cmd.setName("MoveOnPath-" + name);

        return cmd;
    }
	
	//Called just before this Command runs for the first time. 
	protected void initialize() {
	    System.out.println("MoveOnPath: Initializing...");

	    // Reset command state
        reset();

        // Configure PID values
        double[] pid = DriveTrainSettings.getPIDValues("moveOnPath");
        configurePID(pid[0], pid[1], pid[2], Robot.driveTrain.getFeedForward());

        // Change motion control frame period
        left.changeMotionControlFramePeriod(10);
        right.changeMotionControlFramePeriod(10);

        // Fill TOP (API-level) buffer
        fillTopBuffer();

        // Start processing
        // i.e.: moving API points to RAM
        trajectoryProcessor.startPeriodic(0.005);
        log.info(getName() + " Initialized");
	}

	//Called repeatedly when this Command is scheduled to run.
	protected void execute() {
        left.getMotionProfileStatus(statusLeft);
        right.getMotionProfileStatus(statusRight);

        // Give a slight buffer when we process to make sure we don't bite off more than
        // we can chew or however that metaphor goes.
        if (!isRunning && statusLeft.btmBufferCnt >= 5 && statusRight.btmBufferCnt >= 5) {
            setMotionProfileMode(SetValueMotionProfile.Enable);

            log.log(Level.INFO, "Starting motion profile...");

            isRunning = true;
        }
    }

    @Override
	protected boolean isFinished() {
        // If we're running, only finish if both talons
        // reach their last valid point
	    return
            isRunning &&
            statusLeft.activePointValid &&
            statusLeft.isLast &&
            statusRight.activePointValid &&
            statusRight.isLast;
	}

    @Override
	protected void end() {
	    // Stop processing trajectories
        trajectoryProcessor.stop();

		left.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, DriveTrain.TIMEOUT_MS);
        right.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, DriveTrain.TIMEOUT_MS);

        Robot.driveTrain.stop();

        log.log(Level.INFO, "Finished running");
    }

    /**
     * Fill top-level (API-level) buffer with all points
     */
    private void fillTopBuffer() {
	    for (int i = 0; i < TRAJECTORY_SIZE; i++) {
            TrajectoryPoint trajPointL = new TrajectoryPoint();
            TrajectoryPoint trajPointR = new TrajectoryPoint();

	        // NOTE: Encoder ticks are backwards, we need to work with that.
            double currentPosL = -trajectoryL.segments[i].position * dir;
            double currentPosR = -trajectoryR.segments[i].position * dir;

            double velocityL = trajectoryL.segments[i].velocity;
            double velocityR = trajectoryR.segments[i].velocity;

            boolean isLastPointL = TRAJECTORY_SIZE == i + 1;
            boolean isLastPointR = TRAJECTORY_SIZE == i + 1;
            boolean isZero = i == 0;

            // For each point, fill our structure and pass it to API
            trajPointL.position = MercMath.feetToEncoderTicks(currentPosL); //Convert Revolutions to Units
            trajPointR.position = MercMath.feetToEncoderTicks(currentPosR);
            trajPointL.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityL); //Convert RPM to Units/100ms
            trajPointR.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityR);
            trajPointL.profileSlotSelect0 = DriveTrain.SLOT_0;
            trajPointR.profileSlotSelect0 = DriveTrain.SLOT_0;

            // Sets the duration of each trajectory point to 20ms
            trajPointL.timeDur = 20;
            trajPointR.timeDur = 20;

            // Set these to true on the first point
            trajPointL.zeroPos = isZero;
            trajPointR.zeroPos = isZero;

            // Set these to true on the last point
            trajPointL.isLastPoint = isLastPointL;
            trajPointR.isLastPoint = isLastPointR;

            // Push to API level buffer
            left.pushMotionProfileTrajectory(trajPointL);
            right.pushMotionProfileTrajectory(trajPointR);
        }
    }

    private void configurePID(double p, double i, double d, double f) {
        left.configPID(p, i, d, f);
        right.configPID(p, i, d, f);
    }

    private void setMotionProfileMode(SetValueMotionProfile value) {
        left.set(ControlMode.MotionProfile, value.value);
        right.set(ControlMode.MotionProfile, value.value);
    }

    private void reset() {
        // Reset flags and motion profile modes
        isRunning = false;
        setMotionProfileMode(SetValueMotionProfile.Disable);

        // Clear the trajectory buffer
        left.clearMotionProfileTrajectories();
        right.clearMotionProfileTrajectories();

        log.log(Level.INFO, "Cleared trajectories; check: " + statusLeft.btmBufferCnt);
    }
}
