package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManualStingerControl extends Command {
    private final Logger LOG = LogManager.getLogger(ManualStingerControl.class);

    public ManualStingerControl() {
        requires(Robot.stinger);
        setName("ManualStingerControl Command");
        LOG.info(getName() + " Constructed");
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
        Robot.stinger.getEjector().setSpeed(Robot.oi.getGamepadAxis(RobotMap.GAMEPAD_AXIS.leftY));
        LOG.info(getName() + " Executed");
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Robot.stinger.getEjector().stop();
        LOG.info(getName() + " Ended");
    }

    @Override
    protected void interrupted() {
        LOG.info(getName() + " Interrupted");
        this.end();
    }
}
