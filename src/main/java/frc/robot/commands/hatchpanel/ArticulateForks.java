package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Forks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Moves the Hatch Panel intake
 */
public class ArticulateForks extends Command {
    private final Logger LOG = LogManager.getLogger(ArticulateForks.class);
    private final int POSITION_THRESHOLD = 500;
    private Forks.ForksPosition state;
    private boolean endable = false;

    public ArticulateForks(Forks.ForksPosition state) {
        requires(Robot.forks);
        setName("ArticulateForks Command");
        LOG.info(getName() + " Constructed");
        this.state = state;
    }

    public ArticulateForks(Forks.ForksPosition state, boolean endable) {
        this(state);
        this.endable = endable;
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        Robot.forks.getArticulator().setPosition(state.getEncTicks());
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected boolean isFinished() {
        if (endable && POSITION_THRESHOLD >= Math.abs(state.getEncTicks() - Robot.forks.getArticulator().getEncTicks())) {
            LOG.info("Reached " + state.toString());
            return true;
        }
        //The below code ends the command if the ArticulatorPosition is InBot
        //We do not want the command to end normally so it's commented out for the moment
        /**
         * if (state == ArticulatorPosition.IN_BOT) {
         *   if (Robot.stinger.isArticulatorLimitSwitchClosedReverse() || Robot.stinger.isArticulatorLimitSwitchClosedForward()) {
         *     Robot.stinger.getArticulator().setPosition(state.encPos);
         *     LOG.info("Reached!");
         *     return true;
         *   }
         * }
         */
        return false;
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
