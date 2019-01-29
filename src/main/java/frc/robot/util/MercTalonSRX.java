package frc.robot.util;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class MercTalonSRX implements IMercMotorController {
    private WPI_TalonSRX talonsrx;
    private int port;

    public MercTalonSRX(int port) {
        talonsrx = new WPI_TalonSRX(port);
        this.port = port;
    }

    @Override
    public void setSpeed(double speed) {
        talonsrx.set(ControlMode.PercentOutput, speed);
    }

    @Override
    public void setPosition(double ticks) {
        talonsrx.set(ControlMode.Position, ticks);
    }

    @Override
    public double getSpeed() {
        return talonsrx.get();
    }
    
    @Override
    public void setInverted(boolean invert) {
        talonsrx.setInverted(invert);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void follow(IMercMotorController leader) {
        if (Robot.driveTrain.getLayout() != DriveTrainLayout.SPARKS)
            talonsrx.follow(((MercTalonSRX)leader).get());
    }

    @Override
    public void stop() {
        talonsrx.stopMotor();
    }

    @Override
    public double getEncTicks() {
        return talonsrx.getSelectedSensorPosition(0);
    }

    @Override
    public double getEncVelo() {
        return talonsrx.getSelectedSensorVelocity(0);
    }

    @Override
    public double getClosedLoopError() {
        return talonsrx.getClosedLoopError(0);
    }

    @Override
    public void configPID(double p, double i, double d, double f) {
        System.out.println("SETTING PIDF STARTING - kP " + System.currentTimeMillis());
        talonsrx.config_kP(DriveTrain.SLOT_0, p, DriveTrain.TIMEOUT_MS);
        //System.out.println("SETTING PIDF ENDING - kP; SETTING I " + System.currentTimeMillis());
        talonsrx.config_kI(DriveTrain.SLOT_0, i, DriveTrain.TIMEOUT_MS);
        //System.out.println("SETTING PIDF ENDING - kI; SETTING D " + System.currentTimeMillis());
        talonsrx.config_kD(DriveTrain.SLOT_0, d, DriveTrain.TIMEOUT_MS);
        //System.out.println("SETTING PIDF ENDING - kD; SETTING F " + System.currentTimeMillis());
        talonsrx.config_kF(DriveTrain.SLOT_0, f, DriveTrain.TIMEOUT_MS);
        System.out.println("SETTING PIDF ENDING - kF (Done) " + System.currentTimeMillis());
    }

    @Override
    public void configVoltage(double nominalOutput, double peakOutput) {
        talonsrx.configNominalOutputForward(nominalOutput, DriveTrain.TIMEOUT_MS);
        talonsrx.configNominalOutputReverse(-nominalOutput, DriveTrain.TIMEOUT_MS);
        talonsrx.configPeakOutputForward(peakOutput, DriveTrain.TIMEOUT_MS);
        talonsrx.configPeakOutputReverse(-peakOutput, DriveTrain.TIMEOUT_MS);
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        talonsrx.setNeutralMode(neutralMode);
    }

    @Override
    public void setSensorPhase(boolean sensorPhase) {
        talonsrx.setSensorPhase(sensorPhase);
    }

    @Override
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
        talonsrx.configAllowableClosedloopError(slotIdx, allowableCloseLoopError, timeoutMs);
    }

    @Override
    public void configSelectedFeedbackSensor(FeedbackDevice FeedbackDevice, int pidIdx, int timeoutMs) {
        talonsrx.configSelectedFeedbackSensor(FeedbackDevice, pidIdx, timeoutMs);
    }

    @Override
    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
        talonsrx.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    @Override
    public boolean isLimitSwitchClosed() {
        return talonsrx.getSensorCollection().isRevLimitSwitchClosed();
    }

//_________________________________________________________________________________
    /**
     * Get the TalonSRX tied to this class
     * @return the Talon
     */
    public WPI_TalonSRX get() {
        return talonsrx;
    }
}
