/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.Claw.ClawState;
import frc.robot.subsystems.MouthIntaker.IntakeState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntakeCargo extends CommandGroup {

    private final Logger LOG = LogManager.getLogger(IntakeCargo.class);

    public IntakeCargo() {
        addParallel(new RunMouthIntake(IntakeState.INTAKING));
        addSequential(new ArticulateMouth(true));
        addSequential(new RunClaw(ClawState.INTAKING));
        addSequential(new ArticulateMouth(false));
        setName("IntakeCargo CommandGroup");
        LOG.info(getName() + " Constructed");
    }
}
