/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

public class DriveDistance extends MoveHeading {

  /**
   * Construct Drive Distance w / Motion Magic
   * @param distance in inches
   */
  public DriveDistance(double distance) {
    super(distance / 12, 0);

    MOVE_THRESHOLD = 500;
    ANGLE_THRESHOLD = 2;
    ON_TARGET_MINIMUM_COUNT = 10;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    super.execute();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    double distError = right.getClosedLoopError();

    boolean isFinished = false;

    boolean isOnTarget = (Math.abs(distError) < MOVE_THRESHOLD);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > ON_TARGET_MINIMUM_COUNT) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
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
}
