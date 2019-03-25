package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.RobotMap.PCM;

public class Legs extends Subsystem {
  private DoubleSolenoid legs;
  private boolean isInLiftMode;

  public Legs(){
    isInLiftMode = false;
    legs = new DoubleSolenoid(CAN.PCM, PCM.HAB_ACTUATE, PCM.HAB_RETRACT);
  }

  @Override
  public void initDefaultCommand() {
  }

  public boolean isInLiftMode(){
    return isInLiftMode;
  }

  public void actuateDoubleSolenoid(boolean actuate){
    if(actuate) {
      legs.set(DoubleSolenoid.Value.kForward);
    } else {
      legs.set(DoubleSolenoid.Value.kReverse);
    }
    isInLiftMode = actuate;
  }
}
