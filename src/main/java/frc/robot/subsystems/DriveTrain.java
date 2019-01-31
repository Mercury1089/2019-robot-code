package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

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
import frc.robot.sensors.LimeLight;

/**
 * Subsystem that encapsulates the drive train.
 * This contains the {@link DriveAssist} needed to drive manually
 * using the motor controllers.
 */
public class DriveTrain extends Subsystem implements PIDOutput {
    private Logger log = LogManager.getLogger(DriveTrain.class);
    public static final int TIMEOUT_MS = 10;
    public static final int DRIVE_PID_SLOT = 0;
    public static final int DRIVE_SMOOTH_MOTION_SLOT = 1;
    public static final int REMOTE_DEVICE_1 = 0;
    public static final int PRIMARY_LOOP = 0;
    public static final int AUXILIARY_LOOP = 1;

    public static final double MAX_SPEED = 1;
    public static final double MIN_SPEED = -1;

    private IMercMotorController masterLeft, masterRight, followerLeft, followerRight;

    private DriveAssist drive;
    private ADXRS450_Gyro gyroSPI;
    private PigeonIMU podgeboi;
    private LimeLight limelight;

	public static final int MAG_ENCODER_TICKS_PER_REVOLUTION = 4096, NEO_ENCODER_TICKS_PER_REVOLUTION = 42;
	public static final double GEAR_RATIO = 1;                   //TEMP
    public static final double MAX_RPM = 700.63;                    //TEMP
    public static final double WHEEL_DIAMETER_INCHES = 5.125;       //TEMP eventually make this stuff configurable in shuffledash
    public static final double NOMINAL_OUT = 0.0, PEAK_OUT = 1.0;

    private DriveTrainLayout layout;
    private final PIDGain DRIVE_GAINS, SMOOTH_GAINS;

    public enum DriveTrainLayout {
        SPARKS,
        TALONS,
        LEGACY
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

        //Initialize LimeLight
        limelight = new LimeLight();

        //Account for motor orientation.
        masterLeft.setInverted(true);
        followerLeft.setInverted(true);
        masterRight.setInverted(false);
        followerRight.setInverted(false);

        setNeutralMode(NeutralMode.Brake);

        if (layout != DriveTrainLayout.SPARKS) {
            WPI_TalonSRX left = ((MercTalonSRX)masterLeft).get();
            WPI_TalonSRX right = ((MercTalonSRX)masterRight).get();

            //Account for encoder orientation.
            left.setSensorPhase(true);
            right.setSensorPhase(true);

            // Set up feedback sensors
            // Using CTRE_MagEncoder_Relative allows for relative ticks when encoder is zeroed out.
            // This allows us to measure the distance from any given point to any ending point.
            left.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT, TIMEOUT_MS);
            right.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_PID_SLOT, TIMEOUT_MS);
            left.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT, TIMEOUT_MS);
            right.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DRIVE_SMOOTH_MOTION_SLOT, TIMEOUT_MS);

            resetEncoders();
        }

        drive = new DriveAssist(masterLeft, masterRight);
        
        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(masterLeft);
        followerRight.follow(masterRight);

        // Config PID
        DRIVE_GAINS = new PIDGain(0.025, 0.0, 0.05, 0.0);
        SMOOTH_GAINS = new PIDGain(0.6, 0.0, 0.0, getFeedForward());
        masterRight.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        masterLeft.configPID(DRIVE_PID_SLOT, DRIVE_GAINS);
        masterRight.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        masterLeft.configPID(DRIVE_SMOOTH_MOTION_SLOT, SMOOTH_GAINS);
        
        stop();
        configVoltage(NOMINAL_OUT, PEAK_OUT);
        setMaxOutput(PEAK_OUT);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoysticks(DriveWithJoysticks.DriveType.ARCADE));
    }
    
    public void resetEncoders() {
        //Reset encoders (can't do this with Sparks)
        if (layout != DriveTrainLayout.SPARKS)
        ((MercTalonSRX)(masterLeft)).get().getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
        ((MercTalonSRX)(masterRight)).get().getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
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
     * @return The gyro, either the NavX or Analog Gyro, currently in use on the robot
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
    
    public LimeLight getLimeLight() {
        return limelight;
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

    public DriveTrainLayout getLayout() {
        return layout;
    }
}
