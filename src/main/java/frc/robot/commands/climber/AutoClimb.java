
package frc.robot.commands.climber;


import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.drivetrain.DriveDistance;
import frc.robot.commands.climber.MoveLegs;
import frc.robot.commands.climber.ArticulateFangs;
import frc.robot.commands.climber.HoldFangPosition;
import frc.robot.subsystems.Fangs.FangsPosition;
import frc.robot.subsystems.Legs.LegsPosition;


public class AutoClimb extends CommandGroup {
    public AutoClimb() {
        addSequential(new DriveDistance(10)); // Dummy value
        addSequential(new MoveLegs(LegsPosition.OUT));
        addSequential(new DriveDistance(10)); // Dummy value
        addSequential(new ArticulateFangs(FangsPosition.DOWN));
        addSequential(new MoveLegs(LegsPosition.IN));
        addSequential(new DriveDistance(10)); // Dummy value
        addSequential(new ArticulateFangs(FangsPosition.IN_BOT));
    }

}