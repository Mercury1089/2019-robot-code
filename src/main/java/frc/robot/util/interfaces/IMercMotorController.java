package frc.robot.util.interfaces;


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
    public double getEncPos();

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
}