package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.drivetrain.DriveWithJoysticks;
import frc.robot.sensors.LIDAR;
import frc.robot.sensors.LIDAR.PWMOffset;
import frc.robot.sensors.Ultrasonic;
import frc.robot.util.*;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.util.interfaces.IMercMotorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Subsystem that encapsulates the driveAssist train.
 * This contains the {@link DriveAssist} needed to driveAssist manually
 * using the motor controllers.
 */
public class DriveTrain extends Subsystem implements PIDOutput {
    //Testing statement - Ishaq
    public static final int DRIVE_PID_SLOT = 0,
        DRIVE_SMOOTH_MOTION_SLOT = 1,
        DRIVE_MOTION_PROFILE_SLOT = 2,
        DRIVE_SMOOTH_TURN_SLOT = 3;
    public static final int REMOTE_DEVICE_0 = 0,
        REMOTE_DEVICE_1 = 1;
    public static final int PRIMARY_LOOP = 0,
        AUXILIARY_LOOP = 1;
    public static final int MAG_ENCODER_TICKS_PER_REVOLUTION = 4096,
        NEO_ENCODER_TICKS_PER_REVOLUTION = 42,
        PIGEON_NATIVE_UNITS_PER_ROTATION = 8192;
    public static final double MAX_SPEED = 1,
        MIN_SPEED = -1;
    public static final double GEAR_RATIO = 1,
        MAX_RPM = 545,
        WHEEL_DIAMETER_INCHES = 5.8;
    public static final double NOMINAL_OUT = 0.0,
                               PEAK_OUT = 1.0;

    private final PIDGain DRIVE_GAINS, SMOOTH_GAINS, MOTION_PROFILE_GAINS, TURN_GAINS;

    private Logger log = LogManager.getLogger(DriveTrain.class);

    private IMercMotorController leaderLeft, leaderRight, followerLeft, followerRight;
    private DriveAssist driveAssist;
    private PigeonIMU podgeboi;
    private CANifier canifier;
    private LIDAR hatchLidar;
    private Ultrasonic leftUltrasonic;
    private Ultrasonic rightUltrasonic;
    private DriveTrainLayout layout;
    private boolean isInMotionMagicMode;
    private LEDColor currentLEDColor;

    /**
     * Creates the drivetrain, assuming that there are four controllers.
     *
     * @param layout The layout of motor controllers used on the drivetrain
     */
    public DriveTrain(DriveTrain.DriveTrainLayout layout) {
        //This should eventually be fully configurable
        // At this point it's based on what the layout is

        this.layout = layout;
        switch (layout) {
            case LEGACY:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
                leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercTalonSRX(CAN.DRIVETRAIN_SL);
                followerRight = new MercTalonSRX(CAN.DRIVETRAIN_SR);
                break;
            //case SPARKS:
                //leaderLeft = new MercSparkMax(CAN.DRIVETRAIN_ML);
                //leaderRight = new MercSparkMax(CAN.DRIVETRAIN_MR);
                //followerLeft = new MercSparkMax(CAN.DRIVETRAIN_SL);
                //followerRight = new MercSparkMax(CAN.DRIVETRAIN_SR);
                //break;
            case TALONS:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
                leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercVictorSPX(CAN.DRIVETRAIN_SL);
                followerRight = new MercVictorSPX(CAN.DRIVETRAIN_SR);
                break;
        }

        //Initialize podgeboi
        podgeboi = new PigeonIMU(CAN.PIGEON);
        podgeboi.configFactoryDefault();

        //CANifier and distance sensors
        canifier = new CANifier(RobotMap.CAN.CANIFIER);
        hatchLidar = new LIDAR(canifier, CANifier.PWMChannel.PWMChannel0, PWMOffset.EQUATION_C);
        rightUltrasonic = new Ultrasonic(RobotMap.AIO.RIGHT_ULTRASONIC);
        leftUltrasonic = new Ultrasonic(RobotMap.AIO.LEFT_ULTRASONIC);

        //Account for motor orientation.
        leaderLeft.setInverted(false);
        followerLeft.setInverted(false);
        leaderRight.setInverted(true);
        followerRight.setInverted(true);

        //Set neutral mode to Brake to make sure our motor controllers are all in brake mode by default
        setNeutralMode(NeutralMode.Brake);

        //Account for encoder orientation.
        leaderLeft.setSensorPhase(true);
        leaderRight.setSensorPhase(true);

        //Config feedback sensors for each PID slot, ready for MOTION PROFILING
        initializeMotionMagicFeedback();

        // Config PID
        DRIVE_GAINS = new PIDGain(0.125, 0.0, 0.05, 0.0, .75);   // .3
        SMOOTH_GAINS = new PIDGain(0.6, 0.00032, 0.45, getFeedForward(), 1.0);    //.00032
        MOTION_PROFILE_GAINS = new PIDGain(0.6, 0.0, 0.0, getFeedForward(), 1.0);
        TURN_GAINS = new PIDGain(0.35, 0.0, 0.27, 0.0, 1.0);
        leaderRight.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        leaderLeft.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        leaderRight.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        leaderLeft.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        leaderRight.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        leaderLeft.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        leaderRight.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);
        leaderLeft.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);

        resetEncoders();

        driveAssist = new DriveAssist(leaderLeft, leaderRight, DriveDirection.HATCH);

        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(leaderLeft);
        followerRight.follow(leaderRight);

        configVoltage(NOMINAL_OUT, PEAK_OUT);
        setMaxOutput(PEAK_OUT);

        stop();
    }

    public void initializeNormalMotionFeedback() {
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_LOOP);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_LOOP);
        //leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        //leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        //leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);
        //leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);
        //leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_TURN_SLOT);
        //leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_TURN_SLOT);

        leaderRight.configSelectedFeedbackCoefficient(1.0, DriveTrain.PRIMARY_LOOP);

        isInMotionMagicMode = false;
    }

    public void initializeMotionMagicFeedback() {
        /* Configure left's encoder as left's selected sensor */
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DriveTrain.PRIMARY_LOOP);

        /* Configure the Remote Talon's selected sensor as a remote sensor for the right Talon */
        leaderRight.configRemoteFeedbackFilter(leaderLeft.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, DriveTrain.REMOTE_DEVICE_0);

        /* Configure the Pigeon IMU to the other remote slot available on the right Talon */
        leaderRight.configRemoteFeedbackFilter(getPigeon().getDeviceID(), RemoteSensorSource.Pigeon_Yaw, DriveTrain.REMOTE_DEVICE_1);

        /* Setup Sum signal to be used for Distance */
        leaderRight.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
        leaderRight.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

        /* Configure Sum [Sum of both QuadEncoders] to be used for Primary PID Index */
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, DriveTrain.PRIMARY_LOOP);

        /* Scale Feedback by 0.5 to half the sum of Distance */
        leaderRight.configSelectedFeedbackCoefficient(0.5, DriveTrain.PRIMARY_LOOP);

        /* Configure Remote 1 [Pigeon IMU's Yaw] to be used for Auxiliary PID Index */
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, DriveTrain.AUXILIARY_LOOP);

        /* Scale the Feedback Sensor using a coefficient */
        leaderRight.configSelectedFeedbackCoefficient(1, DriveTrain.AUXILIARY_LOOP);

        /* Set status frame periods to ensure we don't have stale data */
        leaderRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20);
        leaderLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20);
        getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5);

        isInMotionMagicMode = true;
    }

    public void configPIDSlots(DriveTrainSide dts, int primaryPIDSlot, int auxiliaryPIDSlot) {
        if (primaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                leaderRight.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
            else
                leaderLeft.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
        }
        if (auxiliaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                leaderRight.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
            else
                leaderLeft.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
        }

    }

    public void configClosedLoopPeakOutput(int driveTrainPIDSlot, double maxOut) {
        leaderLeft.configClosedLoopPeakOutput(driveTrainPIDSlot, maxOut);
        leaderRight.configClosedLoopPeakOutput(driveTrainPIDSlot, maxOut);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoysticks(DriveWithJoysticks.DriveType.ARCADE));
    }

    public void resetEncoders() {
        leaderLeft.resetEncoder();
        leaderRight.resetEncoder();
    }

    @Override
    public void periodic() {
        hatchLidar.updatePWMInput();
        //updateLEDs();
    }

    /*private void updateLEDs() {
        if (Robot.clawAndIntake.isCargoInRobot()) {
            setLEDColor(LEDColor.GREEN);
        } else if (Robot.clawAndIntake.getClawState() == ClawAndIntake.ClawState.EJECTING) {
            setLEDColor(LEDColor.RED);
        } else if (Robot.clawAndIntake.getClawState() == ClawAndIntake.ClawState.INTAKING) {
            setLEDColor(LEDColor.BLUE);
        } else {
            setLEDColor(LEDColor.BLACK);
        }
    }

    /**
     * Sets the canifier LED output to the correct {@code LEDColor}. The
     * CANifier use BRG (not RGB) for its LED Channels
     */
    private void setLEDColor(LEDColor ledColor) {
        currentLEDColor = ledColor;
        canifier.setLEDOutput(ledColor.getRed(), CANifier.LEDChannel.LEDChannelB);
        canifier.setLEDOutput(ledColor.getBlue(), CANifier.LEDChannel.LEDChannelA);
        canifier.setLEDOutput(ledColor.getGreen(), CANifier.LEDChannel.LEDChannelC);
    }

    public CANifier getCanifier() {
        return canifier;
    }

    /**
     * Stops the driveAssist train.
     */
    public void stop() {
        leaderLeft.stop();
        leaderRight.stop();
        if (layout == DriveTrainLayout.SPARKS) {
            //NOTE: CALLING STOP ON VICTOR FOLLOWERS BREAKS THEM OUT OF FOLLOW MODE!!
            followerLeft.stop();
            followerRight.stop();
        }
    }

    /**
     * Sets both of the front talons to have a forward output of nominalOutput and peakOutput with the reverse output setClawState to the negated outputs.
     *
     * @param nominalOutput The desired nominal voltage output of the left and right talons, both forward and reverse.
     * @param peakOutput    The desired peak voltage output of the left and right talons, both forward and reverse
     */
    public void configVoltage(double nominalOutput, double peakOutput) {
        leaderLeft.configVoltage(nominalOutput, peakOutput);
        leaderRight.configVoltage(nominalOutput, peakOutput);
    }

    public DriveDirection getDirection() {
        return driveAssist.getDirection();
    }

    public void setDirection(DriveDirection dd) {
        driveAssist.setDirection(dd);
    }

    public PigeonIMU getPigeon() {
        if (podgeboi == null) {
            log.error("PigeonIMU was not initialized!");
        }
        return podgeboi;
    }

    public LIDAR getHatchLidar() {
        if (hatchLidar == null) {
            log.error("LIDAR was not initialized!");
        }
        return hatchLidar;
    }

    public Ultrasonic getRightUltrasonic() {
        if (rightUltrasonic == null) {
            log.error("Right Ultrasonic was not initialized!");
        }
        return rightUltrasonic;
    }

    public Ultrasonic getLeftUltrasonic() {
        if (leftUltrasonic == null) {
            log.error("Right Ultrasonic was not initialized!");
        }
        return leftUltrasonic;
    }

    public double getPigeonYaw() {
        double[] currYawPitchRoll = new double[3];
        podgeboi.getYawPitchRoll(currYawPitchRoll);
        return currYawPitchRoll[0];
    }

    public DriveTrainLayout getLayout() {
        return layout;
    }

    public boolean isInMotionMagicMode() {
        return isInMotionMagicMode;
    }

    public void resetPigeonYaw() {
        podgeboi.setYaw(0);
    }

    public double getLeftEncPositionInTicks() {
        return leaderLeft.getEncTicks();
    }

    public double getRightEncPositionInTicks() {
        return leaderRight.getEncTicks();
    }

    public double getLeftEncPositionInFeet() {
        return MercMath.getEncPosition(getLeftEncPositionInTicks());
    }

    public double getRightEncPositionInFeet() {
        return MercMath.getEncPosition(getRightEncPositionInTicks());
    }

    public IMercMotorController getLeftLeader() {
        return leaderLeft;
    }

    public IMercMotorController getRightLeader() {
        return leaderRight;
    }

    public IMercMotorController getLeftFollower() {
        return followerLeft;
    }

    public IMercMotorController getRightFollower() {
        return followerRight;
    }

    public DriveAssist getDriveAssist() {
        return driveAssist;
    }

    public double getFeedForward() {
        return MercMath.calculateFeedForward(MAX_RPM);
    }

    public void pidWrite(double output) {
        driveAssist.tankDrive(output, -output);
    }

    public void setMaxOutput(double maxOutput) {
        driveAssist.setMaxOutput(maxOutput);
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        leaderLeft.setNeutralMode(neutralMode);
        leaderRight.setNeutralMode(neutralMode);
        followerLeft.setNeutralMode(neutralMode);
        followerRight.setNeutralMode(neutralMode);
    }

    public enum DriveTrainLayout {
        SPARKS,
        TALONS,
        LEGACY
    }

    public enum DriveTrainSide {
        RIGHT,
        LEFT
    }

    public enum LEDColor {
        RED(1.0, 0.0, 0.0),
        GREEN(0.0, 0.0, 1.0),
        BLUE(0.0, 0.0, 1.0),
        YELLOW(1.0, 1.0, 0.0),
        CYAN(0.0, 1.0, 1.0),
        MAGENTA(1.0, 0.0, 1.0),
        WHITE(1.0, 1.0, 1.0),
        BLACK(0.0, 0.0, 0.0);

        private double r, g, b;

        LEDColor(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public double getRed() {
            return r;
        }

        public double getGreen() {
            return g;
        }

        public double getBlue() {
            return b;
        }
    }
}
