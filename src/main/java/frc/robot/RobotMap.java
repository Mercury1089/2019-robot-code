package frc.robot;

public class RobotMap {

    public static final int CTRE_TIMEOUT = 10;

    public static class CAN {
        public static final int
                DRIVETRAIN_ML = 1,
                DRIVETRAIN_MR = 2,
                DRIVETRAIN_SL = 3,
                DRIVETRAIN_SR = 4,
                ELEVATOR_TALON = 5,
                CLAW_LEFT = 6,
                CLAW_RIGHT = 7,
                MOUTH_INTAKE = 8,
                CANIFIER = 9,
                PIGEON = 10,
                FANGS = 11,
                HATCH_EJECTOR = 12,
                SCREW_BL = 14,
                SCREW_BR = 15,
                SCREW_FRONT = 16,
                SCREW_DRIVE = 17,
                PCM = 18;

        private CAN() {
        }
    }

    public static class PCM {
        public static final int HAB_ACTUATE = 0,
            HAB_RETRACT = 1,
            MOUTH_ACTUATE = 2,
            MOUTH_RETRACT = 3;


        private PCM() {
            
        }
    }

    public static class AIO {
        public static final int LEFT_ULTRASONIC = 0;
        public static final int RIGHT_ULTRASONIC = 1;

        private AIO() {
        }
    }

    public static class PID {
        public static final int PRIMARY_PID_LOOP = 0;

        private PID() {
        }
    }

    public static class CANIFIER_PWM {
        public static final int
                HATCH_LIDAR = 0,
                CARGO_LIDAR = 1;

        private CANIFIER_PWM() {
        }
    }

    public static class PWM {
        public static final int LIMELIGHT_SERVO = 1;

        private PWM() {
        }
    }

    public static class DIGITAL_INPUT {
        public static final int ELEVATOR_LIMIT_SWITCH = 0;

        private DIGITAL_INPUT() {
        }
    }

    public static class DS_USB {
        public static final int LEFT_STICK = 0, RIGHT_STICK = 1, GAMEPAD = 2;

        private DS_USB() {
        }
    }

    public static class GAMEPAD_AXIS {
        public static final int leftX = 0, leftY = 1, leftTrigger = 2, rightTrigger = 3, rightX = 4, rightY = 5;

        private GAMEPAD_AXIS() {
        }
    }

    public static class GAMEPAD_BUTTONS {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;
        public static final int LB = 5;
        public static final int RB = 6;
        public static final int BACK = 7;
        public static final int START = 8;
        public static final int L3 = 9;
        public static final int R3 = 10;

        private GAMEPAD_BUTTONS() {
        }
    }

    public static class JOYSTICK_BUTTONS {
        public static final int BTN1 = 1;
        public static final int BTN2 = 2;
        public static final int BTN3 = 3;
        public static final int BTN4 = 4;
        public static final int BTN5 = 5;
        public static final int BTN6 = 6;
        public static final int BTN7 = 7;
        public static final int BTN8 = 8;
        public static final int BTN9 = 9;
        public static final int BTN10 = 10;
        public static final int BTN11 = 11;

        public JOYSTICK_BUTTONS() {
        }
    }
}
