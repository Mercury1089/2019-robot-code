package frc.robot.commands.conditionals;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.robot.Robot;
import frc.robot.commands.cargo.RunClaw;
import frc.robot.commands.hatchpanel.RunStinger;
import frc.robot.subsystems.Claw;
import frc.robot.util.DriveAssist.DriveDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ejects a game piece (cargo or hatch panel)
 * depending on which way we are driving
 */
public class GeneralEject extends ConditionalCommand {

    private final Logger LOG = LogManager.getLogger(GeneralEject.class);

    public GeneralEject() {
        super(new RunStinger(), new RunClaw(Claw.ClawState.EJECTING));
        setName("GeneralEject ConditionalCommand");
        LOG.info(getName() + " Constructed");
    }

    @Override
    public boolean condition() {
        return Robot.driveTrain.getDirection() == DriveDirection.HATCH;
    }
}
