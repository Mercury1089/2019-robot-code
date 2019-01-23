/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

public class AlignWithTape extends Command {
  
  private boolean finished = false;
  
  private enum ALIGNMENT{
    ALL_FALSE,
    //Sensor 1 is true for below
    DONE,
    FALSE_FALSE,
    LEFT_FALSE,
    FALSE_RIGHT
  }
  
  public AlignWithTape() 
  {
    requires(Robot.driveTrain);
    setName("AlignWithTape Command");
	  log.debug(getName() + " command created");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    /*
      all possible states of sensor allignment
      p1, p2, p3
                            -----------------
    line top half     ----- |            p3 |
      front of robot  ----- | p1            | *not to scale
    line bot half     ----- |            p2 |
                            -----------------
      p1 = false --> find line
      p1 = true, p2 = false, p3 = false --> rotate a lot
      p1 = true, p2 = true, p3 = false --> rotate left 
      p1 = true, p2 = true, p3 = true --> Do nothing
      p1 = true, p2 = false, p3 = true --> rotate right
    */
    //pseudocode: check alignment of each sensor; use alignment enum
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //pseudocode: based on alignment do something
    //when done or keep runnning until interrupted
    finished = true;
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return finished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    log.info(getName() + " ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    log.info(getName() + " interrupted");
    this.end();
  }
}
