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
    Robot.cargoIntake.getArticulator().setPosition(targetState.getTicks());
  }

  @Override
  protected boolean isFinished() {
    if(targetState == ArticulationPosition.IN &&
      Robot.cargoIntake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE)) { //Check if reverse limit switch
        LOG.info(getName() + " isFinished");
        return true;
    }
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
