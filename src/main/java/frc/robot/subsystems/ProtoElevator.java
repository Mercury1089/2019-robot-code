/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.RobotMap;
import frc.robot.commands.ManualElevator;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.PIDGain;

/**
 * Add your docs here.
 */
public class ProtoElevator extends Subsystem {
  private static Logger log = LogManager.getLogger(ProtoElevator.class);

    private IMercMotorController elevatorLeader;
    private IMercMotorController elevatorFollower;

    public static final double NORMAL_P_VAL = 0.1;
    public static final double CLIMBING_P_VAL = 0.15;

    /**
     * Enumeration of positions that the elevator can have.
     * This is more a representation of the target positions, and does not reflect
     * the exact height of the claw at any precise moment.
     */
    public enum ElevatorPosition {
        // TODO: Temporary Values
        SCALE_HIGH(80000.0, NORMAL_P_VAL, 0, 0),        // Scale at its highest point
        SCALE_LOW(65000.0, NORMAL_P_VAL, 0, 0),         // Scale at its lowest point
        INNER_STAGE(38000.0, NORMAL_P_VAL, 0, 0),       // Height of the inner stage
        SWITCH(25000.0, NORMAL_P_VAL, 0, 0),            // Above switch fence
        CUBE_LEVEL_3(25000, NORMAL_P_VAL, 0, 0),        // Top cube of three stacked
        CUBE_LEVEL_2(14000.0, NORMAL_P_VAL, 0, 0),      // Top cube of two stacked
        DRIVE_CUBE(7000.0, NORMAL_P_VAL, 0, 0),         // Height for driving around cube
        CLIMB(5000.0, CLIMBING_P_VAL, 0, 0),            // Position to raise to when climbing
        FLOOR(-2000.0, NORMAL_P_VAL, 0, 0);             // Elevator bottomed out

        public final double encPos;
        public final double pVal;
        public final double iVal;
        public final double dVal;

        /**
         * Creates an elevator position, storing the encoder ticks
         * representing the height that the elevator should be at,
         * as well as the P value to use to reach that level.
         *
         * @param ep encoder position, in ticks
         * @param kp p value between 0 and 1
         */
        ElevatorPosition(double ep, double kp, double ki, double kd) {
            encPos = ep;
            pVal = kp;
            iVal = ki;
            dVal = kd;
        }
    }

    private ElevatorPosition position;

    public static final double MAX_HEIGHT = ElevatorPosition.SCALE_HIGH.encPos;

    /**
     * Creates a new elevator, using the specified CAN IDs for the
     * leader controller (Talon SRX) and follower controller (Victor SPX).

     *
     * @param talonID  Leader (Talon SRX) CAN ID
     * @param victorID Follower (Victor SPX) CAN ID
     */
    public ProtoElevator(int talonID, int victorID) {
        elevatorLeader = new MercTalonSRX(talonID);
        elevatorLeader.setNeutralMode(NeutralMode.Brake);
        elevatorFollower = new MercVictorSPX(victorID);
        elevatorFollower.setNeutralMode(NeutralMode.Brake);

        elevatorFollower.follow(elevatorLeader);

        elevatorLeader.setSensorPhase(false);
        elevatorLeader.configVoltage(0.125, 1.0);
        elevatorLeader.configAllowableClosedLoopError(0, 5, 10);
        elevatorLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PID.PRIMARY_PID_LOOP, DriveTrain.TIMEOUT_MS);
        elevatorLeader.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, 10);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ManualElevator());
    }

    public IMercMotorController getElevatorLeader() {
        return elevatorLeader;
    }

    public boolean isLimitSwitchClosed() {
        return elevatorLeader.isLimitSwitchClosed();
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
      elevatorLeader.configPID(0, new PIDGain(ep.pVal, ep.iVal, ep.dVal, 0));

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
