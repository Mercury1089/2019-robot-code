package frc.robot.sensors;

import com.ctre.phoenix.CANifier;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import frc.robot.util.MercMath;

/**
 * Wrapper class for the entire LIDAR system that we are using to check for distance.
 */
public class LIDAR implements PIDSource {
    private final double[] PWM_INPUT = new double[2];
    private CANifier canifier;
    private CANifier.PWMChannel pwmChannel;
    private PWMOffset equation;

    private LinearDigitalFilter linearDigitalFilter;

    /**
     * Creates a new LIDAR by defining both the CANifier PWM channel that the
     * LIDAR is connected to, as well as the ID of the CANifier that the
     * LIDAR is connected to.
     *
     * @param cFier   CANifier that LIDAR is connected to
     * @param channel PWMChannel of the LIDAR
     */
    public LIDAR(CANifier cFier, CANifier.PWMChannel channel, PWMOffset o) {
        canifier = cFier;
        pwmChannel = channel;
        equation = o;

        linearDigitalFilter = LinearDigitalFilter.movingAverage(this, 5);

        // canifier(channel.value, true);
    }

    ;

    /**
     * Updates the current duty cycle and period recieved
     * from the LIDAR.
     */
    public synchronized void updatePWMInput() {
        canifier.getPWMInput(pwmChannel, PWM_INPUT);
    }

    /**
     * Gets the distance from the LIDAR sensor.
     *
     * @return raw distance from LIDAR, with applied offset
     */
    @Override
    public synchronized double pidGet() {
        // Apply offset equation
        return equation.apply(getRawDistance());
    }

    /**
     * Get the distance reported by the LIDAR with a moving average provided by a LinearDigitalFilter
     *
     * @return the distance to the target
     */
    public synchronized double getDistance() {
        return linearDigitalFilter.pidGet();
    }

    /**
     * Gets the distance from the LIDAR sensor, no offset applied.
     *
     * @return the distance sensed from the LIDAR, in inches
     */
    public synchronized double getRawDistance() {
        // Convert microseconds to cm
        double cm = PWM_INPUT[0] / 10.0; // TODO: use a conversion method in MercMath
        double in = MercMath.centimetersToInches(cm);
        return in;
    }

    /**
     * Gets PWM period from LIDAR.
     *
     * @return PWM input period, in microseconds
     */
    public synchronized double getPeriod() {
        return PWM_INPUT[1];
    }

    /**
     * Get the PID source type
     */
    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    /**
     * Set the pid source type (Should not be implemented)
     */
    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {

    }

    public enum PWMOffset {
        EQUATION_A(-5.55, 1.0),
        EQUATION_B(-4.67, 1.02),
        EQUATION_C(-0.756, 0.996),
        EQUATION_D(-1.33, 1.02),
        DEFAULT(0, 0);

        private final double CONSTANT, COEFFICIENT;

        PWMOffset(double c, double coeff) {
            CONSTANT = c;
            COEFFICIENT = coeff;
        }

        /**
         * Applies the offset equation to the given value
         *
         * @param value the raw value
         * @return the new value, with the offset function applied to it.
         */
        public double apply(double value) {
            return COEFFICIENT * value + CONSTANT;
        }
    }
}
