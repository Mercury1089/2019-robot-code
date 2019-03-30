/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.elevator.ManualElevator;
import frc.robot.commands.hatchpanel.ManualStingerControl;
import frc.robot.commands.hatchpanel.RunStinger;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.PIDGain;
import frc.robot.util.interfaces.IMercMotorController;

/**
 * Subsystem to eject hatch panels
 */
public class Stinger extends Subsystem {

    public final int EJECTOR_PID_SLOT = 0;
    public final PIDGain ejectorGain = new PIDGain(0.1, 0.0, 0.0, 0.0, 0.75);

    public final int EJECTOR_THRESHOLD = 500;

    private IMercMotorController ejector;

    private StingerState state;

    public Stinger() {
        ejector = new MercTalonSRX(CAN.HATCH_EJECTOR);

        ejector.resetEncoder();

        ejector.setPosition(0);

        ejector.configPID(EJECTOR_PID_SLOT, ejectorGain);

        ejector.configAllowableClosedLoopError(EJECTOR_PID_SLOT, EJECTOR_THRESHOLD);
    }

    public void initDefaultCommand() {
        //setDefaultCommand(new ManualStingerControl());
    }

    public StingerState getState() {
        return state;
    }

    public void setState(StingerState state) {
        this.state = state;
    }

    public IMercMotorController getEjector() {
        return ejector;
    }

    public enum StingerState {
        EJECTING,
        IDLE
    }
}