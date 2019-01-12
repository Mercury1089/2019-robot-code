package frc.robot.util;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import frc.robot.util.interfaces.IMercMotorController;

public class MercSparkMax implements IMercMotorController {
    private CANSparkMax sparkmax;

    public MercSparkMax(int port) {
        sparkmax = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
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
}