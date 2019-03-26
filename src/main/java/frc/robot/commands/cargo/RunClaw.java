package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Claw;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunClaw extends Command {

    private final Logger LOG = LogManager.getLogger(RunClaw.class);
    private Claw.ClawState state;
    private int timeThreshold = 550;
    private long startTimeMillis;

    public RunClaw(Claw.ClawState state) {
        requires(Robot.claw);

        setName("RunClaw Command");

        LOG.info(getName() + " Constructed");

        this.state = state;
    }

    @Override
    protected void initialize() {
        startTimeMillis = System.currentTimeMillis();
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected void execute() {
        Robot.claw.setClawState(state);
        LOG.info(getName() + " Executed");
    }

    @Override
    protected boolean isFinished() {
        if (state == Claw.ClawState.INTAKING) {
            return Robot.claw.isCargoInRobot();
        }
        return System.currentTimeMillis() - startTimeMillis > timeThreshold;
    }

    @Override
    protected void end() {
        Robot.claw.setClawState(Claw.ClawState.IDLE);
        LOG.info(getName() + " Ended");
    }

    @Override
    protected void interrupted() {
        end();
        LOG.info(getName() + " Interrupted");
    }
}
