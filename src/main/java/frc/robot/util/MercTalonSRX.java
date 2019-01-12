package frc.robot.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.util.interfaces.IMercMotorController;

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
        if (leader instanceof MercTalonSRX)
            talonsrx.follow(((MercTalonSRX)leader).get());
    }

    @Override
    public void stop() {
        talonsrx.stopMotor();
    }

    @Override
    public double getEncPos() {
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
        talonsrx.config_kP(DriveTrain.SLOT_0, p, DriveTrain.TIMEOUT_MS);
        talonsrx.config_kI(DriveTrain.SLOT_0, i, DriveTrain.TIMEOUT_MS);
        talonsrx.config_kD(DriveTrain.SLOT_0, d, DriveTrain.TIMEOUT_MS);
        talonsrx.config_kF(DriveTrain.SLOT_0, f, DriveTrain.TIMEOUT_MS);
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

//_________________________________________________________________________________
    /**
     * Get the TalonSRX tied to this class
     * @return the Talon
     */
    public WPI_TalonSRX get() {
        return talonsrx;
    }
}
