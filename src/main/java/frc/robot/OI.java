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
import frc.robot.commands.DegreeRotate;
import frc.robot.commands.DriveDistance;
import frc.robot.commands.DriveWithJoysticks;
import frc.robot.commands.MoveOnPath;
import frc.robot.commands.RotateToTarget;
import frc.robot.commands.MoveOnPath.MPDirection;
import frc.robot.commands.MoveHeading;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import frc.robot.commands.RunCargoManipulator;
import frc.robot.commands.TrackTarget;
import frc.robot.commands.DriveWithJoysticks.DriveType;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  public static final double DEADZONE = 0.08;

  private ShuffleDash shuffleDash;

  private Joystick rightJoystick, leftJoystick, gamepad;

  private JoystickButton left1, left2, left3, left7, left8, left10, right1, right2, right6, right7, right8, right9, right10, right11;

  public OI() {
    leftJoystick = new Joystick(DS_USB.LEFT_STICK);
    rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
    gamepad = new Joystick(DS_USB.GAMEPAD);

    shuffleDash = new ShuffleDash();

    left1 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN1);
    left2 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN2);
    left3 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN3);
    left7 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN7);
    left8 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN8);
    left10 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN10);
    right1 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN1);
    right2 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN2);
    right6 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN6);
    right7 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN7);
    right8 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN8);
    right9 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN9);
    right10 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN10);
    right11 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN11);
    //gamepadB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.B);

    left1.whenPressed(new RunCargoManipulator(ShooterSpeed.FAST_INTAKE));
    left3.whenPressed(new DriveWithJoysticks(DriveType.ARCADE));
    left7.whenPressed(new MoveOnPath("CurveLeft", MPDirection.FORWARD));
    left8.whenPressed(new MoveOnPath("StraightProfile", MPDirection.FORWARD));
    left10.whenPressed(new RotateToTarget());

    right1.whenPressed(new RunCargoManipulator(ShooterSpeed.FAST_EJECT));
    right2.whenPressed(new RunCargoManipulator(ShooterSpeed.STOP));
    right6.whenPressed(new DegreeRotate(90));
    right7.whenPressed(new DegreeRotate(-90));
    right8.whenPressed(new DriveDistance(36));
    right9.whenPressed(new TrackTarget());
    right10.whenPressed(new MoveHeading(60, -60));
    right11.whenPressed(new MoveHeading(60, 60));

    //gamepadB.whenPressed();
  }
  
  public double getX(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getX();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getX();
      case DS_USB.GAMEPAD:
        return gamepad.getX();
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
