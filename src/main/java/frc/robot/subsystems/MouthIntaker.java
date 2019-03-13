package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.cargo.ManuallyArticulateMouth;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;

public class MouthIntaker extends Subsystem {

    IMercMotorController intaker;

    IntakeState state;

    public MouthIntaker() {
        intaker = new MercVictorSPX(CAN.CARGO_INTAKE);

        intaker.configFactoryReset();

        state = IntakeState.IDLE;
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new ManuallyArticulateMouth());
    }

    public IMercMotorController getIntaker() {
        return intaker;
    }

    public void setState(IntakeState state) {
        this.state = state;
    }

    public enum IntakeState {
        INTAKING,
        EJECTING,
        IDLE
    }
}