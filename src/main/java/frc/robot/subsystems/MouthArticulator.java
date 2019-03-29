package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.cargo.ArticulateMouth;

public class MouthArticulator extends Subsystem {

    private DoubleSolenoid articulator;

    public MouthArticulator() {
        articulator = new DoubleSolenoid(RobotMap.CAN.PCM, RobotMap.PCM.MOUTH_ACTUATE, RobotMap.PCM.MOUTH_RETRACT);
    }

    @Override
    public void initDefaultCommand() {
        //setDefaultCommand(new ArticulateMouth(MouthPosition.OUT));
    }

    public void setArticulatorPosition(MouthPosition position) {
        switch(position) {
            case OUT:
                articulator.set(DoubleSolenoid.Value.kForward);
                break;
            case IN:
                articulator.set(DoubleSolenoid.Value.kReverse);
                break;
        }
    }

    public enum MouthPosition {
        IN,
        OUT;
    }
}