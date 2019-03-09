package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.elevator.ManualElevator;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;
import frc.robot.util.PIDGain;

//FORWARD IS DOWN
public class Elevator extends Subsystem {
  private static Logger log = LogManager.getLogger(Elevator.class);

    private IMercMotorController elevatorLeader;

    public static final double NORMAL_P_VAL = 0.1;
    public static final double CLIMBING_P_VAL = 0.15;

    public static final int PRIMARY_PID_LOOP = 0;

    /**
     * Enumeration of positions that the elevator can have.
     * This is more a representation of the target positions, and does not reflect
     * the exact height of the claw at any precise moment.
     */
    public enum ElevatorPosition {
        // TODO: Temporary Values
        ROCKET_3_C(80000.0),    // 3st level Rocket: Cargo
        ROCKET_2_C(65000.0),    // 2rd level Rocket: Cargo
        ROCKET_1_C(38000.0),    // 1nd level Rocket: Cargo
        ROCKET_3_HP(25000.0),   // 3th level Rocket: Hatch Panel
        ROCKET_2_HP(25000.0),   // 2st level Rocket: Hatch Panel
        CARGOSHIP_C(7000.0),    // Cargo ship: Cargo
        BOTTOM(-2000.0);        // Elavator bottom, can do hatchpanel at loading station, rocket level 1, and cargo ship

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

    private ElevatorPosition position;

    public static final double MAX_HEIGHT = ElevatorPosition.ROCKET_3_C.encPos;

    /**
     * Creates a new elevator, using the specified CAN IDs for the
     * leader controller (Talon SRX) and follower controller (Victor SPX).

     *
     * @param talonID  Leader (Talon SRX) CAN ID
     * @param victorID Follower (Victor SPX) CAN ID
     */
    public Elevator() {
        elevatorLeader = new MercTalonSRX(CAN.ELEVATOR_TALON);
        elevatorLeader.setNeutralMode(NeutralMode.Brake);

        elevatorLeader.setSensorPhase(false);
        elevatorLeader.configVoltage(0.125, 1.0);
        elevatorLeader.configAllowableClosedLoopError(0, 5);
        elevatorLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PID.PRIMARY_PID_LOOP);
        elevatorLeader.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0);
        elevatorLeader.setReverseSoftLimit(10000); //TODO <-- Arbitrary
        elevatorLeader.enableForwardSoftLimit();

        elevatorLeader.configPID(Elevator.PRIMARY_PID_LOOP, new PIDGain(NORMAL_P_VAL, 0.0, 0.0, 0.0));
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ManualElevator());
    }

    public IMercMotorController getElevatorLeader() {
        return elevatorLeader;
    }

    public boolean isLimitSwitchClosed() {
        return elevatorLeader.isLimitSwitchClosed(LimitSwitchDirection.FORWARD);
    }

    /**
     * Gets the current {@link ElevatorPosition} for the elevator.
     *
     * @return the current ElevatorPosition
     */
    public ElevatorPosition getPosition() {
        return position;
    }

    /**
     * Sets the {@link ElevatorPosition} for the elevator.
     *
     * @param ep the new ElevatorPosition to set
     * @param pid the pid values
     */
    public void setPosition(ElevatorPosition ep) {
      position = ep;
    }

    /**
     * Get current height of claw on elevator.
     *
     * @return height of claw as read by the encoder, in ticks
     */
    public double getCurrentHeight() {
        return elevatorLeader.getEncTicks(); 
    }
}
