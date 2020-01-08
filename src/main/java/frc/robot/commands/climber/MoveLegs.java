package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Legs.LegsPosition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveLegs extends Command {
  private final Logger LOG = LogManager.getLogger(MoveLegs.class);
  private LegsPosition position; 

  public MoveLegs(LegsPosition position) {
    requires(Robot.legs);
    setName("ActuateFangs Command" + position.toString());
    LOG.info(getName() + " Constructed");

    this.position = position;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    LOG.info(getName() + " Initialized");
    Robot.legs.actuateDoubleSolenoid(position);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    LOG.info(getName() + " Executed");
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}