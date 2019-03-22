package frc.robot.commands.limelight;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.util.MercMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manually rotate the limelight based on a gamepad joystick
 */
public class ManualLimelightRotation extends Command {

  private final Logger LOG = LogManager.getLogger(ManualLimelightRotation.class);

  public ManualLimelightRotation() {
    requires(Robot.limelightAssembly);
    setName("ManualLimelightRotation Command");
    LOG.info(getName() + " Constructed");
  }

  /**
   * Use the position of gamepad joystick to rotate the limelight
   */
  @Override
  protected void execute() {
    Servo servo = Robot.limelightAssembly.getServo();
    servo.setPosition(MercMath.clamp(servo.get() + MercMath.applyDeadzone(Robot.oi.getJoystickX(RobotMap.DS_USB.GAMEPAD), 0.1), 0.0, 1.0));
    LOG.info(getName() + " Executed");
  }

  /**
   *  Always return false; When we enable manual mode we have to re-enable automatic mode
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
