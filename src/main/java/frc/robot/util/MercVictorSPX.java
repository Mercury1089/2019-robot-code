package frc.robot.util;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 */
public class MercVictorSPX implements IMercMotorController {
    private WPI_VictorSPX victorspx;
    private int port;
    private int timeoutms = 20;

    public MercVictorSPX(int port) {
        victorspx = new WPI_VictorSPX(port);
        this.port = port;
        victorspx.configFactoryDefault();
    }

    @Override
    public void setSpeed(double speed) {
        victorspx.set(ControlMode.PercentOutput, speed);
    }

    @Override
    public void setPosition(double ticks) {
        victorspx.set(ControlMode.Position, ticks);
    }

    @Override
    public double getSpeed() {
        return victorspx.get();
    }
   
    @Override
    public void setInverted(boolean invert) {
        victorspx.setInverted(invert);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void follow(IMercMotorController leader) {
        if (leader instanceof MercTalonSRX) {
            victorspx.follow(((MercTalonSRX)leader).get());
        }
    }

    @Override
    public void stop() {
        victorspx.stopMotor();
    }

    @Override
    public double getEncTicks() {
        return 0;
    }

    @Override
    public double getEncVelo() {
        return 0;
    }

    @Override
    public void resetEncoder() {
        return;
    }

    @Override
    public double getClosedLoopError() {
        return victorspx.getClosedLoopError(0);
    }

    @Override
    public void configPID(int slot, PIDGain gains) {
        victorspx.config_kP(slot, gains.kP, 10);
        victorspx.config_kI(slot, gains.kI, 10);
        victorspx.config_kD(slot, gains.kD, 10);
        victorspx.config_kF(slot, gains.kF, 10);
    }

    @Override
    public void configVoltage(double nominalOutput, double peakOutput) {
        victorspx.configNominalOutputForward(nominalOutput, RobotMap.CTRE_TIMEOUT);
        victorspx.configNominalOutputReverse(-nominalOutput, RobotMap.CTRE_TIMEOUT);
        victorspx.configPeakOutputForward(peakOutput, RobotMap.CTRE_TIMEOUT);
        victorspx.configPeakOutputReverse(-peakOutput, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        victorspx.setNeutralMode(neutralMode);
    }

    @Override
    public void setSensorPhase(boolean sensorPhase) {
        victorspx.setSensorPhase(sensorPhase);
    }

    @Override
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError) {
        victorspx.configAllowableClosedloopError(slotIdx, allowableCloseLoopError, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
        victorspx.configSelectedFeedbackSensor(feedbackDevice, pidIdx, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
        victorspx.configSetParameter(param, value, subValue, ordinal, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public boolean isFwdLimitSwitchClosed() {
        return false;
    }

    @Override
    public boolean isRevLimitSwitchClosed() {
        return false;
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection limitSwitchDirection) {
        return false;
    }

    @Override
    public void configSensorTerm(SensorTerm st, FeedbackDevice fd) {
        victorspx.configSensorTerm(st, fd, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void configRemoteFeedbackFilter(int deviceID, RemoteSensorSource rss, int remoteSlotIdx) {
        victorspx.configRemoteFeedbackFilter(deviceID, rss, remoteSlotIdx);
    }

    @Override
    public void configSelectedFeedbackCoefficient(double fdbkScale, int pidIdx) {
        victorspx.configSelectedFeedbackCoefficient(fdbkScale, pidIdx, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void setStatusFramePeriod(StatusFrame sf, int statusms) {
        victorspx.setStatusFramePeriod(sf, statusms, RobotMap.CTRE_TIMEOUT);
    }

    @Override
    public void selectProfileSlot(int slotIdx, int pidIdx) {
        victorspx.selectProfileSlot(slotIdx, pidIdx);
    }

//_________________________________________________________________________________
    /**
     * Get the VictorSPX tied to this class
     * @return the Victor
     */
    public WPI_VictorSPX get() {
        return victorspx;
    }
}
