package frc.robot.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

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

    /**
     * Follows the TalonSRX that is passed in. Will not do anything if it has to follow anything else.
     */
    @Override
    public void follow(IMercMotorController leader) {
        if (leader instanceof MercTalonSRX)
            talonsrx.follow(((MercTalonSRX)leader).get());
    }

    @Override
    public void stop() {
        talonsrx.stopMotor();
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
