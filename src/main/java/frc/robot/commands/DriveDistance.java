package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.DelayableLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;
import frc.robot.util.MercMath;
import frc.robot.util.MercSparkMax;
import frc.robot.util.Recallable;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;

import frc.robot.util.MercTalonSRX;

import java.util.concurrent.TimeUnit;

import static frc.robot.subsystems.DriveTrain.PRIMARY_PID_LOOP;

/**
 * Uses Talons and mag encoders to drive a setClawState distance.
 */
public class DriveDistance extends Command implements Recallable<Double> {
    private final double MOVE_THRESHOLD = 500;
    private final int ON_TARGET_MINIMUM_COUNT = 10;
    private int onTargetCount;

    private static Logger log = LogManager.getLogger(DriveDistance.class);
    private static final DelayableLogger SLOW_LOG = new DelayableLogger(log, 1, TimeUnit.SECONDS);
    protected double distance;
    protected double percentVoltage; // Voltage is NOW from [-1, 1]

    private double[] pid = {.025, 0, .05};      //TEMP eventually make this configurable through constructor
    private double[] volts = {.25, .5};         //TEMP output range; make configurable

    private double initialDistance;
    private double distanceTraveled = Double.NEGATIVE_INFINITY;
    private Recallable<Double> originator;
    private RecallMethod treatment;

    /**
     * @param dist  distance to travel, in inches
     * @param pVolt voLtage to use when driving, -1.0 to 1.0
     */
    public DriveDistance(double dist, double pVolt) {
        log.info(getName() + " Beginning constructor");
        requires(Robot.driveTrain);
        distance = dist;
        percentVoltage = pVolt;
        log.info(getName() + " Constructed");
    }

    public DriveDistance(Recallable<Double> o, RecallMethod t, double pVolt) {
        this(0, pVolt);

        if (o.getType() == getType()) {
            originator = o;
            treatment = t;
            log.info(getName() + " being recalled, with method " + treatment);
        } else {
            log.warn("Recallable type not equal! Looking for " + getType() + ", found " + o.getType() + ".");
        }
    }

    // Called just before this Command runs the first time
    protected void initialize() {

        distanceTraveled = Double.NEGATIVE_INFINITY;

        if (originator != null) {
            distance = originator.recall();

            if (treatment == RecallMethod.REVERSE)
                distance *= -1;
        }

        setPIDF(pid[0], pid[1], pid[2], 0);
        Robot.driveTrain.configVoltage(volts[0], volts[1]);

        initialDistance = Robot.driveTrain.getLeftEncPositionInFeet();

        updateDistance();

        log.info(getName() + " initialized");
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        boolean isFinished = false;

        double leftError = Robot.driveTrain.getLeft().getClosedLoopError();
        double rightError = Robot.driveTrain.getRight().getClosedLoopError();
        boolean isOnTarget = (Math.abs(rightError) < MOVE_THRESHOLD && Math.abs(leftError) < MOVE_THRESHOLD);

        if (isOnTarget) {
            onTargetCount++;
        } else {
            if (onTargetCount > 0) {
                onTargetCount = 0;
            } else {
                // we are definitely moving
            }
        }

        if (onTargetCount > ON_TARGET_MINIMUM_COUNT) {
            isFinished = true;
            onTargetCount = 0;
            log.info("DriveDistance ended");
        }

        return isFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.driveTrain.stop();

        // Get distance delta
        distanceTraveled = initialDistance - Robot.driveTrain.getLeftEncPositionInFeet();

        // Convert the average encoder position to inches
        distanceTraveled *= 12.0;

        //The voltage setClawState on the Talons is global, so the talons must be reconfigured back to their original outputs.
        Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);

        log.info("Final Distance: " + distanceTraveled);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        log.info("DriveDistance interrupted");
        this.end();
    }

    /**
     * Updates the setpoint for PID.
     */
    protected void updateDistance() {
        double endPosL = 0, endPosR = 0;

        endPosL = MercMath.inchesToEncoderTicks(distance);

        // Per CTRE documentation, the encoder value need to increase when the Talon LEDs are green.
        // On Crossfire, the Talon LEDs are *red* when the robot is moving forward. For this reason, we need
        // to negate both endPosR and endPosL.
        // THIS MIGHT CHANGE on the 2018 robot!!
        endPosL = -endPosL;
        endPosR = endPosL;

        endPosL += Robot.driveTrain.getLeftEncPositionInTicks();
        endPosR += Robot.driveTrain.getRightEncPositionInTicks();

        Robot.driveTrain.getLeft().setPosition(endPosL);
        Robot.driveTrain.getRight().setPosition(endPosR);
    }

    /**
     * Sets PID values on both leader talons
     *
     * @param p proportional value
     * @param i integral value
     * @param d derivative value
     * @param f feed-forward value
     */
    private void setPIDF(double p, double i, double d, double f) {
        if (Robot.driveTrain.getLayout() != DriveTrainLayout.SPARKS) {
            TalonSRX left = ((MercTalonSRX)Robot.driveTrain.getLeft()).get();
            TalonSRX right = ((MercTalonSRX)Robot.driveTrain.getLeft()).get();

            left.config_kP(DriveTrain.SLOT_0, p, DriveTrain.TIMEOUT_MS);
            right.config_kP(DriveTrain.SLOT_0, p, DriveTrain.TIMEOUT_MS);
            left.config_kI(DriveTrain.SLOT_0, i, DriveTrain.TIMEOUT_MS);
            right.config_kI(DriveTrain.SLOT_0, i, DriveTrain.TIMEOUT_MS);
            left.config_kD(DriveTrain.SLOT_0, d, DriveTrain.TIMEOUT_MS);
            right.config_kD(DriveTrain.SLOT_0, d, DriveTrain.TIMEOUT_MS);
            left.config_kF(DriveTrain.SLOT_0, f, DriveTrain.TIMEOUT_MS);
            right.config_kF(DriveTrain.SLOT_0, f, DriveTrain.TIMEOUT_MS);
        } else {
            CANSparkMax left = ((MercSparkMax)Robot.driveTrain.getLeft()).get();
            CANSparkMax right = ((MercSparkMax)Robot.driveTrain.getLeft()).get();

            left.getPIDController().setP(p, DriveTrain.SLOT_0);
            right.getPIDController().setP(p, DriveTrain.SLOT_0);
            left.getPIDController().setI(i, DriveTrain.SLOT_0);
            right.getPIDController().setI(i, DriveTrain.SLOT_0);
            left.getPIDController().setD(d, DriveTrain.SLOT_0);
            right.getPIDController().setD(d, DriveTrain.SLOT_0);
            left.getPIDController().setFF(f, DriveTrain.SLOT_0);
            right.getPIDController().setFF(f, DriveTrain.SLOT_0);
        }
    }

    @Override
    public Double recall() {
        if (distanceTraveled > Double.NEGATIVE_INFINITY)
            return distanceTraveled;

        return 0.0;
    }

    @Override
    public CommandType getType() {
        return CommandType.DISTANCE;
    }
}
