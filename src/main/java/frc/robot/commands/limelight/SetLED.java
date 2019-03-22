package frc.robot.commands.limelight;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.sensors.Limelight.LimelightLEDState;

/**
 * Sets the LED state of the limelight
 * This command is used to override the Pipeline's default value
 * ex: When disabled, turn off the LEDs
 */
public class SetLED extends Command {

  private LimelightLEDState state;

  public SetLED(LimelightLEDState state) {
    requires(Robot.limelightAssembly);
    this.state = state;
  }

  @Override
  protected void initialize() {
    Robot.limelightAssembly.getLimeLight().setLEDState(this.state);
  }

  @Override
  protected boolean isFinished() {
    return true;
  }
}
