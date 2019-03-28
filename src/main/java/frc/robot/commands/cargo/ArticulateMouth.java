package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.MouthArticulator;
import frc.robot.subsystems.MouthArticulator.MouthPosition;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArticulateMouth extends Command {

    private final Logger LOG = LogManager.getLogger(ArticulateMouth.class);
    private MouthPosition position;
    private long startTime;
    private final static long timeout = 500L;

    public ArticulateMouth(MouthPosition position) {
        requires(Robot.mouthArticulator);
        setName("ArticulateMouth Command");
        LOG.info(getName() + " Constructed");
        this.position = position;
    }

    @Override
    protected void initialize() {
        Robot.mouthArticulator.setArticulatorPosition(position);
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected boolean isFinished() {
        return System.currentTimeMillis() - startTime > timeout;
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
