package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class ManuallyArticulateMouth extends Command {
    public ManuallyArticulateMouth() {
        requires(Robot.mouthArticulator);
    }

    @Override
    protected void execute() {
        Robot.mouthArticulator.getArticulator().setSpeed(-Robot.oi.getGamepadAxis(RobotMap.GAMEPAD_AXIS.leftY) * 0.2);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
