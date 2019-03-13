package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.interfaces.IMercMotorController;

public class HoldPosition extends Command {

    private IMercMotorController elevatorMotor;

    public HoldPosition() {
        elevatorMotor = Robot.elevator.getElevatorLeader();
    }

    @Override
    protected void initialize() {
        elevatorMotor.setPosition(elevatorMotor.getEncTicks());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
