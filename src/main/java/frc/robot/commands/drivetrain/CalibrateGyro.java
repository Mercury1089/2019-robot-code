package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CalibrateGyro extends Command {

    public CalibrateGyro() {
        setRunWhenDisabled(true);
    }

    public void initialize() {
        System.out.println("Calibrating gyro...");
        Robot.driveTrain.getSPIGyro().calibrate();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
