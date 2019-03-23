/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* .-  ..  -..  .-  -.      .--  .-.  ---  -  .        -  ....  ..  ...       */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.RobotMap.CAN;
import frc.robot.RobotMap.PCM_PORTS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
/**
 * Add your docs here.
 * 
 */
public class Climber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private DoubleSolenoid fangs;
  private boolean isInLiftMode;

  public Climber(){
    isInLiftMode = false;
    fangs = new DoubleSolenoid(CAN.PCM_ID, PCM_PORTS.HAB_ACTUATE, PCM_PORTS.HAB_RETRACT);
  }

  @Override
  public void initDefaultCommand() {
  }

  public boolean isInLiftMode(){
    return isInLiftMode;
  }

  public void actuateDoubleSolenoid(boolean actuate){
    if(actuate) {
      fangs.set(DoubleSolenoid.Value.kForward);
    } else {
      fangs.set(DoubleSolenoid.Value.kReverse);
    }
    isInLiftMode = actuate;
  }
}