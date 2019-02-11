package frc.robot.util;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.Robot;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class MercTalonSRX implements IMercMotorController {
    private WPI_TalonSRX talonsrx;
    private int port;
    private int timeoutms = 20;

    public MercTalonSRX(int port) {
        talonsrx = new WPI_TalonSRX(port);
        this.port = port;
        talonsrx.configFactoryDefault();
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
    public void resetEncoder() {
        talonsrx.getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public double getClosedLoopError() {
        return talonsrx.getClosedLoopError(0);
    }

    @Override
    public void configPID(int slot, PIDGain gains) {
        talonsrx.config_kP(slot, gains.kP, 10);
        talonsrx.config_kI(slot, gains.kI, 10);
        talonsrx.config_kD(slot, gains.kD, 10);
        talonsrx.config_kF(slot, gains.kF, 10);
    }

    @Override
    public void configVoltage(double nominalOutput, double peakOutput) {
        talonsrx.configNominalOutputForward(nominalOutput, RobotMap.CTRE_TIMEOUT);
        talonsrx.configNominalOutputReverse(-nominalOutput, RobotMap.CTRE_TIMEOUT);
        talonsrx.configPeakOutputForward(peakOutput, RobotMap.CTRE_TIMEOUT);
        talonsrx.configPeakOutputReverse(-peakOutput, RobotMap.CTRE_TIMEOUT);
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
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError) {
        talonsrx.configAllowableClosedloopError(slotIdx, allowableCloseLoopError, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
        talonsrx.configSelectedFeedbackSensor(feedbackDevice, pidIdx, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
        talonsrx.configSetParameter(param, value, subValue, ordinal, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection limitSwitchDirection) {
        if(limitSwitchDirection == LimitSwitchDirection.FORWARD) {
            return talonsrx.getSensorCollection().isFwdLimitSwitchClosed();
        }
        return talonsrx.getSensorCollection().isRevLimitSwitchClosed();
    }

    @Override
    public void configSensorTerm(SensorTerm st, FeedbackDevice fd) {
        talonsrx.configSensorTerm(st, fd, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configRemoteFeedbackFilter(int deviceID, RemoteSensorSource rss, int remoteSlotIdx) {
        talonsrx.configRemoteFeedbackFilter(deviceID, rss, remoteSlotIdx);
    }

    @Override
    public void configSelectedFeedbackCoefficient(double fdbkScale, int pidIdx) {
        talonsrx.configSelectedFeedbackCoefficient(fdbkScale, pidIdx, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void setStatusFramePeriod(StatusFrame sf, int statusms) {
        talonsrx.setStatusFramePeriod(sf, statusms, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void selectProfileSlot(int slotIdx, int pidIdx) {
        talonsrx.selectProfileSlot(slotIdx, pidIdx);
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
