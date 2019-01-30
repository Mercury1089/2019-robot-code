/**
 * 
 */
package frc.robot.commands;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.util.MercTalonSRX;
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
import frc.robot.util.TrajectoryGenerator.TrajectoryPair;

import java.io.File;

/**
 * Use motion profiling to move on a specified path
 */
public class MoveOnPath extends Command {
    private static Logger log = LogManager.getLogger(MoveOnPath.class);
	private TalonSRX left;
	private TalonSRX right;

	private final int TRAJECTORY_SIZE;

    private Trajectory trajectoryR, trajectoryL;

    private MotionProfileStatus statusLeft, statusRight;
    private static Notifier trajectoryProcessor;

    private boolean isRunning;
    private int dir;

    public enum Direction {
        BACKWARD,
        FORWARD
    }

    /**
     * Creates this command using the file prefix to determine
     * the files to load.
     *
     * @param name name of the trajectory
     */
    public MoveOnPath(String filename, Direction direction) {
        requires(Robot.driveTrain);
        setName("MoveOnPath-" + filename);
        log.info(getName() + " Beginning constructor");
        
        trajectoryL = Pathfinder.readFromCSV(new File("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".left.pf1.csv"));
        trajectoryR = Pathfinder.readFromCSV(new File("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".right.pf1.csv"));

        System.out.println(trajectoryL);

        left = ((MercTalonSRX)Robot.driveTrain.getLeft()).get();
        right = ((MercTalonSRX)Robot.driveTrain.getRight()).get();

        switch(direction) {
            case BACKWARD:
                dir = -1;
                break;
            case FORWARD:
            default:
                dir = 1;
                break;
        }

        if (trajectoryProcessor == null) {
            trajectoryProcessor = new Notifier(() -> {
                left.processMotionProfileBuffer();
                right.processMotionProfileBuffer();
            });
        }

        statusLeft = new MotionProfileStatus();
        statusRight = new MotionProfileStatus();

        if (trajectoryL != null) {
            TRAJECTORY_SIZE = trajectoryL.length();
            System.out.println("traj size test - " + TRAJECTORY_SIZE);
            log.info(getName() + " construced: " + TRAJECTORY_SIZE);
        } else {
            TRAJECTORY_SIZE = 0;
            log.info(getName() + " could not be constructed!");
            end();
        }
    }
	
	//Called just before this Command runs for the first time. 
	protected void initialize() {
	    System.out.println("MoveOnPath: Initializing...");

	    // Reset command state
        reset();

        // Change motion control frame period
        left.changeMotionControlFramePeriod(10);
        right.changeMotionControlFramePeriod(10);

        // Fill TOP (API-level) buffer
        fillTopBuffer();

        // Start processing
        // i.e.: moving API points to RAM
        trajectoryProcessor.startPeriodic(0.005);
        System.out.println("mop initialized");
        log.info(getName() + " Initialized");
	}

	//Called repeatedly when this Command is scheduled to run.
	protected void execute() {
        System.out.println("mop executing");
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
            trajPointL.profileSlotSelect0 = DriveTrain.DRIVE_SMOOTH_MOTION_SLOT;
            trajPointR.profileSlotSelect0 = DriveTrain.DRIVE_SMOOTH_MOTION_SLOT;

            // Sets the duration of each trajectory point to 20ms
            trajPointL.timeDur = 15;
            trajPointR.timeDur = 15;

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

    private void setMotionProfileMode(SetValueMotionProfile value) {
        left.set(ControlMode.MotionProfile, value.value);
        right.set(ControlMode.MotionProfile, value.value);
    }

    private void reset() {
        // Reset flags and motion profile modes
        isRunning = false;
        setMotionProfileMode(SetValueMotionProfile.Disable);
        left.getSensorCollection().setQuadraturePosition(0, DriveTrain.TIMEOUT_MS);
        right.getSensorCollection().setQuadraturePosition(0, DriveTrain.TIMEOUT_MS);

        // Clear the trajectory buffer
        left.clearMotionProfileTrajectories();
        right.clearMotionProfileTrajectories();

        log.log(Level.INFO, "Cleared trajectories; check: " + statusLeft.btmBufferCnt);
    }
}
