package frc.robot.commands.limelight;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.LimelightAssembly.LimelightPosition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Rotate the limelight assembly to a specified position
 */
public class RotateLimelight extends Command {

  private final Logger LOG = LogManager.getLogger(RotateLimelight.class);
  private LimelightPosition limePos;
  private double position;

  /**
   * Rotate the limelight to the specified servo position.
   * 
   * The position is given as a double from 0.0 to 1.0, where 0.0 is 0 degrees and 
   * 1.0 is 180 degrees
   * 
   * @param pos The position, from 0.0 to 1.0, to rotate the limelight to
   */
  public RotateLimelight(double pos) {
    requires(Robot.limelightAssembly);
    setName("Rotate Limelight Command");
    LOG.info(getName() + " Constructed");
    this.position = pos;
  }

  /**
   * Rotate the limight to the pre-specified servo position,
   * and switch the pipeline of the limelight to that of the correct side
   * @param pos The preset position to rotate the limight to
   */
  public RotateLimelight(LimelightPosition pos) {
    this(pos.servoPosition);
    limePos = pos;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    LOG.info(getName() + " Initialized");
    Robot.limelightAssembly.setServoPosition(position);
    Robot.limelightAssembly.getLimeLight().setPipeline(limePos);
  }

  /**
   * Always false, this command should always be active
   */
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
  }
}
