package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.cargo.ArticulateMouth;
import frc.robot.commands.climber.ArticulateFangs;
import frc.robot.commands.conditionals.UseElevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.Fangs;
import frc.robot.subsystems.MouthArticulator;

public class TurtleMode extends CommandGroup {
    /**
     * Stows the robot to starting position
     */
    public TurtleMode() {
        addSequential(new UseElevator(ElevatorPosition.BOTTOM));
        addParallel(new ArticulateFangs(Fangs.FangsPosition.IN_BOT));
        addSequential(new ArticulateMouth(MouthArticulator.MouthPosition.IN));
    }
}