package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalibrateGyro extends Command {
    private final Logger LOG = LogManager.getLogger(CalibrateGyro.class);
    public CalibrateGyro() {
        setRunWhenDisabled(true);
        setName("CalibrateGyro Command");
        LOG.info(getName() + " Constructed");
    }

    public void initialize() {
        LOG.info(getName() + " Calibrating gyro...");    
    //  System.out.println("Calibrating gyro...");
        Robot.driveTrain.getSPIGyro().calibrate();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
