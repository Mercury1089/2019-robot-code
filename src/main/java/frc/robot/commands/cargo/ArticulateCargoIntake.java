package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArticulateCargoIntake extends Command {

  private final Logger LOG = LogManager.getLogger(ArticulateCargoIntake.class);
  ArticulationPosition targetState;

  public ArticulateCargoIntake(ArticulationPosition targetState) {
    requires(Robot.cargoIntake);
    setName("ArticulateCargoIntake Command");
    LOG.info(getName() + " Constructed");
    this.targetState = targetState;
  }

  @Override
  protected void initialize() {
    Robot.cargoIntake.getArticulator().setPosition(targetState.getTicks());
  }

  @Override
  protected boolean isFinished() {
    /**
    if(targetState == ArticulationPosition.IN &&
      Robot.cargoIntake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE)) { //Check if reverse limit switch
        LOG.info(getName() + " isFinished");
        return true;
    }
    */
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

  /**
   * HMMMM Is this number even?
   * @param num
   * @return true if num is even, false otherwise
   */
  public boolean isEven(int num) {
    return (num & 1) == 0;
  }
}
