/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMath;
import frc.robot.util.interfaces.IMercMotorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveHeading extends Command {
    protected final int CLOSED_LOOP_TIME_MS = 1;
    private final Logger LOG = LogManager.getLogger(MoveHeading.class);
    protected int moveThresholdTicks;   // ticks
    protected double angleThresholdDeg; // degrees
    protected int onTargetMinCount; // 100 millis
    protected int checkThreshold = 50;
    protected IMercMotorController left, right;

    protected double distance, targetHeading;

    protected double dirFactor;

    protected int onTargetCount, initialCheckCount;

    /**
     * Move with heading assist from pigeon
     *
     * @param distance distance to move in inches
     * @param heading  heading to turn to for the pigeon
     */
    public MoveHeading(double distance, double heading) {
        requires(Robot.driveTrain);

        left = Robot.driveTrain.getLeftLeader();
        right = Robot.driveTrain.getRightLeader();

        moveThresholdTicks = 500;
        angleThresholdDeg = 5;
        onTargetMinCount = 4;

        dirFactor = Robot.driveTrain.getDirection().dir;

        this.distance = MercMath.inchesToEncoderTicks(distance * dirFactor);
        this.targetHeading = MercMath.degreesToPigeonUnits(heading);

        setName("MoveHeading Command");
        LOG.info(getName() + " Constructed");
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        Robot.driveTrain.resetEncoders();

        if (!Robot.driveTrain.isInMotionMagicMode())
            Robot.driveTrain.initializeMotionMagicFeedback();

        onTargetCount = 0;
        initialCheckCount = 0;

        /* Motion Magic Configurations */
        right.configMotionAcceleration(1000);
        right.configMotionCruiseVelocity((int) MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM));

        int closedLoopTimeMs = 1;
        right.configClosedLoopPeriod(0, closedLoopTimeMs);
        right.configClosedLoopPeriod(1, closedLoopTimeMs);

        right.configAuxPIDPolarity(true);

        Robot.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        Robot.driveTrain.resetPigeonYaw();

        LOG.info(getName() + " Initialized");
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        /* Configured for MotionMagic on Quad Encoders and Auxiliary PID on Pigeon */
        right.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
        left.follow(right, FollowerType.AuxOutput1);
        LOG.info(getName() + " Executed");
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (initialCheckCount < checkThreshold) {
            initialCheckCount++;
            return false;
        }

        double distError = right.getClosedLoopError(), angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        angleError = MercMath.pigeonUnitsToDegrees(angleError);

        boolean isFinished = false;

        boolean isOnTarget = (Math.abs(distError) < moveThresholdTicks &&
                Math.abs(angleError) < angleThresholdDeg);

        if (isOnTarget) {
            onTargetCount++;
        } else {
            if (onTargetCount > 0)
                onTargetCount = 0;
        }

        if (onTargetCount > onTargetMinCount) {
            isFinished = true;
            onTargetCount = 0;
        }

        return isFinished;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        Robot.driveTrain.stop();
        Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
        LOG.info(getName() + " Ended");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        LOG.info(getName() + " Interrupted");
        this.end();
    }
}
