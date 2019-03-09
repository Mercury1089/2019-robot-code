package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.CargoIntake;
import frc.robot.subsystems.CargoManipulator;
import frc.robot.subsystems.CargoIntake.IntakeSpeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunCargoIntake extends Command {

  private final Logger LOG = LogManager.getLogger(RunCargoIntake.class);
  private IntakeSpeed intakeSpeed;

  public RunCargoIntake(IntakeSpeed intakeSpeed) {
    requires(Robot.cargoIntake);
    setName("RunCargoIntake Command");
    LOG.info(getName() + " Constructed");
    this.intakeSpeed = intakeSpeed;
  }

  @Override
  protected void initialize() {
    Robot.cargoIntake.setIntakeSpeed(intakeSpeed);
    LOG.info(getName() + " Initialized");
  }

  @Override
  protected boolean isFinished() {
    if (intakeSpeed == CargoIntake.IntakeSpeed.FAST_IN || intakeSpeed == CargoIntake.IntakeSpeed.SLOW_IN) {
      LOG.info(getName() + " isFinished");
      return Robot.driveTrain.getLidar().getDistance() <= CargoManipulator.CARGO_IN_ROBOT_THRESH;
    }
    return false;
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
