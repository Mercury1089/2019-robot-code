package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Spins the HatchPanel ejector one rotation to eject a hatch panel
 */
public class EjectHatchPanel extends Command {

  private final Logger LOG = LogManager.getLogger(EjectHatchPanel.class);
  private int targetPosition = 102000; //Make this a constant somewhere

  private int onTargetCount;
  private final int ON_TARGET_MIN_COUNT = 5;

  public EjectHatchPanel() {
    requires(Robot.hatchManipulator);
    setName("EjectHatchPanel Command");
    LOG.info(getName() + " Constructed");

    onTargetCount = 0;
  }

  public EjectHatchPanel(int targetPos) {
    this();
    targetPosition = targetPos;
  } 

  @Override
  protected void initialize() {
    Robot.hatchManipulator.getEjector().resetEncoder();
    Robot.hatchManipulator.getEjector().setPosition(targetPosition);
    LOG.info(getName() + " Initialized");
  }

  @Override
  protected boolean isFinished() {
    double error = Robot.hatchManipulator.getEjector().getClosedLoopError();
    boolean isFinished = false;
    boolean isOnTarget = (Math.abs(error) < Robot.hatchManipulator.EJECTOR_THRESHOLD);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > ON_TARGET_MIN_COUNT) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
  }

  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
  }

  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
