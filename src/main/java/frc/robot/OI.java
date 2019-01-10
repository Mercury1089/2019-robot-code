/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.RobotMap.*;
/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  private Joystick rightJoystick, leftJoystick, gamepad;

  public OI() {
    leftJoystick = new Joystick(DS_USB.LEFT_STICK);
    rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
    gamepad = new Joystick(DS_USB.GAMEPAD);
  }

  //COMPLETE:
  public double getX(int port) {
    //return 0;
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getX();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getX();
      default:
        return 0;
    }
  }

  //COMPLETE:
  public double getY(int port) {
    //return 0;
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getY();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getY();
      default:
        return 0;
    }
  }
}
