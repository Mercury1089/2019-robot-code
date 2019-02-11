package frc.robot.sensors;

import edu.wpi.first.networktables.*;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.util.MercMath;

/**
 * Add your docs here.
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
    private final double HALF_ROBOT_FRAME_WIDTH_INCHES = 15;

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
                    targetAcquired = nv.getDouble() == 0.0 ? false : true;
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

    public synchronized double getTargetCenterXAngle(){
        return this.targetCenterXAngle;
    }

    public synchronized double getTargetCenterYAngle(){
        return this.targetCenterYAngle;
    }

    public synchronized double getTargetArea(){
        return this.targetArea;
    }

    public synchronized double getNumTargets(){
        return this.numTargets;
    }

    public synchronized double getVerticalLength() {
        return this.verticalLength;
    }

    public synchronized double getHorizontalLength() {
        return this.horizontalLength;
    }
    public synchronized void setPIDSourceType(PIDSourceType pidST){
        
    }

    public synchronized PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    public synchronized double pidGet() {
        return this.targetCenterXAngle;
    }

    public synchronized double getAreaDistance() {
        return calcDistFromArea();
    }

    public synchronized double getVertDistance() {
        return calcDistFromVert();
    }

    public synchronized double getHorizDistance() {
        return calcDistFromHoriz();
    }

    private double calcDistFromArea() {
        return areaCoeff * Math.pow(targetArea, areaExp);
    }

    private double calcDistFromVert() {
        return vertCoeff * Math.pow(verticalLength, vertExp);
    }

    private double calcDistFromHoriz() {
        return horizCoeff * Math.pow(horizontalLength, horizExp);
    }

    public double getRobotDistance() {
        return calcRobotDistance();
    }

    private double calcRobotDistance() {
            return MercMath.lawOfCosines(calcDistFromVert(), 
                                         LIMELIGHT_TO_ROBOT_CENTER_CARGO_IN, 
                                         LIMELIGHT_TO_ROBOT_CENTER_CARGO_DEG + targetCenterXAngle);
    }

    public double getRobotHeading() {
        return calcRobotHeading();
    }

    public void setLEDState(LimelightLEDState limelightLEDState) {
        nt.getEntry("ledMode").setDouble(limelightLEDState.value);
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
}
