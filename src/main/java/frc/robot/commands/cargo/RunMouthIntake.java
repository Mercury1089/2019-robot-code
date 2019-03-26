package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.MouthIntaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunMouthIntake extends Command {

    private final Logger LOG = LogManager.getLogger(RunMouthIntake.class);

    private MouthIntaker.IntakeState state;

    public RunMouthIntake(MouthIntaker.IntakeState state) {
        requires(Robot.mouthIntaker);
        setName("RunMouthIntake Command");
        LOG.info(getName() + " Constructed");
        this.state = state;
    }

    @Override
    protected void initialize() {
        Robot.mouthIntaker.setState(state);
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected void execute() {
        switch (state) {
            case INTAKING:
                Robot.mouthIntaker.getIntaker().setSpeed(1.0);
                break;
            case EJECTING:
                Robot.mouthIntaker.getIntaker().setSpeed(-1.0);
                break;
            case IDLE:
                Robot.mouthIntaker.getIntaker().setSpeed(0.0);
        }
    }

    @Override
    protected boolean isFinished() {
        if (state == MouthIntaker.IntakeState.INTAKING) {
            LOG.info(getName() + " isFinished");
            return Robot.claw.isCargoInRobot();
        }
        return false;
    }

    @Override
    protected void end() {
        Robot.mouthIntaker.setState(MouthIntaker.IntakeState.IDLE);
        LOG.info(getName() + " Ended");
    }

    @Override
    protected void interrupted() {
        LOG.info(getName() + " Interrupted");
        end();
    }
}
