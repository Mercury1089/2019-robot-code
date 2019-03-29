package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.ClawAndIntake;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunClaw extends Command {

    private final Logger LOG = LogManager.getLogger(RunClaw.class);
    private ClawAndIntake.ClawState state;
    private int timeThreshold = 550;
    private long startTimeMillis;

    public RunClaw(ClawAndIntake.ClawState state) {
        requires(Robot.clawAndIntake);

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
        Robot.clawAndIntake.setClawState(state);
        LOG.info(getName() + " Executed");
    }

    @Override
    protected boolean isFinished() {
        if (state == ClawAndIntake.ClawState.INTAKING) {
            return Robot.clawAndIntake.isCargoInRobot();
        }
        else {
            return System.currentTimeMillis() - startTimeMillis > timeThreshold;
        }
    }

    @Override
    protected void end() {
        Robot.clawAndIntake.setClawState(ClawAndIntake.ClawState.IDLE);
        LOG.info(getName() + " Ended");
    }

    @Override
    protected void interrupted() {
        end();
        LOG.info(getName() + " Interrupted");
    }
}
