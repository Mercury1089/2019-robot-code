package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap.GAMEPAD_AXIS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manual control for the hatch panel intake
 */
public class ManualActuateForks extends Command {
  private final Logger LOG = LogManager.getLogger(ManualActuateForks.class);
  public ManualActuateForks() {
    requires(Robot.hatchManipulator);
    setName("ManualActuateForks Command");
    LOG.info(getName() + " Constructed");
  }

  /**
   * Rotate the hatch panel intake in accordance with the gamepad axis
   */
  @Override
  protected void execute() {
    Robot.hatchManipulator.setArticulatorSpeed(Robot.oi.getGamepadAxis(GAMEPAD_AXIS.rightY));
    LOG.info(getName() + " Executed");
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
  }

  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
    this.end();
  }
}
