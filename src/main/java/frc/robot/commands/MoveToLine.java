/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
/*
package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.util.DelayableLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMath;
import frc.robot.util.Recallable;

public class MoveToLine extends Command {
  //run until interupted; then it is aligned for scoring
  private static Logger log = LogManager.getLogger(DriveWithJoysticks.class); 
  private double[] volts = {.25, .5};
  private DriveTrain.Alignment state;

  public MoveToLine() {
    requires(Robot.driveTrain);
    log.debug(getName() + " Created");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.driveTrain.configVoltage(volts[0], volts[1]);
    log.info(getName() + " Initialized");
    Robot.driveTrain.set
  }

  // Called repeatedly when this Command is scheduled to run
  //state is set to the first side of the robot to go over the tape
  @Override
  protected void execute() {
    if(state == null && Robot.driveTrain.getAlignment() != Robot.driveTrain.Alignment.NOT_ON_TAPE)
      state = Robot.driveTrain.getAlignment();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(state == Robot.driveTrain.Alignment.LEFT)
      return  Robot.driveTrain.getAlignment() == Robot.driveTrain.RIGHT
           || Robot.driveTrain.getAlignment() == Robot.driveTrain.LINED_UP;
    else if(stete == Robot.driveTrain.RIGHT)
      return  Robot.driveTrain.getAlignment() == Robot.driveTrain.LEFT
           || Robot.driveTrain.getAlignment() == Robot.driveTrain.LINED_UP;
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.driveTrain.stop();
    log.info(getName() + "Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
*/