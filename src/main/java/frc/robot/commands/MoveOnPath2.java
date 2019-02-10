/**
 * 
 */
package frc.robot.commands;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
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
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMath;

import java.io.File;

/**
 * Use motion profiling to move on a specified path
 */
public class MoveOnPath2 extends Command {
    private static Logger log = LogManager.getLogger(MoveOnPath.class);
	private TalonSRX right, left;

	private final int TRAJECTORY_SIZE;

    private Trajectory trajectory;

    private MotionProfileStatus status;
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
    public MoveOnPath2(String filename, Direction direction) {
        requires(Robot.driveTrain);
        setName("MoveOnPath-" + filename);
        log.info(getName() + " Beginning constructor");
        
        trajectory = Pathfinder.readFromCSV(new File("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".pf1.csv"));

        right = ((MercTalonSRX)Robot.driveTrain.getRightLeader()).get();
        left = ((MercTalonSRX)Robot.driveTrain.getLeftLeader()).get();

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
                right.processMotionProfileBuffer();
            });
        }

        status = new MotionProfileStatus();

        if (trajectory != null) {
            TRAJECTORY_SIZE = trajectory.length();
            log.info(getName() + " construced: " + TRAJECTORY_SIZE);
        } else {
            TRAJECTORY_SIZE = 0;
            log.info(getName() + " could not be constructed!");
            end();
        }
    }
	
	//Called just before this Command runs for the first time. 
	protected void initialize() {
        
        System.out.println("running MP");

	    // Reset command state
        reset();

        // Change motion control frame period
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
        right.getMotionProfileStatus(status);

        // Give a slight buffer when we process to make sure we don't bite off more than
        // we can chew or however that metaphor goes.
        if (!isRunning && status.btmBufferCnt >= 5) {
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
            status.activePointValid &&
            status.isLast;
	}

    @Override
	protected void end() {
        // Stop processing trajectories
        trajectoryProcessor.stop();

        right.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);

        Robot.driveTrain.stop();

        log.log(Level.INFO, "Finished running");
    }

    /**
     * Fill top-level (API-level) buffer with all points
     */
    private void fillTopBuffer() {
	    for (int i = 0; i < TRAJECTORY_SIZE; i++) {
            TrajectoryPoint trajPoint = new TrajectoryPoint();

	        // NOTE: Encoder ticks are backwards, we need to work with that.
            double currentPos = trajectory.segments[i].position * dir;

            double velocity = trajectory.segments[i].velocity;

            double heading = trajectory.segments[i].heading;

            boolean isLastPoint = TRAJECTORY_SIZE == i + 1;
            boolean isZero = i == 0;

            // For each point, fill our structure and pass it to API
            trajPoint.position = MercMath.feetToEncoderTicks(currentPos); //Convert Revolutions to Units
            trajPoint.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocity); //Convert RPM to Units/100ms
            trajPoint.auxiliaryPos = MercMath.radiansToPigeonUnits(heading);
            trajPoint.profileSlotSelect0 = DriveTrain.DRIVE_MOTION_PROFILE_SLOT;
            trajPoint.profileSlotSelect1 = DriveTrain.DRIVE_SMOOTH_TURN_SLOT;

            // Sets the duration of each trajectory point to 20ms
            trajPoint.timeDur = 20;

            // Set these to true on the first point
            trajPoint.zeroPos = isZero;

            // Set these to true on the last point
            trajPoint.isLastPoint = isLastPoint;

            // Push to API level buffer
            right.pushMotionProfileTrajectory(trajPoint);
        }
    }

    private void setMotionProfileMode(SetValueMotionProfile value) {
        right.set(ControlMode.MotionProfile, value.value, DemandType.AuxPID, 0);
        left.follow(right, FollowerType.AuxOutput1);
    }

    private void reset() {
        // Reset flags and motion profile modes
        isRunning = false;
        setMotionProfileMode(SetValueMotionProfile.Disable);
        Robot.driveTrain.resetEncoders();

        // Clear the trajectory buffer
        right.clearMotionProfileTrajectories();

        log.log(Level.INFO, "Cleared trajectories; check: " + status.btmBufferCnt);
    }
}
