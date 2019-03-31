package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.elevator.HoldPosition;
import frc.robot.util.MercMath;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.PIDGain;
import frc.robot.util.interfaces.IMercMotorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Elevator extends Subsystem {

    public static final double NORMAL_P_VAL = 0.18;
    public static final int PRIMARY_PID_LOOP = 0;
    public static final int MAX_ELEV_RPM = 18000;

    private static Logger log = LogManager.getLogger(Elevator.class);

    private IMercMotorController elevatorLeader;

    public Elevator() {
        elevatorLeader = new MercTalonSRX(CAN.ELEVATOR_TALON);
        elevatorLeader.setNeutralMode(NeutralMode.Brake);

        elevatorLeader.configMotionAcceleration((int)(MercMath.revsPerMinuteToTicksPerTenth(18000 * 2)));
        elevatorLeader.configMotionCruiseVelocity((int) MercMath.revsPerMinuteToTicksPerTenth(MAX_ELEV_RPM));

        elevatorLeader.setSensorPhase(false);
        elevatorLeader.setInverted(true);
        elevatorLeader.configVoltage(0.125, 1.0);
        elevatorLeader.configClosedLoopPeriod(0, 1);
        elevatorLeader.configAllowableClosedLoopError(0, 5);
        elevatorLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PID.PRIMARY_PID_LOOP);
        elevatorLeader.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0);
        elevatorLeader.setForwardSoftLimit((int) ElevatorPosition.MAX_HEIGHT.encPos);
        elevatorLeader.enableForwardSoftLimit();

        elevatorLeader.configPID(Elevator.PRIMARY_PID_LOOP, new PIDGain(NORMAL_P_VAL, 0.00005, 0.0, MercMath.calculateFeedForward(MAX_ELEV_RPM)));
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new HoldPosition());
    }

    public IMercMotorController getElevatorLeader() {
        return elevatorLeader;
    }

    /**
     * Get current height of claw on elevator.
     *
     * @return height of claw as read by the encoder, in ticks
     */
    public double getCurrentHeight() {
        return elevatorLeader.getEncTicks();
    }

    /**
     * Enumeration of positions that the elevator can have.
     * This is more a representation of the target positions, and does not reflect
     * the exact height of the claw at any precise moment.
     */
    public enum ElevatorPosition {
        MAX_HEIGHT(920000.0),   // TEST THIS
        ROCKET_3_C(914641.0),    // 3st level Rocket: Cargo
        ROCKET_2_C(551295.0),    // 2rd level Rocket: Cargo
        ROCKET_1_C(179359.0),    // 1nd level Rocket: Cargo
        ROCKET_3_HP(723000.0),   // 3th level Rocket: Hatch Panel
        ROCKET_2_HP(355720.0),   // 2st level Rocket: Hatch Panel
        CARGOSHIP_C(380000.0),   // Cargo ship: Cargo
        DANGER_LINE(150000.0),   // Line where Claw is in danger of hitting the Mouth
        //3062
        BOTTOM(-5000.0);         // Elevator bottom, can do hatchpanels at: loading station, rocket level 1, and cargo ship

        public final double encPos;

        /**
         * Creates an elevator position, storing the encoder ticks
         * representing the height that the elevator should be at,
         * as well as the P value to use to reach that level.
         *
         * @param ep encoder position, in ticks
         */
        ElevatorPosition(double ep) {
            encPos = ep;
        }
    }
}
