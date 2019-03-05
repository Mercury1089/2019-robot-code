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
import frc.robot.RobotMap.DS_USB;
import frc.robot.RobotMap.GAMEPAD_BUTTONS;
import frc.robot.RobotMap.JOYSTICK_BUTTONS;
import frc.robot.commands.TurtleMode;
import frc.robot.commands.cargo.ArticulateCargoIntake;
import frc.robot.commands.cargo.RunCargoManipulator;
import frc.robot.commands.cargo.ManuallyIntakeCargo;
//import frc.robot.commands.climber.Climb;
import frc.robot.commands.conditionals.ConditionalLevel2Elevator;
import frc.robot.commands.conditionals.ConditionalLevel3Elevator;
import frc.robot.commands.conditionals.GeneralIntake;
import frc.robot.commands.drivetrain.DriveToTarget;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.SwitchDriveDirection;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.commands.elevator.ManualElevator;
import frc.robot.commands.hatchpanel.ArticulateHatchPanel;
import frc.robot.subsystems.CargoIntake.ArticulationPosition;
import frc.robot.subsystems.CargoManipulator.ShooterSpeed;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.HatchManipulator.ArticulatorPosition;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.util.ShuffleDash;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  public static final double DEADZONE = 0.08;

  private ShuffleDash shuffleDash;

  private Joystick rightJoystick, leftJoystick, gamepad;

  private JoystickButton left1, left2, left3, left4, left5, left6, left7, left8, left9, left10, left11;
  private JoystickButton right1, right2, right3, right4, right5, right6, right7, right8, right9, right10, right11;
  private JoystickButton gamepadA, gamepadB, gamepadX, gamepadY, gamepadRB, gamepadLB, gamepadBack, gamepadStart, gamepadLeftStickButton, gamepadRightStickButton;

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
    left6 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN6);
    left7 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN7);
    left8 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN8);
    left9 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN9);
    left10 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN10);
    left11 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN11);

    right1 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN1);
    right2 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN2);
    right3 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN3);
    right4 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN4);
    right5 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN5);
    right6 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN6);
    right7 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN7);
    right8 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN8);
    right9 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN9);
    right10 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN10);
    right11 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN11);

    gamepadA = new JoystickButton(gamepad, GAMEPAD_BUTTONS.A);
    gamepadB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.B);
    gamepadX = new JoystickButton(gamepad, GAMEPAD_BUTTONS.X);
    gamepadY = new JoystickButton(gamepad, GAMEPAD_BUTTONS.Y);
    gamepadRB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.RB);
    gamepadLB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.LB);
    gamepadBack = new JoystickButton(gamepad, GAMEPAD_BUTTONS.BACK);
    gamepadStart = new JoystickButton(gamepad, GAMEPAD_BUTTONS.START);
    gamepadLeftStickButton = new JoystickButton(gamepad, GAMEPAD_BUTTONS.L3);
    gamepadRightStickButton = new JoystickButton(gamepad, GAMEPAD_BUTTONS.R3);

    left1.whenPressed(new GeneralIntake());
    left2.whenPressed(new SwitchDriveDirection(DriveDirection.HATCH));
    left3.whenPressed(new DriveToTarget());
    left4.whenPressed(new ArticulateHatchPanel(ArticulatorPosition.READY_TO_PICK));
    left5.whenPressed(new ArticulateHatchPanel(ArticulatorPosition.IN_BOT));

    try {
      left6.whenPressed(new MoveOnPath("LeftMiddle"));
      left7.whenPressed(new MoveOnPath("LeftClose"));
      left8.whenPressed(new MoveOnPath("MidLeft"));
      left9.whenPressed(new MoveOnPath("MidRight"));
      left10.whenPressed(new MoveOnPath("RightClose"));
      left11.whenPressed(new MoveOnPath("RightMiddle"));
    } catch(FileNotFoundException fnfe) {
      System.out.println("Invalid file! " + fnfe.getMessage());
    }

    right1.whenPressed(new RunCargoManipulator(ShooterSpeed.FAST_EJECT));
    right2.whenPressed(new SwitchDriveDirection(DriveDirection.CARGO));
    right3.whenPressed(new ManuallyIntakeCargo());
    right4.whenPressed(new ArticulateCargoIntake(ArticulationPosition.OUT));
    right5.whenPressed(new ArticulateCargoIntake(ArticulationPosition.IN));

    try {
      right6.whenPressed(new MoveOnPath("LeftRocketFar"));
      right7.whenPressed(new MoveOnPath("LeftRocketClose"));
      right8.whenPressed(new MoveOnPath("MidLeft"));        /* PLACEHOLDER FOR GO TO STATION */
      right9.whenPressed(new MoveOnPath("MidRight"));       /* PLACEHOLDER FOR GO TO STATION */
      right10.whenPressed(new MoveOnPath("RightRocketClose"));
      right11.whenPressed(new MoveOnPath("RightRocketFar"));
    } catch(FileNotFoundException fnfe) {
      System.out.println("Invalid file! " + fnfe.getMessage());
    }

    gamepadA.whenPressed(new AutomaticElevator(ElevatorPosition.BOTTOM));
    gamepadB.whenPressed(new ManualElevator());
    gamepadX.whenPressed(new AutomaticElevator(ElevatorPosition.CARGOSHIP_C));
    gamepadY.whenPressed(new AutomaticElevator(ElevatorPosition.ROCKET_1_C));
    gamepadLB.whenPressed(new ConditionalLevel2Elevator());
    gamepadRB.whenPressed(new ConditionalLevel3Elevator());
    //gamepadBack.whenPressed(new Climb());
    gamepadStart.whenPressed(new TurtleMode());

  }
  
  public double getJoystickX(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getX();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getX();
      default:
        return 0;
    }
  }

  public double getJoystickY(int port) {
    switch(port) {
      case DS_USB.LEFT_STICK:
        return leftJoystick.getY();
      case DS_USB.RIGHT_STICK:
        return rightJoystick.getY();
      default:
        return 0;
    }
  }

  public double getJoystickZ(int port) {
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
