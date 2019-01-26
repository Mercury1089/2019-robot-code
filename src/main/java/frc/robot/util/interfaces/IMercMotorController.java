package frc.robot.util.interfaces;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public interface IMercMotorController {
    /**
     * Sets the relative speed of the controller. 
     * 
     * @param speed speed from -1 to 1
     */
    public void setSpeed(double x);

    /**
     * Sets relative position of controller
     * 
     * @param position the final position in ticks
     */
    public void setPosition(double x);

    /**
     * Want the speed of the controller?
     * 
     * @return the speed of the controller
     */
    public double getSpeed();

    /**
     * Sets a motor controller inverted.
     * 
     * @param invert Whether to invert or not
     */
    public void setInverted(boolean invert);

    /**
     * Follows the motor controller that is passed in. Will not do anything if it has to follow anything else.
     * 
     * @param leader the controller to follow
     */
    public void follow(IMercMotorController leader);

    /**
     * Want the CAN ID of this controller?
     * 
     * @return the CAN ID of this controller
     */
    public int getPort();

    /**
     * STOP! You have been caught singing despacito 9: yodeling kid edition
     */
    public void stop();

    /**
     * Want the encoder position of the controller?
     * NOTE: Victors can't connect to another sensor like an encoder, so it will return 0 for MercVictorSPX.
     * 
     * @return the encoder position of the controller
     */
    public double getEncTicks();

    /**
     * Want the encoder velocity of the controller?
     * NOTE: Victors can't connect to another sensor like an encoder, so it will return 0 for MercVictorSPX.
     * 
     * @return the encoder velocity of the controller
     */
    public double getEncVelo();

    /**
     * Want the error from setpoint?
     * NOTE: Not to be used with Victors (no connected encoder)
     * 
     * @return the error from setpoint in ticks
     */
    public double getClosedLoopError();

    /**
     * Configure PID for the motor controller
     * 
     * @param p Proportional
     * @param i Integral
     * @param d Derivative
     * @param f Feed Forward
     */
    public void configPID(double p, double i, double d, double f);

    /**
     * Configure the min and max voltage.
     * NOTE: for sparks, this is only for closed loop control. For talons, it is for both.
     * 
     * @param nominalOutput min voltage
     * @param peakOutput max voltage
     */
    public void configVoltage(double nominalOutput, double peakOutput);

    /**
     * Set the neutral mode - brake or coast
     * 
     * @param nm the NeutralMode to set it to
     */
    public void setNeutralMode(NeutralMode neutralMode);

    /**
     * 
     */
    public void setSensorPhase(boolean bool);

    /**
     * 
     */
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError, int timeoutMs);

    public void configSelectedFeedbackSensor(FeedbackDevice FeedbackDevice, int pidIdx, int timeoutMs);

    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs);

    public boolean isLimitSwitchClosed();
}