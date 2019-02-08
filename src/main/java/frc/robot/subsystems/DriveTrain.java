package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import frc.robot.util.PIDGain;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.DriveWithJoysticks;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.MercMath;
import frc.robot.util.MercSparkMax;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.DriveAssist;
import frc.robot.sensors.Limelight;
import frc.robot.sensors.RightSight;

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

    private IMercMotorController masterLeft, masterRight, followerLeft, followerRight;

    private DriveAssist drive;
    private ADXRS450_Gyro gyroSPI;
    private PigeonIMU podgeboi;
    private Limelight limelight;
    private RightSight rightSight;

    private DriveTrainLayout layout;

    private final PIDGain DRIVE_GAINS, SMOOTH_GAINS, MOTION_PROFILE_GAINS, TURN_GAINS;

    private boolean isInMotionMagicMode;

    public enum DriveTrainLayout {
        SPARKS,
        TALONS,
        LEGACY
    }

    public enum DriveTrainSide {
        RIGHT,
        LEFT
    }

	/**
	 * Creates the drivetrain, assuming that there are four controllers.
	 *
	 * @param fl Front-left controller ID
	 * @param fr Front-right controller ID
	 * @param bl Back-left controller ID
	 * @param br Back-right controller ID
	 */
	public DriveTrain(DriveTrain.DriveTrainLayout layout) { //This should eventually be fully configurable
        // At this point it's based on what the layout is

        this.layout = layout;
        switch(layout) {
            case LEGACY:
                masterLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
	        	masterRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercTalonSRX(CAN.DRIVETRAIN_SL);
                followerRight = new MercTalonSRX(CAN.DRIVETRAIN_SR);
                break;
            case SPARKS:
                masterLeft = new MercSparkMax(CAN.DRIVETRAIN_ML);
                masterRight = new MercSparkMax(CAN.DRIVETRAIN_MR);
                followerLeft = new MercSparkMax(CAN.DRIVETRAIN_SL);
                followerRight = new MercSparkMax(CAN.DRIVETRAIN_SR);
                break;
			case TALONS:
                masterLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
	        	masterRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercVictorSPX(CAN.DRIVETRAIN_SL);
				followerRight = new MercVictorSPX(CAN.DRIVETRAIN_SR);
				break;
        }

        //Initialize the gyro that is currently on the robot. Comment out the initialization of the one not in use.
        gyroSPI = new ADXRS450_Gyro();

        //Initialize podgeboi
        podgeboi = new PigeonIMU(CAN.PIGEON);

        podgeboi.configFactoryDefault();

        //Initialize LimeLight
        limelight = new Limelight();

        //Initialize RightSight
        rightSight = new RightSight(0);

        //Account for motor orientation.
        masterLeft.setInverted(false);
        followerLeft.setInverted(false);
        masterRight.setInverted(true);
        followerRight.setInverted(true);

        //Set neutral mode
        setNeutralMode(NeutralMode.Brake);

        //Account for encoder orientation.
        masterLeft.setSensorPhase(true);
        masterRight.setSensorPhase(true);

        //Config feedback sensors for each PID slot, ready for MOTION PROFILING
        initializeMotionMagicFeedback();

        // Config PID
        DRIVE_GAINS = new PIDGain(0.1, 0.0, 0.0, 0.0);
        SMOOTH_GAINS = new PIDGain(2.0, 0.0, 4.0, getFeedForward());
        MOTION_PROFILE_GAINS = new PIDGain(0.6, 0.0, 0.0, getFeedForward());
        TURN_GAINS = new PIDGain(0.05, 0.0, 0.025, 0.0);
        masterRight.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        masterLeft.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        masterRight.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        masterLeft.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        masterRight.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        masterLeft.configPID(DRIVE_MOTION_PROFILE_SLOT, MOTION_PROFILE_GAINS);
        masterRight.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);
        masterLeft.configPID(DRIVE_SMOOTH_TURN_SLOT, TURN_GAINS);

        //Reset encoders
        resetEncoders();

        //Initialize drive with joysticks
        drive = new DriveAssist(masterLeft, masterRight);
        
        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(masterLeft);
        followerRight.follow(masterRight);
        
        stop();
        configVoltage(NOMINAL_OUT, PEAK_OUT);
        setMaxOutput(PEAK_OUT);
    }

    public void initializeNormalMotionFeedback() {
        masterLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT);
        masterRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT);
        masterLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        masterRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT);
        masterLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);
        masterRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_MOTION_PROFILE_SLOT);

        isInMotionMagicMode = false;
    }

    public void initializeMotionMagicFeedback() {
        /* Configure left's encoder as left's selected sensor */
        masterLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DriveTrain.PRIMARY_LOOP);

        /* Configure the Remote Talon's selected sensor as a remote sensor for the right Talon */
        masterRight.configRemoteFeedbackFilter(masterLeft.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, DriveTrain.REMOTE_DEVICE_0);

        /* Configure the Pigeon IMU to the other remote slot available on the right Talon */
        masterRight.configRemoteFeedbackFilter(getPigeon().getDeviceID(), RemoteSensorSource.Pigeon_Yaw, DriveTrain.REMOTE_DEVICE_1);

        /* Setup Sum signal to be used for Distance */
        masterRight.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
        masterRight.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

        /* Configure Sum [Sum of both QuadEncoders] to be used for Primary PID Index */
        masterRight.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, DriveTrain.PRIMARY_LOOP);

        /* Scale Feedback by 0.5 to half the sum of Distance */
        masterRight.configSelectedFeedbackCoefficient(0.5, DriveTrain.PRIMARY_LOOP);

        /* Configure Remote 1 [Pigeon IMU's Yaw] to be used for Auxiliary PID Index */
        masterRight.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, DriveTrain.AUXILIARY_LOOP);

        /* Scale the Feedback Sensor using a coefficient */
        masterRight.configSelectedFeedbackCoefficient(1, DriveTrain.AUXILIARY_LOOP);

        /* Set status frame periods to ensure we don't have stale data */
        masterRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20);
        masterRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20);
        masterRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20);
        masterRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20);
        masterLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5);
        getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR , 5);

        isInMotionMagicMode = true;
    }

    public void configPIDSlots(DriveTrainSide dts, int primaryPIDSlot, int auxiliaryPIDSlot) {
        if (primaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                masterRight.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
            else
                masterLeft.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
        }
        if (auxiliaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                masterRight.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
            else
                masterLeft.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
        }
        
    }
    public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoysticks(DriveWithJoysticks.DriveType.ARCADE));
    }
    
    public void resetEncoders() {
        masterLeft.resetEncoder();
        masterRight.resetEncoder();
    }

    /**
     * Stops the drive train.
     */
    public void stop() {
        masterLeft.stop();
        masterRight.stop();
        if (layout == DriveTrainLayout.SPARKS) {
            // NOTE: CALLING STOP ON VICTOR FOLLOWERS BREAKS THEM OUT OF FOLLOW MODE !!
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
        masterLeft.configVoltage(nominalOutput, peakOutput);
        masterRight.configVoltage(nominalOutput, peakOutput);
    }

    /**
     * Gets the gyro being used by the drive train.
     *
     * @return The Analog Gyro currently in use on the robot
     */
    public Gyro getSPIGyro() {
        if (gyroSPI != null) {
            return gyroSPI;
        } else {
            return null;
        }
    }

    public PigeonIMU getPigeon() {
        if (podgeboi != null) {
            return podgeboi;
        } else {
            return null;
        }
    }

    public RightSight getRightSight() {
        return rightSight;
    }

    public double getPigeonYaw() {
        double[] currYawPitchRoll = new double[3];
        podgeboi.getYawPitchRoll(currYawPitchRoll);
        return currYawPitchRoll[0];
    }

    public Limelight getLimeLight() {
        return limelight;
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
        masterLeft.setSpeed(1);
        masterRight.setSpeed(1);
    }

    public double getLeftEncPositionInTicks() {
        return masterLeft.getEncTicks();
    }

    public double getRightEncPositionInTicks() {
        return masterRight.getEncTicks();
    }

    public double getLeftEncPositionInFeet() {
        return MercMath.getEncPosition(getLeftEncPositionInTicks());
    }

    public double getRightEncPositionInFeet() {
        return MercMath.getEncPosition(getRightEncPositionInTicks());
    }

    public IMercMotorController getLeft() {
        return masterLeft;
    }

    public IMercMotorController getRight() {
        return masterRight;
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
        masterLeft.setNeutralMode(neutralMode);
        masterRight.setNeutralMode(neutralMode);
        followerLeft.setNeutralMode(neutralMode);
        followerRight.setNeutralMode(neutralMode);
    }
}
