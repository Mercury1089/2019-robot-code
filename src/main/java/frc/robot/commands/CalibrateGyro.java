package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CalibrateGyro extends Command {

    public CalibrateGyro() {
        setRunWhenDisabled(true);
    }

    public void initialize() {
        System.out.println("Calibrating gyro...");
        Robot.driveTrain.getGyro().calibrate();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
