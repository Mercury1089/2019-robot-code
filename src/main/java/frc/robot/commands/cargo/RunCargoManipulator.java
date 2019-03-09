package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.CargoManipulator;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunCargoManipulator extends Command {

  private final Logger LOG = LogManager.getLogger(RunCargoManipulator.class);
  private ShooterSpeed targetState;
  private int timeThreshold = 550;
  private long startTimeMillis;

  public RunCargoManipulator(CargoManipulator.ShooterSpeed targetState) {
    requires(Robot.cargoShooter);
    setName("RunCargoManipulator Command");
    LOG.info(getName() + " Constructed");
    this.targetState = targetState;
  }

  @Override
  protected void initialize() {
    if(targetState == ShooterSpeed.FAST_INTAKE || targetState == ShooterSpeed.SLOW_INTAKE) {
      Robot.cargoShooter.setIntaking(true);
      Robot.cargoShooter.setEjecting(false);
    }
    else if(targetState == ShooterSpeed.FAST_EJECT || targetState == ShooterSpeed.SLOW_EJECT) {
      Robot.cargoShooter.setEjecting(true);
      Robot.cargoShooter.setIntaking(false);
    }
    startTimeMillis = System.currentTimeMillis();
    LOG.info(getName() + " Initialized");
  }

  @Override
  protected void execute() {
    Robot.cargoShooter.setClawState(targetState);
    LOG.info(getName() + " Executed");
  }

  @Override
  protected boolean isFinished() {
    if (targetState == ShooterSpeed.FAST_INTAKE || targetState == ShooterSpeed.SLOW_INTAKE) {
      return Robot.cargoShooter.getLidar().getRawDistance() <= CargoManipulator.CARGO_IN_ROBOT_THRESH;
    }
    return System.currentTimeMillis() - startTimeMillis > timeThreshold;
  }

  @Override
  protected void end() {
    Robot.cargoShooter.setClawState(ShooterSpeed.STOP);
    Robot.cargoShooter.setIntaking(false);
    Robot.cargoShooter.setEjecting(false);
    LOG.info(getName() + " Ended");
  }

  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
