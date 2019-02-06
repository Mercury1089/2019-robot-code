package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.DelayableLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMath;
import frc.robot.util.Recallable;


import java.util.concurrent.TimeUnit;

/**
 * Uses Talons and mag encoders to drive a setClawState distance.
 */
@Deprecated
public class EncDriveDistance extends Command implements Recallable<Double> {
    private final double MOVE_THRESHOLD = 500;
    private final int ON_TARGET_MINIMUM_COUNT = 10;
    private int onTargetCount;

    private static Logger log = LogManager.getLogger(EncDriveDistance.class);
    private static final DelayableLogger SLOW_LOG = new DelayableLogger(log, 1, TimeUnit.SECONDS);
    protected double distance;
    protected double percentVoltage; // Voltage is NOW from [-1, 1]

    private double[] volts = {.25, .5};         //TEMP output range; make configurable

    private double initialDistance;
    private double distanceTraveled = Double.NEGATIVE_INFINITY;
    private Recallable<Double> originator;
    private RecallMethod treatment;

    /**
     * @param dist  distance to travel, in inches
     * @param pVolt voLtage to use when driving, -1.0 to 1.0
     */
    public EncDriveDistance(double dist, double pVolt) {
        log.info(getName() + " Beginning constructor");
        requires(Robot.driveTrain);
        distance = dist * Robot.driveTrain.GEAR_RATIO;
        percentVoltage = pVolt;
        log.info(getName() + " Constructed");
    }

    public EncDriveDistance(Recallable<Double> o, RecallMethod t, double pVolt) {
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

        Robot.driveTrain.configVoltage(volts[0], volts[1]);

        initialDistance = Robot.driveTrain.getLeftEncPositionInFeet();

        updateDistance();

        log.info(getName() + " initialized");
        
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        long initialTime = System.currentTimeMillis();
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

        //System.out.println(System.currentTimeMillis() - initialTime);

        return isFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
        System.out.println("END STARTING " + System.currentTimeMillis());
        Robot.driveTrain.stop();

        // Get distance delta
        distanceTraveled = initialDistance - Robot.driveTrain.getLeftEncPositionInFeet();

        // Convert the average encoder position to inches
        distanceTraveled *= 12.0;

        //The voltage setClawState on the Talons is global, so the talons must be reconfigured back to their original outputs.
        Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);

        log.info("Final Distance: " + distanceTraveled);
        System.out.println("END ENDING " + System.currentTimeMillis());
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

        endPosL += Robot.driveTrain.getLeftEncPositionInTicks();
        endPosL += Robot.driveTrain.getRightEncPositionInTicks();

        Robot.driveTrain.getLeft().setPosition(endPosL);
        Robot.driveTrain.getRight().setPosition(endPosL);
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
