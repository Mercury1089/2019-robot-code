package frc.robot.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.NeutralMode;

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
    public double getEncPos() {
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
    public void configPID(double p, double i, double d, double f) {
        victorspx.config_kP(DriveTrain.SLOT_0, p, DriveTrain.TIMEOUT_MS);
        victorspx.config_kI(DriveTrain.SLOT_0, i, DriveTrain.TIMEOUT_MS);
        victorspx.config_kD(DriveTrain.SLOT_0, d, DriveTrain.TIMEOUT_MS);
        victorspx.config_kF(DriveTrain.SLOT_0, f, DriveTrain.TIMEOUT_MS);
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

//_________________________________________________________________________________
    /**
     * Get the VictorSPX tied to this class
     * @return the Victor
     */
    public WPI_VictorSPX get() {
        return victorspx;
    }
}
