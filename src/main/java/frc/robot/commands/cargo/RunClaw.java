package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.ClawAndIntake;
import frc.robot.util.DriveAssist.DriveDirection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunClaw extends Command {

    private final Logger LOG = LogManager.getLogger(RunClaw.class);
    private ClawAndIntake.ClawState state;
    private long timeThreshold = 550;
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
        Robot.clawAndIntake.setClawState(state);
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected boolean isFinished() {
        if (state == ClawAndIntake.ClawState.INTAKING) {
            if(Robot.clawAndIntake.isCargoInRobot()) {
                Robot.driveTrain.setDirection(DriveDirection.CARGO);
            }
            return Robot.clawAndIntake.isCargoInRobot();
        }
        else if (state == ClawAndIntake.ClawState.EJECTING) {
            if (System.currentTimeMillis() - startTimeMillis > timeThreshold) {
                Robot.driveTrain.setDirection(DriveDirection.HATCH);
            }
            return System.currentTimeMillis() - startTimeMillis > timeThreshold;
        }
        return true;
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
