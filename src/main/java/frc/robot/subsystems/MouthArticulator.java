package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.cargo.ManuallyArticulateMouth;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.PIDGain;
import frc.robot.util.interfaces.IMercMotorController;

public class MouthArticulator extends Subsystem {

    IMercMotorController articulator;

    public MouthArticulator() {
        articulator = new MercTalonSRX(RobotMap.CAN.CARGO_MANIPULATOR);

        articulator.configFactoryReset();

        articulator.setSensorPhase(false);

        articulator.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0);
        articulator.configClosedLoopPeakOutput(0, 0.6);
    }

    public void updateArticulatorP(double p) {
        articulator.configPID(0, new PIDGain(p, 0.0, 0.0, 0));
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new ManuallyArticulateMouth());
    }

    public IMercMotorController getArticulator() {
        return articulator;
    }

    public enum MouthPosition {
        IN(100000, 0.15),
        ANGLED45(-36300.0, 0.07),
        OUT(-45000.0, 0.07);

        private double ticks;
        private double p;

        MouthPosition(double ticks, double p) {
            this.ticks = ticks;
            this.p = p;
        }

        public double getTicks() {
            return ticks;
        }

        public double getP() {
            return p;
        }
    }
}