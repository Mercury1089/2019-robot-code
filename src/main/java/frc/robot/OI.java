/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.FileNotFoundException;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.RobotMap.*;
import frc.robot.util.ShuffleDash;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.DriveDistance;
import frc.robot.commands.drivetrain.DriveWithJoysticks;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.limelight.RotateLimelight;
import frc.robot.commands.drivetrain.RotateToTarget;
import frc.robot.commands.drivetrain.SwitchDrive;
import frc.robot.commands.drivetrain.SwitchDriveDirection;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;
import frc.robot.commands.drivetrain.MoveHeading;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import frc.robot.subsystems.LimelightAssembly.LimelightPosition;
import frc.robot.commands.cargo.RunCargoManipulator;
import frc.robot.commands.drivetrain.TrackTarget;
import frc.robot.commands.drivetrain.DriveWithJoysticks.DriveType;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  public static final double DEADZONE = 0.08;

  private ShuffleDash shuffleDash;

  private Joystick rightJoystick, leftJoystick, gamepad;

  private JoystickButton left1, left2, left3, left4, left5, left7, left8, left10;
  private JoystickButton right1, right2, right3, right6, right7, right8, right9, right10, right11;

  public OI() {
    leftJoystick = new Joystick(DS_USB.LEFT_STICK);
    rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
    gamepad = new Joystick(DS_USB.GAMEPAD);

    shuffleDash = new ShuffleDash();

    left1 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN1);
    left2 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN2);
    left3 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN3);
    left4 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN4);
    left5 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN5);
    left7 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN7);
    left8 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN8);
    left10 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN10);
    right1 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN1);
    right2 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN2);
    right3 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN3);
    right6 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN6);
    right7 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN7);
    right8 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN8);
    right9 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN9);
    right10 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN10);
    right11 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN11);
    //gamepadB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.B);

    left1.whenPressed(new RunCargoManipulator(ShooterSpeed.FAST_INTAKE));
    left2.whenPressed(new SwitchDriveDirection(DriveDirection.HATCH));
    left3.whenPressed(new DriveWithJoysticks(DriveType.ARCADE));
    left4.whenPressed(new RotateLimelight(LimelightPosition.FACING_HATCH_PANEL));
    left5.whenPressed(new RotateLimelight(LimelightPosition.FACING_CARGO));
    try {
      left7.whenPressed(new MoveOnPath("RightMiddle"));
      left8.whenPressed(new MoveOnPath("RightFar"));
      right10.whenPressed(new MoveOnPath("RightRocketClose"));
      right11.whenPressed(new MoveOnPath("RightRocketFar"));
    } catch(FileNotFoundException fnfe) {
      System.out.println("Invalid file!!!!");
    }
    left10.whenPressed(new RotateToTarget());
    right1.whenPressed(new RunCargoManipulator(ShooterSpeed.FAST_EJECT));
    right2.whenPressed(new SwitchDriveDirection(DriveDirection.CARGO));
    right3.whenPressed(new RunCargoManipulator(ShooterSpeed.STOP));
    right6.whenPressed(new DegreeRotate(90));
    right7.whenPressed(new DegreeRotate(-90));
    right8.whenPressed(new DriveDistance(36));
    right9.whenPressed(new TrackTarget());
    //right11.whenPressed(new MoveHeading(60, 60));

    //gamepadB.whenPressed();
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
      default:
        return 0;
    }
  }

  public double getZ(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getZ();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getZ();
      default:
        return 0;
    }
  }

  public double getGamepadAxis(int axis) {
    return ((axis % 2 != 0 && axis != 3) ? -1.0 : 1.0) * gamepad.getRawAxis(axis);
  }

  public void updateDash() {
    shuffleDash.updateDash();
  }
}
