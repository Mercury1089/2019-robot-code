package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.HatchManipulator.HatchArticulatorPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.commands.climber.ActuateForks;

/**
 * Command group that controls the hatch intake. The hatch intake will go down to the floor
 * and then move up slightly to allow the driver to see if they have the hatch panel
 * or not.
 */
public class AcquireHatchPanel extends CommandGroup {
  
  private final Logger LOG = LogManager.getLogger(AcquireHatchPanel.class);
  
  /**
   * Moves the Hatch Panel Pickup to the floor and then up slightly
   */
  public AcquireHatchPanel() {
    addSequential(new ActuateForks(HatchArticulatorPosition.ACQUIRE, true));
    addSequential(new ActuateForks(HatchArticulatorPosition.READY_TO_PICK));
    setName("AcquireHatchPanel CommandGroup");
    LOG.info(getName() + " Constructed");
  }
}
