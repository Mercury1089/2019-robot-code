package frc.robot;

/**
 * The RobotSettings is a mapping from the ports sensors and actuators are wired
 * into to a variable name. This provides flexibility changing wiring, makes
 * checking the wiring easier and significantly reduces the number of magic
 * numbers floating around.
 * <p>
 * For example to map the left and right motors, you could define the following
 * variables to use with your drivetrain subsystem.
 * 
 * <pre>
 * public static int leftMotor = 1;
 * public static int rightMotor = 2;
 * </pre>
 * <p>
 * If you are using multiple modules, make sure to define both the port number
 * and the module. For example, with a rangefinder:
 * 
 * <pre>
 * public static int rangefinderPort = 1;
 * public static int rangefinderModule = 1;
 * </pre>
 */
public class RobotMap {

	public static final int CTRE_TIMEOUT = 10;

	/**
	 * Class containing constants for the ports of all CAN devices.
	 */
	public static class CAN {
		public static final int 
			DRIVETRAIN_ML = 1, 
			DRIVETRAIN_MR = 2, 
			DRIVETRAIN_SL = 3, 
			DRIVETRAIN_SR = 4,
			ELEVATOR_TALON = 5,
			SHOOTER_LEFT = 6, 
			SHOOTER_RIGHT = 7, 
			CARGO_INTAKE = 8, 
			CANIFIER = 9,
			PIGEON = 10,
			HATCH_INTAKE = 11,
			HATCH_EJECTOR = 12,
			CARGO_MANIPULATOR = 13,
			SCREW_BL = 14,
			SCREW_BR = 15,
			SCREW_FRONT = 16,
			SCREW_DRIVE = 17,
			PCM_ID = 18; 
			
		private CAN() {
		} // Should never be constructed.
	}

	/**
	 * Class containing constants for AIO device channels
	 */
	public static class AIO {
		public static final int 
			LEFT_ULTRASONIC = 0,
			RIGHT_ULTRASONIC = 1;

		private AIO() {
		} // Should never be constructed.
	}

	/**
	 * Class containing constants for PID loop indicies
	 */
	public static class PID {
		public static final int PRIMARY_PID_LOOP = 0;

		private PID() {
		} // Should never be constructed.
	}

	public static class PCM_PORTS{
		public static final int 
			HAB_ACTUATE = 0,
			HAB_RETRACT = 1;
		private PCM_PORTS(){			
		} // Should never be constructed
	}

	public static class CANIFIER_PWM {
		public static final int 
			HATCH_LIDAR = 0,
			CARGO_LIDAR = 1;
		private CANIFIER_PWM() {
		} // Should never be constructed.
	}

	/**
	 * Class containing constants for PWM device channels
	 */
	public static class PWM {
		public static final int LIMELIGHT_SERVO = 1;
		private PWM() {
		} // Should never be constructed.
	}

	/**
	 * Class containing constants for Digital Input channels
	 */
	public static class DIGITAL_INPUT {
		public static final int ELEVATOR_LIMIT_SWITCH = 0;

		private DIGITAL_INPUT() {
		} // Should never be constructed.
	}

	/**
	 * Class containing constants for ports of the devices on the USB interface of
	 * the Driver Station. Good for OI joystick ports and of the like.
	 */
	public static class DS_USB {
		public static final int 
			LEFT_STICK = 0, 
			RIGHT_STICK = 1, 
			GAMEPAD = 2;
		private DS_USB() {
		} // Should never be constructed.
	}

	public static class GAMEPAD_AXIS {
		public static final int 
			leftX = 0, 
			leftY = 1, 
			leftTrigger = 2, 
			rightTrigger = 3, 
			rightX = 4, 
			rightY = 5;
		private GAMEPAD_AXIS() {
		}
	}

	public static class GAMEPAD_BUTTONS {
		public static final int 
			A = 1,
			B = 2,
			X = 3,
			Y = 4,
			LB = 5,
			RB = 6,
			BACK = 7,
			START = 8,
			L3 = 9,
			R3 = 10;
		private GAMEPAD_BUTTONS() {
		}
	}

	public static class JOYSTICK_BUTTONS {
		public static final int 
			BTN1 = 1,
			BTN2 = 2,
			BTN3 = 3,
			BTN4 = 4,
			BTN5 = 5,
			BTN6 = 6,
			BTN7 = 7,
			BTN8 = 8,
			BTN9 = 9,
			BTN10 = 10,
			BTN11 = 11;
		public JOYSTICK_BUTTONS() {
		}
	}
}
