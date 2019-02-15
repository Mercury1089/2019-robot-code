package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.util.PIDGain;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.drivetrain.DriveWithJoysticks;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.MercMath;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.DriveAssist;
import frc.robot.sensors.LIDAR;
import frc.robot.sensors.RightSight;
import frc.robot.sensors.LIDAR.PWMOffset;

/**
 * Subsystem that encapsulates the drive train.
 * This contains the {@link DriveAssist} needed to drive manually
 * using the motor controllers.
 */
public class DriveTrain extends Subsystem implements PIDOutput {

    private Logger log = LogManager.getLogger(DriveTrain.class);

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

    private IMercMotorController leaderLeft, leaderRight, followerLeft, followerRight;

    private DriveAssist drive;
    private ADXRS450_Gyro gyroSPI;
    private PigeonIMU podgeboi;
    private RightSight rightSight;
    private CANifier canifier;
    private LIDAR lidar;

    private DriveTrainLayout layout;

    private final PIDGain DRIVE_GAINS, SMOOTH_GAINS, MOTION_PROFILE_GAINS, TURN_GAINS;

    private boolean isInMotionMagicMode;

    private static final double CARGO_INTAKE_THRESHOLD = 8.0;
    private LEDColor currentLEDColor;

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

	/**
	 * Creates the drivetrain, assuming that there are four controllers.
	 *
	 * @param fl Front-left controller ID
	 * @param fr Front-right controller ID
	 * @param bl Back-left controller ID
	 * @param br Back-right controller ID
	 */
	public DriveTrain(DriveTrain.DriveTrainLayout layout) { 
        //This should eventually be fully configurable
        // At this point it's based on what the layout is

        this.layout = layout;
        switch(layout) {
            case LEGACY:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
	        	leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercTalonSRX(CAN.DRIVETRAIN_SL);
                followerRight = new MercTalonSRX(CAN.DRIVETRAIN_SR);
                break;/*
            case SPARKS:
                leaderLeft = new MercSparkMax(CAN.DRIVETRAIN_ML);
                leaderRight = new MercSparkMax(CAN.DRIVETRAIN_MR);
                followerLeft = new MercSparkMax(CAN.DRIVETRAIN_SL);
                followerRight = new MercSparkMax(CAN.DRIVETRAIN_SR);
                break;*/
			case TALONS:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
	        	leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercVictorSPX(CAN.DRIVETRAIN_SL);
				followerRight = new MercVictorSPX(CAN.DRIVETRAIN_SR);
                break;
        }

        //Initialize the gyro that is currently on the robot. Comment out the initialization of the one not in use.
        gyroSPI = new ADXRS450_Gyro();

        //Initialize podgeboi
        podgeboi = new PigeonIMU(CAN.PIGEON);
        podgeboi.configFactoryDefault();

        //Initialize RightSight
        rightSight = new RightSight(0);

        //Initialize the CANifier and LIDAR
        canifier = new CANifier(RobotMap.CAN.CANIFIER);
        lidar = new LIDAR(canifier, CANifier.PWMChannel.PWMChannel0, PWMOffset.DEFAULT);

        //Account for motor orientation.
        leaderLeft.setInverted(false);
        followerLeft.setInverted(false);
        leaderRight.setInverted(true);
        followerRight.setInverted(true);

        //Set neutral mode
        setNeutralMode(NeutralMode.Brake);

        //Account for encoder orientation.
        leaderLeft.setSensorPhase(true);
        leaderRight.setSensorPhase(true);

        //Config feedback sensors for each PID slot, ready for MOTION PROFILING
        initializeMotionMagicFeedback();

        // Config PID
        DRIVE_GAINS = new PIDGain(0.1, 0.0, 0.0, 0.0, .95);
        SMOOTH_GAINS = new PIDGain(2.0, 0.0, 4.0, getFeedForward(), 1.0);
        MOTION_PROFILE_GAINS = new PIDGain(0.6, 0.0, 0.0, getFeedForward(), 1.0);
        TURN_GAINS = new PIDGain(0.25, 0.0, 0.27, 0.0, 1.0);
        leaderRight.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        leaderLeft.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        leaderRight.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        leaderLeft.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        leaderRight.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        leaderLeft.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        leaderRight.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);
        leaderLeft.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);

        //Reset encoders
        resetEncoders();

        //Initialize drive with joysticks
        drive = new DriveAssist(leaderLeft, leaderRight, DriveDirection.FORWARD);
        
        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(leaderLeft);
        followerRight.follow(leaderRight);
        
        stop();
        configVoltage(NOMINAL_OUT, PEAK_OUT);
        setMaxOutput(PEAK_OUT);
    }

    public void initializeNormalMotionFeedback() {
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT);
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_TURN_SLOT);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_TURN_SLOT);

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
        getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR , 5);

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

    public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoysticks(DriveWithJoysticks.DriveType.ARCADE));
    }
    
    public void resetEncoders() {
        leaderLeft.resetEncoder();
        leaderRight.resetEncoder();
    }

    @Override
    public void periodic() {
        lidar.updatePWMInput();
        updateLEDs();
    }

    private void updateLEDs() {
        if(lidar.getDistance() <= CARGO_INTAKE_THRESHOLD) {
            setLEDColor(LEDColor.BLUE);
        }
        else {
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

    public LEDColor getCurrentLEDOutput() {
        return currentLEDColor;
    }

    /**
     * Stops the drive train.
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

    public void setDirection(DriveDirection dd) {
        drive.setDirection(dd);
    }

    public DriveDirection getDirection() {
        return drive.getDirection();
    }

    /**
     * Gets the gyro being used by the drive train.
     *
     * @return The Analog Gyro currently in use on the robot
     */
    public Gyro getSPIGyro() {
        if(gyroSPI == null) {
            log.error("SPI Gyro was not initialized!");
        } 
        return gyroSPI;
    }

    public PigeonIMU getPigeon() {
        if(podgeboi == null) {
            log.error("PigeonIMU was not initialized!");
        }
        return podgeboi;
    }

    public RightSight getRightSight() {
        if(rightSight == null) {
            log.error("Right Sight was not initialized!");
        }
        return rightSight;
    }

    public LIDAR getLidar() {
        if(lidar == null) {
            log.error("LIDAR was not initialized!");
        }
        return lidar;
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

    public void setFullSpeed() {
        leaderLeft.setSpeed(1);
        leaderRight.setSpeed(1);
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
        return drive;
    }

    public double getFeedForward() {
        return MercMath.calculateFeedForward(MAX_RPM);
    }

    public void pidWrite(double output) {
        drive.tankDrive(output, -output);
    }

    public void setMaxOutput(double maxOutput) {
        drive.setMaxOutput(maxOutput);
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        leaderLeft.setNeutralMode(neutralMode);
        leaderRight.setNeutralMode(neutralMode);
        followerLeft.setNeutralMode(neutralMode);
        followerRight.setNeutralMode(neutralMode);
    }
}
