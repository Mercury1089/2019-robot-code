package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.hatchpanel.ArticulateForks;
import frc.robot.commands.hatchpanel.RunStinger;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.PIDGain;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Subsystem to intake and eject hatch panels
 * articulator is the intake mechanism
 */
public class Forks extends Subsystem {
    public final int ARTICULATION_PID_SLOT = 0;

    public final PIDGain articulationGain = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);

    public final int ARTICULATION_THRESHOLD = 100;

    private IMercMotorController articulator;

    public Forks() {
        articulator = new MercTalonSRX(RobotMap.CAN.HATCH_INTAKE);

        articulator.resetEncoder();

        articulator.setPosition(ForksPosition.IN_BOT.getEncTicks());

        articulator.configPID(ARTICULATION_PID_SLOT, articulationGain);

        articulator.configAllowableClosedLoopError(ARTICULATION_PID_SLOT, ARTICULATION_THRESHOLD);

        articulator.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0);
        articulator.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new ArticulateForks(ForksPosition.IN_BOT));
    }

    public void setForksSpeed(double speed) {
        articulator.setSpeed(speed);
    }

    public IMercMotorController getArticulator() {
        return articulator;
    }

    public enum ForksPosition {
        //Temporary encoder tick values
        GROUND(0),
        READY_TO_PICK(1100),
        PERPENDICULAR(1500),
        IN_BOT(2000);

        private int encPos;

        ForksPosition(int encPos) {
            this.encPos = encPos;
        }

        public int getEncTicks() {
            return encPos;
        }
    }
}
