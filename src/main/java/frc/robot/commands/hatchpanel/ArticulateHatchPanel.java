/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.HatchManipulator.ArticulatorPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArticulateHatchPanel extends Command {
  private final Logger LOG = LogManager.getLogger(ArticulateHatchPanel.class);
  private ArticulatorPosition state;
  private final int POSITION_THRESHOLD = 500;
  private boolean endable = false;

  public ArticulateHatchPanel(ArticulatorPosition state) {
    requires(Robot.hatchManipulator);
    setName("ArticulateHatchPanel Command");
    LOG.info(getName() + " Constructed");
    this.state = state;
  }

  public ArticulateHatchPanel(ArticulatorPosition state, boolean endable) {
    this(state);
    this.endable = endable;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.hatchManipulator.setArticulatorPosition(state);
    LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    // below if-statement should never execute because endable is false
    if (endable && POSITION_THRESHOLD >= Math.abs(state.encPos - Robot.hatchManipulator.getArticulatorPositionTicks())) {
      LOG.info("Reached " + state.toString());
      return true;
    }
    if (state == ArticulatorPosition.IN_BOT) {
      if (Robot.hatchManipulator.isArticulatorLimitSwitchClosedReverse() || Robot.hatchManipulator.isArticulatorLimitSwitchClosedForward()) {
        Robot.hatchManipulator.getArticulator().setPosition(state.encPos);
        LOG.info("Reached!");
        return true;
      }
    }
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
    if(Robot.hatchManipulator.getArticulatorPosition() != ArticulatorPosition.IN_BOT)
      Robot.hatchManipulator.setArticulatorPosition(ArticulatorPosition.IN_BOT);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
  }
}
