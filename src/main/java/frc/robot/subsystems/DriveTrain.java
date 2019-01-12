package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import frc.robot.commands.DriveWithJoysticks;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.MercMath;
import frc.robot.util.MercSparkMax;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.DriveAssist;
import frc.robot.util.config.DriveTrainSettings;

/**
 * Subsystem that encapsulates the drive train.
 * This contains the {@link DriveAssist} needed to drive manually
 * using the motor controllers.
 */
public class DriveTrain extends Subsystem implements PIDOutput {
    private Logger log = LogManager.getLogger(DriveTrain.class);
    public static final int TIMEOUT_MS = 10;
    public static final int SLOT_0 = 0;
    public static final int PRIMARY_PID_LOOP = 0;

    public static final double MAX_SPEED = 1.0;
    public static final double MIN_SPEED = .65;

    private IMercMotorController masterLeft, masterRight, followerLeft, followerRight;

    private DriveAssist tDrive;
    // private NavX navX;
    private ADXRS450_Gyro gyroSPI;

	public static final int MAG_ENCODER_TICKS_PER_REVOLUTION = 4096;
	public static final double GEAR_RATIO;
    public static final double MAX_RPM;
    public static final double WHEEL_DIAMETER_INCHES;
    public static final double NOMINAL_OUT = 0.0, PEAK_OUT = 1.0;

    private DriveTrainLayout layout;

    static {
        GEAR_RATIO = DriveTrainSettings.getGearRatio();
        MAX_RPM = DriveTrainSettings.getMaxRPM();
        WHEEL_DIAMETER_INCHES = DriveTrainSettings.getWheelDiameter();
    }
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
	public DriveTrain(DriveTrain.DriveTrainLayout layout, int fl, int fr, int bl, int br) {
        // At this point it's based on what the layout is
        this.layout = layout;
        switch(layout) {
            case LEGACY:
                masterLeft = new MercTalonSRX(fl);
	        	masterRight = new MercTalonSRX(fr);
                followerLeft = new MercTalonSRX(bl);
                followerRight = new MercTalonSRX(br);
                break;
            case SPARKS:
                masterLeft = new MercSparkMax(fl);
                masterRight = new MercSparkMax(fr);
                followerLeft = new MercSparkMax(bl);
                followerRight = new MercSparkMax(br);
			case TALONS:
                masterLeft = new MercTalonSRX(fl);
	        	masterRight = new MercTalonSRX(fr);
				followerLeft = new MercVictorSPX(bl);
				followerRight = new MercVictorSPX(br);
				break;
        }

        //Initialize the gyro that is currently on the robot. Comment out the initialization of the one not in use.
        // navX = new NavX(SerialPort.Port.kUSB1);
        gyroSPI = new ADXRS450_Gyro();

        //Account for motor orientation.
        masterLeft.setInverted(true);
        followerLeft.setInverted(true);
        masterRight.setInverted(false);
        followerRight.setInverted(false);

        setNeutralMode(NeutralMode.Brake);

        if (!(layout == DriveTrainLayout.SPARKS)) {
            TalonSRX left = ((MercTalonSRX)masterLeft).get();
            TalonSRX right = ((MercTalonSRX)masterRight).get();

            //Account for encoder orientation.
            left.setSensorPhase(true);
            right.setSensorPhase(true);

            // Set up feedback sensors
            // Using CTRE_MagEncoder_Relative allows for relative ticks when encoder is zeroed out.
            // This allows us to measure the distance from any given point to any ending point.
            left.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_PID_LOOP, TIMEOUT_MS);
            right.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_PID_LOOP, TIMEOUT_MS);

            //Reset encoders (can't do this with Sparks)
            left.getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
            right.getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
        }

        tDrive = new DriveAssist(masterLeft, masterRight);

        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(masterLeft);
        followerRight.follow(masterRight);

        

        configVoltage(0, DriveTrainSettings.getMaxOutput());
        setMaxOutput(DriveTrainSettings.getMaxOutput());
    }

    public void resetEncoders() {
        if (masterLeft instanceof MercTalonSRX) {
            ((MercTalonSRX)masterLeft).get().getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
            ((MercTalonSRX)masterRight).get().getSensorCollection().setQuadraturePosition(0, TIMEOUT_MS);
        }
    }

    /**
     * Stops the motor.
     */
    public void stop() {
        masterLeft.stop();
        masterRight.stop();
    }

    public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoysticks(DriveWithJoysticks.DriveType.ARCADE));
    }

    /**
     * Sets both of the front talons to have a forward output of nominalOutput and peakOutput with the reverse output setClawState to the negated outputs.
     *
     * @param nominalOutput The desired nominal voltage output of the left and right talons, both forward and reverse.
     * @param peakOutput    The desired peak voltage output of the left and right talons, both forward and reverse
     */
    public void configVoltage(double nominalOutput, double peakOutput) {
        if (masterLeft instanceof MercTalonSRX) {
            TalonSRX left = ((MercTalonSRX)masterLeft).get();
            TalonSRX right = ((MercTalonSRX)masterRight).get();

            left.configNominalOutputForward(nominalOutput, TIMEOUT_MS);
            left.configNominalOutputReverse(-nominalOutput, TIMEOUT_MS);
            left.configPeakOutputForward(peakOutput, TIMEOUT_MS);
            left.configPeakOutputReverse(-peakOutput, TIMEOUT_MS);
            right.configNominalOutputForward(nominalOutput, TIMEOUT_MS);
            right.configNominalOutputReverse(-nominalOutput, TIMEOUT_MS);
            right.configPeakOutputForward(peakOutput, TIMEOUT_MS);
            right.configPeakOutputReverse(-peakOutput, TIMEOUT_MS);
        }
    }

    /**
     * Gets the gyro being used by the drive train.
     *
     * @return The gyro, either the NavX or Analog Gyro, currently in use on the robot
     */
    public Gyro getGyro() {
       /* if (navX != null) {
            return navX;
        } else */
        if (gyroSPI != null) {
            return gyroSPI;
        } else {
            return null;
        }
    }

    public int getLeftEncPositionInTicks() {
        return tMasterLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP);
    }

    public double getRightEncPositionInTicks() {
        return tMasterRight.getSelectedSensorPosition(PRIMARY_PID_LOOP);
    }

    public double getLeftEncPositionInFeet() {
        return MercMath.getEncPosition(getLeftEncPositionInTicks());
    }

    public double getRightEncPositionInFeet() {
        return MercMath.getEncPosition(getRightEncPositionInTicks());
    }

    public TalonSRX getLeft() {
        return tMasterLeft;
    }

    public TalonSRX getRight() {
        return tMasterRight;
    }

    public TalonDrive getTalonDrive() {
        return tDrive;
    }

    public double getFeedForward() {
        return MercMath.calculateFeedForward(MAX_RPM);
    }

    public void pidWrite(double output) {
        tDrive.tankDrive(output, -output);
    }

    public void setMaxOutput(double maxOutput) {
        tDrive.setMaxOutput(maxOutput);
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        if (!(layout == DriveTrainLayout.SPARKS)) {
            ((MercTalonSRX)masterLeft).get().setNeutralMode(neutralMode);
            ((MercTalonSRX)masterRight).get().setNeutralMode(neutralMode);
            if (layout == DriveTrainLayout.TALONS) {
                ((MercVictorSPX)followerLeft).get().setNeutralMode(neutralMode);
                ((MercVictorSPX)followerRight).get().setNeutralMode(neutralMode);
            } else if (layout == DriveTrainLayout.LEGACY) {
                ((MercTalonSRX)followerLeft).get().setNeutralMode(neutralMode);
                ((MercTalonSRX)followerRight).get().setNeutralMode(neutralMode);
            }
        } else {
            CANSparkMax.IdleMode mode;
            if (neutralMode == NeutralMode.Brake) {
                mode = IdleMode.kBrake;
            } else {
                mode = IdleMode.kCoast;
            }
            ((MercSparkMax)masterLeft).get().setIdleMode(mode);
            ((MercSparkMax)masterRight).get().setIdleMode(mode);
            ((MercSparkMax)followerLeft).get().setIdleMode(mode);
            ((MercSparkMax)followerRight).get().setIdleMode(mode);
        }
    }
}
