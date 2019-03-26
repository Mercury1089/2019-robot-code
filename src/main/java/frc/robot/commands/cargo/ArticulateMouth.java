package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.MouthArticulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArticulateMouth extends Command {

    private final Logger LOG = LogManager.getLogger(ArticulateMouth.class);
    private MouthArticulator.MouthPosition targetState;

    public ArticulateMouth(MouthArticulator.MouthPosition targetState) {
        requires(Robot.mouthArticulator);
        setName("ArticulateMouth Command");
        LOG.info(getName() + " Constructed");
        this.targetState = targetState;
    }

    @Override
    protected void initialize() {
        Robot.mouthArticulator.updateArticulatorP(targetState.getP());
        Robot.mouthArticulator.getArticulator().setPosition(targetState.getTicks());
    }

    @Override
    protected boolean isFinished() {
        /*
         if(targetState == ArticulationPosition.IN &&
         Robot.mouth.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE)) { //Check if reverse limit switch
         LOG.info(getName() + " isFinished");
         return true;
         }
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
        this.end();
    }

    /**
     * HMMMM Is this number even?
     *
     * @param num
     * @return true if num is even, false otherwise
     */
    public boolean isEven(int num) {
        return (num & 1) == 0;
    }
}
