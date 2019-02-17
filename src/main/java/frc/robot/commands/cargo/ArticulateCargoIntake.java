/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArticulateCargoIntake extends Command {
  private final Logger LOG = LogManager.getLogger(ArticulateCargoIntake.class);
  ArticulationPosition targetState;

  public ArticulateCargoIntake(ArticulationPosition targetState) {
    requires(Robot.cargoIntake);
    setName("ArticulateCargoIntake Command");
    LOG.info(getName() + " Constructed");
    this.targetState = targetState;
  }

  @Override
  protected void initialize() {

  }

  /**
   * TODO Check the values for the speeds to make sure that the motors are running
   * the right direction. Also, make constants for input/output directions
   */
  @Override
  protected void execute() {
    Robot.cargoIntake.setArticulatorSpeed(targetState == ArticulationPosition.IN ? -1.0 : 1.0);
    LOG.info(getName() + " Executed");
  }

  /**
   * TODO Check the reverse/forward limit switch configuration to see if
   * this is activated correctly and we don't decimate the robot
   */
  @Override
  protected boolean isFinished() {
    if(targetState == ArticulationPosition.IN &&
      Robot.cargoIntake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE)) {
        LOG.info(getName() + " isFinished");
        return true;
    }
    else if(targetState == ArticulationPosition.OUT &&
      Robot.cargoIntake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.FORWARD)) {
        LOG.info(getName() + " isFinished");
        return true;
    }
    LOG.info(getName() + " isFinished");
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.cargoIntake.setArticulatorState(targetState);
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
    this.end();
  }

  /**
   * HMMMM Is this number even?
   * @param num
   * @return
   */
  public boolean isEven(int num) {
    return (num & 1) == 0;
  }
}
