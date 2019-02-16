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
    private final double RobotCenterX = 13.0, RobotCenterY = 15.5;

    /*
    * Coefficients and exponents to help find the distance of a target
    * Each equation is in the form ax^b where a is the coefficient
    * (Coeff) of the equation and b is the exponent (Exp). x is the 
    * variable, either vertical length (vert), horizontal length (horiz),
    * or area (area).
    */
    private final double vertCoeff = 111.0;
    private final double vertExp = -0.948;
    private final double horizCoeff = 264.0;
    private final double horizExp = -0.953;
    private final double areaCoeff = 6.64;
    private final double areaExp = -0.466;

    //WARNING: Arbitrary values below v
    private final double LIMELIGHT_TO_ROBOT_CENTER_CARGO_IN = 23.75; //Distance from the LL to the center of the cargo side
    private final double LIMELIGHT_TO_ROBOT_CENTER_CARGO_DEG = 35.5; //Angle from the LL to the center of the cargo side
    private final double LIMELIGHT_TO_ROBOT_CARGO_PLANE_IN = 21; //Distance from the LL to the plane of the cargo side
    private final double HALF_ROBOT_FRAME_WIDTH_INCHES = 13;

    private final double LIMELIGHT_HFOV_DEG = 59.6;
    private final double LIMELIGHT_VFOV_DEG = 45.7;

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
                    break;
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
     * u want to know if the limelight sees a valid target?
     * @return If there is a valid target
     */
    public synchronized boolean getTargetAcquired() {
        return this.targetAcquired;
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
    public synchronized double getRawAreaDistance() {
        return calcDistFromArea();
    }

    /**
     * u want the distance based on the vertical distance?
     * @return the distance based on the vertical distance
     */
    public synchronized double getRawVertDistance() {
        return calcDistFromVert();
    }

    /**
     * u want the distance based on the horizontal distance?
     * @return the distance based on the horizontal distance
     */
    public synchronized double getRawHorizDistance() {
        return calcDistFromHoriz();
    }

    /**
     * Helper method for the area-dist calculation
     * @return the distance based on area
     */
    private double calcDistFromArea() {
        return areaCoeff * Math.pow(targetArea, areaExp) * 12;
    }

    /**
     * Helper method for the vert-dist calculation
     * @return the distance based on vertical distance
     */
    private double calcDistFromVert() {
        return vertCoeff * Math.pow(verticalLength, vertExp) * 12;
    }

    /**
     * Helper method for the horiz-dist calculation
     * @return the distance based on horizontal distance
     */
    private double calcDistFromHoriz() {
        return horizCoeff * Math.pow(horizontalLength, horizExp) * 12;
    }

    /**
     * u need the robot distance to the target based on trig?
     * @return the distance to target using the trig formulas
     */
    public synchronized double getRobotDistanceOffset() {
        return calcRobotDistance();
    }
    /**
     * u need the robot angle offset to the target based on trig?
     * @return the distance to target using the trig formulas
     */
    public synchronized double getRobotHeadingOffset() {
        return calcRobotHeading();
    }

    /**
     * Calculating the robot heading by switching the coordinate plane the camera is on
     * @return the heading from switching from cartesian to polar and back.
     */
    public double calcRobotHeading(){
        return 180 / Math.PI * Math.atan((this.calcDistFromVert()*Math.sin(Math.toRadians(this.targetCenterXAngle)) - RobotCenterX)/(calcDistFromVert()*Math.cos(Math.toRadians(this.targetCenterXAngle) - RobotCenterY)));
    }

    /**
     * Calculating the robot distance by switching the coordinate plane the camera is on
     * @return the distance from switching from cartesian to polar and back.
     */
    public double calcRobotDistance(){
        return Math.sqrt(Math.pow(this.calcDistFromVert()*Math.sin(Math.toRadians(this.targetCenterXAngle)) - 9.5, 2) + Math.pow(this.calcDistFromVert()*Math.cos(Math.toRadians(this.targetCenterXAngle)) - 19.0, 2));
    }

    /**
     * Set the LED state on the limelight.
     * @param limelightLEDState the state of the LED.
     */
    public void setLEDState(LimelightLEDState limelightLEDState) {
        nt.getEntry("ledMode").setNumber(limelightLEDState.value);
    }

    /**
     * Get the x pixel coordinate of the target
     * @return the pixel x-position of the target
     */
    public synchronized double getTargetPixelXPos() {
        double vpw = 2 * Math.tan(LIMELIGHT_HFOV_DEG / 2);
        double x = Math.tan(targetCenterXAngle);
        double nx = (2 * x) / vpw;
        return 160 * nx + 159.5;
    }

    /**
     * Get the y pixel coordinate of the target
     * @return the pixel y-position of the target
     */
    public synchronized double getTargetPixelYPos() {
        double vph = 2 * Math.tan(LIMELIGHT_VFOV_DEG / 2);
        double y = Math.tan(targetCenterYAngle);
        double ny = (2 * y) / vph;
        return 120 * ny + 159.5;
    }
}