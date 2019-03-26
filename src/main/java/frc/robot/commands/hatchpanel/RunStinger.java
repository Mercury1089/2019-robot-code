package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Spins the HatchPanel ejector one rotation to eject a hatch panel
 */
public class RunStinger extends Command {

    private final Logger LOG = LogManager.getLogger(RunStinger.class);
    private final int ON_TARGET_MIN_COUNT = 5;
    private int targetPosition = 4096 * 20; //Make this a constant somewhere
    private int onTargetCount;

    public RunStinger() {
        requires(Robot.stinger);
        setName("RunStinger Command");
        LOG.info(getName() + " Constructed");

        onTargetCount = 0;
    }

    public RunStinger(int targetPos) {
        this();
        targetPosition = targetPos;
    }

    @Override
    protected void initialize() {
        Robot.stinger.getEjector().resetEncoder();
        Robot.stinger.getEjector().setPosition(targetPosition);
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected boolean isFinished() {
        double error = Robot.stinger.getEjector().getClosedLoopError();
        boolean isFinished = false;
        boolean isOnTarget = (Math.abs(error) < Robot.stinger.EJECTOR_THRESHOLD);

        if (isOnTarget) {
            onTargetCount++;
        } else {
            if (onTargetCount > 0)
                onTargetCount = 0;
        }

        if (onTargetCount > ON_TARGET_MIN_COUNT) {
            isFinished = true;
            onTargetCount = 0;
        }

        return isFinished;
    }

    @Override
    protected void end() {
        LOG.info(getName() + " Ended");
    }

    @Override
    protected void interrupted() {
        LOG.info(getName() + " Interrupted");
    }
}
