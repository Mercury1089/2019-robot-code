/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.RobotMap.*;
import frc.robot.util.ShuffleDash;
import frc.robot.commands.DriveDistance;
import frc.robot.commands.MoveOnPath;
import frc.robot.commands.MoveOnPath.Direction;
import frc.robot.subsystems.ProtoShooter.ShooterSpeed;
import frc.robot.commands.RunShooter;
/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  private ShuffleDash shuffleDash;
  
  private Joystick rightJoystick, leftJoystick, gamepad;

  private JoystickButton left1, left7, left8, left10, right1, right2, right10;

  public OI() {
    leftJoystick = new Joystick(DS_USB.LEFT_STICK);
    rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
    gamepad = new Joystick(DS_USB.GAMEPAD);

    shuffleDash = new ShuffleDash();

    left1 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN1);
    left7 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN7);
    left8 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN8);
    left10 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN10);
    right1 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN1);
    right2 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN2);
    right10 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN10);

    left1.whenPressed(new RunShooter(ShooterSpeed.FAST_INTAKE));
    left7.whenPressed(new MoveOnPath("CurveLeft", Direction.FORWARD));
    left8.whenPressed(new MoveOnPath("StraightProfile", Direction.FORWARD));
    left10.whenPressed(new DriveDistance(100, .7));

    right1.whenPressed(new RunShooter(ShooterSpeed.FAST_EJECT));
    right2.whenPressed(new RunShooter(ShooterSpeed.STOP));
  }

  public double getX(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getX();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getX();
      default:
        return 0;
    }
  }

  public double getY(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getY();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getY();
      case DS_USB.GAMEPAD:
				return -gamepad.getRawAxis(5);
      default:
        return 0;
    }
  }

  public void updateDash() {
    shuffleDash.updateDash();
  }
}
