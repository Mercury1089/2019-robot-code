package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.util.DriveAssist.DriveDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Raises the elevator to the correct 2nd level rocket (cargo or hatch panel)
 * depending on which way we are driving
 */
public class ConditionalLevel2Elevator extends ConditionalCommand {

    private final Logger LOG = LogManager.getLogger(GeneralEject.class);

    public ConditionalLevel2Elevator() {
        super(new UseElevator(ElevatorPosition.ROCKET_2_HP), new UseElevator(ElevatorPosition.ROCKET_2_C));
        setName("GeneralEject ConditionalCommand");
        LOG.info(getName() + " Constructed");
    }

    @Override
    public boolean condition() {
        return Robot.driveTrain.getDirection() == DriveDirection.HATCH;
    }
}
