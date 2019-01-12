package frc.robot.util;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMax.ExternalFollower;

import frc.robot.util.interfaces.IMercMotorController;

public class MercSparkMax implements IMercMotorController {
    private CANSparkMax sparkmax;
    private int port;

    public MercSparkMax(int port) {
        sparkmax = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
        this.port = port;
    }

    /**
     * Sets the relative speed of the controller. 
     * 
     * @param speed: speed from -1 to 1
     */
    @Override
    public void setSpeed(double speed) {
        sparkmax.set(speed);
    }

    @Override
    public double getSpeed() {
        return sparkmax.get();
    }
    
    @Override
    public void setInverted(boolean invert) {
        sparkmax.setInverted(invert);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void follow(IMercMotorController leader) {
        if (leader instanceof MercSparkMax)
            sparkmax.follow(((MercSparkMax)leader).get());
        else
            sparkmax.follow((ExternalFollower)leader, leader.getPort());    //Def the wrong way to do this please check
    }

    @Override
    public void stop() {
        sparkmax.stopMotor();
    }

//_________________________________________________________________________________
    /**
     * Get the Spark Max tied to this class
     * @return the Spark
     */
    public CANSparkMax get() {
        return sparkmax;
    }
}