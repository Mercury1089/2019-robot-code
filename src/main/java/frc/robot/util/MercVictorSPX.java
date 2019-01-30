package frc.robot.util;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.util.interfaces.IMercMotorController;

/**
 * Add your docs here.
 */
public class MercVictorSPX implements IMercMotorController {
    private WPI_VictorSPX victorspx;
    private int port;

    public MercVictorSPX(int port) {
        victorspx = new WPI_VictorSPX(port);
        this.port = port;
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
        victorspx.configNominalOutputForward(nominalOutput, DriveTrain.TIMEOUT_MS);
        victorspx.configNominalOutputReverse(-nominalOutput, DriveTrain.TIMEOUT_MS);
        victorspx.configPeakOutputForward(peakOutput, DriveTrain.TIMEOUT_MS);
        victorspx.configPeakOutputReverse(-peakOutput, DriveTrain.TIMEOUT_MS);
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
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
        victorspx.configAllowableClosedloopError(slotIdx, allowableCloseLoopError, timeoutMs);
    }

    @Override
    public void configSelectedFeedbackSensor(FeedbackDevice FeedbackDevice, int pidIdx, int timeoutMs) {
        victorspx.configSelectedFeedbackSensor(FeedbackDevice, pidIdx, timeoutMs);
    }

    @Override
    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
        victorspx.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    @Override
    public boolean isLimitSwitchClosed() {
        return false;
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
