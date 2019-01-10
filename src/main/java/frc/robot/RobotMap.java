/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
  // For example to map the left and right motors, you could define the
  // following variables to use with your drivetrain subsystem.
  // public static int leftMotor = 1;
  // public static int rightMotor = 2;

  // If you are using multiple modules, make sure to define both the port
  // number and the module. For example you with a rangefinder:
  // public static int rangefinderPort = 1;
  // public static int rangefinderModule = 1;
  public static class CAN {
		public static final int
      DRIVETRAIN_ML = 4,
			DRIVETRAIN_MR = 2,
			DRIVETRAIN_SL = 3,
      DRIVETRAIN_SR = 1;
      
      private CAN() {}
  }
  
  public static class DS_USB {
    public static final int
      LEFT_STICK = 0,
      RIGHT_STICK = 1,
      GAMEPAD = 2;

      private DS_USB() {}
  }
}
