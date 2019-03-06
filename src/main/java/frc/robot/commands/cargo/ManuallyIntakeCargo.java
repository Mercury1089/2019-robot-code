package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.CargoIntake.IntakeSpeed;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;

/**
 * Seems useless, remove?
 */
@Deprecated
public class ManuallyIntakeCargo extends CommandGroup {
  
  public ManuallyIntakeCargo() {
    addParallel(new RunCargoIntake(IntakeSpeed.FAST_IN));
    addSequential(new RunCargoManipulator(ShooterSpeed.FAST_INTAKE));
  }
}
