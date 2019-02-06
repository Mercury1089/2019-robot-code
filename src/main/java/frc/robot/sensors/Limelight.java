package frc.robot.sensors;

import edu.wpi.first.networktables.*;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Add your docs here.
 */
public class Limelight implements PIDSource, TableEntryListener {
    private NetworkTable nt = NetworkTableInstance.getDefault().getTable("limelight"); //finds the limelight network table
    private double numTargets, targetCenterXAngle, targetCenterYAngle, targetArea, horizontalLength, verticalLength;

    /** 
     * Constucts the sensor and adds a listener to the table 
     */
    public Limelight(){
        numTargets = nt.getEntry("tv").getDouble(0.0);
        targetCenterXAngle = nt.getEntry("tx").getDouble(0.0);
        targetCenterYAngle = nt.getEntry("ty").getDouble(0.0);
        targetArea = nt.getEntry("ta").getDouble(0.0);
        horizontalLength = nt.getEntry("thor").getDouble(0.0);
        verticalLength = nt.getEntry("tvert").getDouble(0.0);
        nt.addEntryListener(this, EntryListenerFlags.kUpdate);
    }

    /**
     * @param nt is always the limelight network table in this case
     * @param key is the key of the entry that changed
     * @param ne is the entry that changed
     * @param nv is the value of the entry that changed
     * @param flags is the flag that occured which is always kUpdate in this case
     */
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
}
