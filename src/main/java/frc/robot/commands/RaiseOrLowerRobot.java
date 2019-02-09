/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.PIDCommand;
import frc.robot.Robot;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Climber.ClimberPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RaiseOrLowerRobot extends PIDCommand {
  
  public Climber.ClimberPosition startingPosition, endingPosition;
  public static final double p = 0, i = 0, d = 0; //null values must be tuned
  private final Logger LOG = LogManager.getLogger(AcquireHatchPanel.class);
  
  
  public RaiseOrLowerRobot(Climber.ClimberPosition endPos) {
    super(p, i, d);
    requires(Robot.climber);
    setName("RaiseOrLowerRobot Command");
    LOG.info(getName() + " Constructed");
    this.startingPosition = Robot.climber.getClimberPosition();
    this.endingPosition = endPos;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if (this.startingPosition == ClimberPosition.GROUNDED){
      if (this.endingPosition == ClimberPosition.RAISED){

      }else if (this.endingPosition == ClimberPosition.RAISED_BACK){

      } 
    }else if (this.endingPosition == ClimberPosition.RAISED){
      if (this.endingPosition == ClimberPosition.GROUNDED){

      }else if (this.endingPosition == ClimberPosition.RAISED_BACK){

      }
    }else if (this.startingPosition == ClimberPosition.RAISED_BACK){
      if (this.endingPosition == ClimberPosition.GROUNDED){

      }else if (this.endingPosition == ClimberPosition.RAISED){

      }
    }else{
      this.startingPosition = Robot.climber.getClimberPosition();
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return Robot.climber.getClimberPosition() == this.endingPosition;

  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    super.end();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }

  @Override
  protected double returnPIDInput() {
    return 0;
  }

  @Override
  protected void usePIDOutput(double output) {

  }
 
}