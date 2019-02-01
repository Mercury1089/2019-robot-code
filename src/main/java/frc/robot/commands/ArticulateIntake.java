/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.ProtoIntake;
import frc.robot.subsystems.ProtoIntake.ArticulationPosition;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;

public class ArticulateIntake extends Command {
  ArticulationPosition targetState;
  ProtoIntake intake;

  public ArticulateIntake() {
    requires(Robot.protoIntake);
    intake = Robot.protoIntake;
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
    Robot.protoIntake.setArticulatorSpeed(targetState == ArticulationPosition.IN ? -1.0 : 1.0);
  }

  /**
   * TODO Check the reverse/forward limit switch configuration to see if
   * this is activated correctly and we don't decimate the robot
   */
  @Override
  protected boolean isFinished() {
    if(targetState == ArticulationPosition.IN &&
      intake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE)) {
        return true;
    }
    else if(targetState == ArticulationPosition.OUT &&
      intake.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.FORWARD)) {
        return true;
    }
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    intake.setArticulatorState(targetState);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
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
