/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import frc.robot.util.DelayableLogger;
import java.util.concurrent.TimeUnit;

import com.ctre.phoenix.motorcontrol.ControlMode;

public class AutomaticElevator extends Command {
  private final Logger LOG = LogManager.getLogger(AutomaticElevator.class);
  private final DelayableLogger SLOW_LOG = new DelayableLogger(LOG, 1, TimeUnit.SECONDS);
  private final int ELEVATOR_THRESHOLD = 500;

  private Elevator.ElevatorPosition targetPos;

  private boolean endable;

  public AutomaticElevator(ElevatorPosition pos) {
    requires(Robot.elevator);
    targetPos = pos;
    endable = false;

    setName("UseElevator (" + pos + ")");
    LOG.info(getName() + " Constructed");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.elevator.setPosition(targetPos); //line switched with next
    Robot.elevator.getElevatorLeader().setPosition(targetPos.encPos);

    LOG.info(getName() + " initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    SLOW_LOG.run(log -> log.debug(getName() + " executing"));
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (endable && ELEVATOR_THRESHOLD >= Math.abs(targetPos.encPos - Robot.elevator.getCurrentHeight())) {
      LOG.info("Reached " + targetPos.toString());
      return true;
    }
    if (targetPos == Elevator.ElevatorPosition.BOTTOM) {
      if (Robot.elevator.isLimitSwitchClosed()) {
        Robot.elevator.getElevatorLeader().setPosition(targetPos.encPos);
        LOG.info("Reached!");
        return true;
      }
    }
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    LOG.info(getName() + "elevator ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " interrupted");
  }
}
