package frc.robot.sensors;

import edu.wpi.first.networktables.*;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.util.MercMath;

/**
 * A wrapper class for limelight information from the network table.
 */
public class Limelight implements PIDSource, TableEntryListener {
    private NetworkTable nt; //finds the limelight network table
    private double numTargets, targetCenterXAngle, targetCenterYAngle, targetArea, horizontalLength, verticalLength;
    private boolean targetAcquired;

    /*
    * Coefficients and exponents to help find the distance of a target
    * Each equation is in the form ax^b where a is the coefficient
    * (Coeff) of the equation and b is the exponent (Exp). x is the 
    * variable, either vertical length (vert), horizontal length (horiz),
    * or area (area).
    */
    private final double vertCoeff = 164.0;
    private final double vertExp = -1.09;
    private final double horizCoeff = 308.0;
    private final double horizExp = -0.99;
    private final double areaCoeff = 6.6;
    private final double areaExp = -0.504;

    //WARNING: Arbitrary values below v
    private final double LIMELIGHT_TO_ROBOT_CENTER_CARGO_IN = 10; //Distance from the LL to the center of the cargo side
    private final double LIMELIGHT_TO_ROBOT_CENTER_CARGO_DEG = 30; //Angle from the LL to the center of the cargo side
    private final double LIMELIGHT_TO_ROBOT_CARGO_PLANE_IN = 8; //Distance from the LL to the plane of the cargo side
    private final double HALF_ROBOT_FRAME_WIDTH_INCHES = 9.5;

    public enum LimelightLEDState {
        ON(3.0),
        OFF(1.0),
        BLINKING(2.0),
        PIPELINE_DEFAULT(0.0);

        private double value;

        LimelightLEDState(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    /** 
     * Constucts the sensor and adds a listener to the table 
     */
    public Limelight() {
        nt = NetworkTableInstance.getDefault().getTable("limelight-merc");
        numTargets = nt.getEntry("tv").getDouble(0.0);
        targetCenterXAngle = nt.getEntry("tx").getDouble(0.0);
        targetCenterYAngle = nt.getEntry("ty").getDouble(0.0);
        targetArea = nt.getEntry("ta").getDouble(0.0);
        horizontalLength = nt.getEntry("thor").getDouble(0.0);
        verticalLength = nt.getEntry("tvert").getDouble(0.0);
        targetAcquired = nt.getEntry("tv").getDouble(0.0) == 0.0 ? false : true;
        nt.addEntryListener(this, EntryListenerFlags.kUpdate);
    }

    /**
     * @param nt is always the limelight network table in this case
     * @param key is the key of the entry that changed
     * @param ne is the entry that changed
     * @param nv is the value of the entry that changed
     * @param flags is the flag that occured which is always kUpdate in this case
     */
    @Override
    public void valueChanged(NetworkTable nt, String key, NetworkTableEntry ne, NetworkTableValue nv, int flags){
        synchronized(this){
            switch (key) {
                case "tx": {
                    targetCenterXAngle = nv.getDouble();
                    break; 
                }
                case "ty": {
                    targetCenterYAngle = nv.getDouble();
                    break;
                }
                case "ta": {
                    targetArea = nv.getDouble();
                    break;
                }
                case "tv": {
                    targetAcquired = nv.getDouble() != 0.0;
                }
                case "tl": {
                    numTargets = nv.getDouble();
                    break;
                }
                case "thor": {
                    horizontalLength = nv.getDouble();
                    break;
                }
                case "tvert": {
                    verticalLength = nv.getDouble();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    /**
     * u want the angle to target in x direction?
     * @return angle to target in x direction
     */
    public synchronized double getTargetCenterXAngle(){
        return this.targetCenterXAngle;
    }

    /**
     * u want the angle to target in y direction?
     * @return angle to target in y direction
     */
    public synchronized double getTargetCenterYAngle(){
        return this.targetCenterYAngle;
    }

    /**
     * u want the angle to target's area?
     * @return angle to target in x direction
     */
    public synchronized double getTargetArea(){
        return this.targetArea;
    }

    /**
     * u want the number of targets?
     * @return number of visible targets
     */
    public synchronized double getNumTargets(){
        return this.numTargets;
    }

    /**
     * u want the vertical length of the target?
     * @return the vertical length of the target
     */
    public synchronized double getVerticalLength() {
        return this.verticalLength;
    }

    /**
     * u want the number of targets?
     * @return number of visible targets
     */
    public synchronized double getHorizontalLength() {
        return this.horizontalLength;
    }

    /**
     * Set the PID Source (should not be implemented)
     */
    public synchronized void setPIDSourceType(PIDSourceType pidST){
        
    }

    /**
     * Get the PID Source Type (what kind of value PID is acting on)
     */
    public synchronized PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    /**
     * Get the value that PID acts on. For PIDCommand
     */
    public synchronized double pidGet() {
        return this.targetCenterXAngle;
    }

    /**
     * u want the distance based on the area?
     * @return the distance based on the area
     */
    public synchronized double getAreaDistance() {
        return calcDistFromArea();
    }

    /**
     * u want the distance based on the vertical distance?
     * @return the distance based on the vertical distance
     */
    public synchronized double getVertDistance() {
        return calcDistFromVert();
    }

    /**
     * u want the distance based on the horizontal distance?
     * @return the distance based on the horizontal distance
     */
    public synchronized double getHorizDistance() {
        return calcDistFromHoriz();
    }

    /**
     * Helper method for the area-dist calculation
     * @return the distance based on area
     */
    private double calcDistFromArea() {
        return areaCoeff * Math.pow(targetArea, areaExp);
    }

    /**
     * Helper method for the vert-dist calculation
     * @return the distance based on vertical distance
     */
    private double calcDistFromVert() {
        return vertCoeff * Math.pow(verticalLength, vertExp);
    }

    /**
     * Helper method for the horiz-dist calculation
     * @return the distance based on horizontal distance
     */
    private double calcDistFromHoriz() {
        return horizCoeff * Math.pow(horizontalLength, horizExp);
    }

    /**
     * u need the robot distance to the target based on trig?
     * @return the distance to target using the trig formulas
     */
    public double getRobotDistance() {
        return calcRobotDistance();
    }

    /**
     * Helper method for the distance to target based on trig
     * @return the distance to target based on trig
     */
    private double calcRobotDistance() {
            return MercMath.lawOfCosines(calcDistFromVert(), 
                                         LIMELIGHT_TO_ROBOT_CENTER_CARGO_IN, 
                                         LIMELIGHT_TO_ROBOT_CENTER_CARGO_DEG + targetCenterXAngle);
    }

    /**
     * u need the robot angle offset to the target based on trig?
     * @return the distance to target using the trig formulas
     */
    public synchronized double getRobotHeadingOffset() {
        return calcRobotHeading();
    }

    /**
     * Set the LED state on the limelight.
     * @param limelightLEDState the state of the LED.
     */
    public void setLEDState(LimelightLEDState limelightLEDState) {
        nt.getEntry("ledMode").setNumber(limelightLEDState.value);
    }

    /**
     * Calculates the angle from the center of the robot to the target
     * Trust me on this one
     * TODO: Make this work for both sides (It should choose between constants)
     */
    private double calcRobotHeading() {
        double alpha = LIMELIGHT_TO_ROBOT_CENTER_CARGO_DEG + targetCenterXAngle;
        double temp1 = LIMELIGHT_TO_ROBOT_CARGO_PLANE_IN / Math.cos(targetCenterXAngle);
        double temp2 = calcDistFromVert() - temp1;
        double temp3 = temp1 * Math.sin(alpha);
        double temp4 = temp3 + HALF_ROBOT_FRAME_WIDTH_INCHES;
        double temp5 = MercMath.lawOfCosines(temp2, temp4, alpha);
        return MercMath.lawOfSinesAngle(temp5, temp2, alpha); 
    }

    /**
     * Calculating the robot heading by switching the coordinate system the camera is on
     * @return the heading from switching from cartesian to polar and back.
     */
    public double calcRobotHeading2(){
        return Math.atan(calcDistFromVert()*Math.cos(targetCenterXAngle)/(calcDistFromVert()*Math.sin(targetCenterXAngle) - HALF_ROBOT_FRAME_WIDTH_INCHES));
    }
}