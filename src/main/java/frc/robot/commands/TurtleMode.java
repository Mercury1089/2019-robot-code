package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateMouth;
import frc.robot.commands.climber.ArticulateFangs;
import frc.robot.commands.conditionals.UseElevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.MouthArticulator.MouthPosition;
import frc.robot.subsystems.Fangs;

public class TurtleMode extends CommandGroup {
    /**
     * Stows the robot to starting position
     */
    public TurtleMode() {
        addParallel(new ArticulateFangs(Fangs.FangsPosition.IN_BOT));
        addSequential(new UseElevator(ElevatorPosition.BOTTOM));
        addSequential(new ArticulateMouth(MouthPosition.IN));
    }
}
