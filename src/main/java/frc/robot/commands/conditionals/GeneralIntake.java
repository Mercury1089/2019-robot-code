package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.cargo.IntakeCargo;
import frc.robot.commands.hatchpanel.AcquireHatchPanel;
import frc.robot.util.DriveAssist.DriveDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Intakes a game piece (cargo or hatch panel)
 * depending on which way we are driving
 */
public class GeneralIntake extends ConditionalCommand {

    private final Logger LOG = LogManager.getLogger(GeneralIntake.class);

    public GeneralIntake() {
        super(new AcquireHatchPanel(), new IntakeCargo());
        setName("GeneralIntake ConditionalCommand");
        LOG.info(getName() + " Constructed");
    }

    @Override
    public boolean condition() {
        return Robot.driveTrain.getDirection() == DriveDirection.HATCH;
    }
}
