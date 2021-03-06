package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Fangs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Moves the Fangs
 */
public class ArticulateFangs extends Command {
    private final Logger LOG = LogManager.getLogger(ArticulateFangs.class);
    private final int POSITION_THRESHOLD = 500;
    private Fangs.FangsPosition state;
    private boolean endable = false;

    public ArticulateFangs(Fangs.FangsPosition state) {
        requires(Robot.fangs);
        setName("ArticulateForks Command");
        LOG.info(getName() + " Constructed");
        this.state = state;
    }

    public ArticulateFangs(Fangs.FangsPosition state, boolean endable) {
        this(state);
        this.endable = endable;
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        Robot.fangs.getArticulator().setPosition(state.getEncTicks());
        LOG.info(getName() + " Initialized");
    }

    @Override
    protected boolean isFinished() {
        if (endable && POSITION_THRESHOLD >= Math.abs(state.getEncTicks() - Robot.fangs.getArticulator().getEncTicks())) {
            LOG.info("Reached " + state.toString());
            return true;
        }
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
