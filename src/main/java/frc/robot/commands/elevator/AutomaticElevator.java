package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.robot.util.DelayableLogger;
import java.util.concurrent.TimeUnit;


public class AutomaticElevator extends Command {

  private final Logger LOG = LogManager.getLogger(AutomaticElevator.class);
  private final DelayableLogger SLOW_LOG = new DelayableLogger(LOG, 1, TimeUnit.SECONDS);
  private final int ELEVATOR_THRESHOLD = 3000;

  private ElevatorPosition targetPos;

  private boolean endable;

  public AutomaticElevator(ElevatorPosition pos) {
    requires(Robot.elevator);
    targetPos = pos;
    endable = false;

    setName("UseElevator (" + pos + ")");
    LOG.info(getName() + " Constructed");
  }

  public AutomaticElevator(ElevatorPosition pos, boolean endable) {
    this(pos);
    endable = true;
  }

  @Override
  protected void initialize() {
    Robot.elevator.setPosition(targetPos); //line switched with next
    Robot.elevator.getElevatorLeader().setPosition(targetPos.encPos);

    LOG.info(getName() + " initialized");
  }

  @Override
  protected void execute() {
    SLOW_LOG.run(log -> log.debug(getName() + " executing"));
  }

  @Override
  protected boolean isFinished() {
    if (endable && ELEVATOR_THRESHOLD >= Math.abs(targetPos.encPos - Robot.elevator.getCurrentHeight())) {
      LOG.info("Reached " + targetPos.toString());
      return true;
    }
    if (targetPos == Elevator.ElevatorPosition.BOTTOM) {
      if (Robot.elevator.isLimitSwitchClosed()) {
        Robot.elevator.getElevatorLeader().setPosition(targetPos.encPos);
        LOG.info("Reached!");
        return true;
      }
    }
    return false;
  }

  @Override
  protected void end() {
    LOG.info(getName() + "elevator ended");
  }

  @Override
  protected void interrupted() {
    LOG.info(getName() + " interrupted");
  }
}
