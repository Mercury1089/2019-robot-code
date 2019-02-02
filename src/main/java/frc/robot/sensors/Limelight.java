package frc.robot.sensors;


import java.util.Calendar;
import edu.wpi.first.networktables.*;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class Limelight implements PIDSource, TableEntryListener {
    private NetworkTable nt = NetworkTableInstance.getDefault().getTable("limelight"); //finds the limelight network table
    private double numTargets, targetCenterXAngle, targetCenterYAngle, targetArea;

    /**Constucts the sensor and adds a listener to the table 
     */
    public Limelight(){
        nt.addEntryListener(this, EntryListenerFlags.kUpdate);
        numTargets = nt.getEntry("tv").getDouble(0.0);
        targetCenterXAngle = nt.getEntry("tx").getDouble(0.0);
        targetCenterYAngle = nt.getEntry("ty").getDouble(0.0);
        targetArea = nt.getEntry("ta").getDouble(0.0);
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
                default: {
                    break;
                }
            }
        }
    }

    public double getTargetCenterXAngle(){
        return this.targetCenterXAngle;
    }

    public double getTargetCenterYAngle(){
        return this.targetCenterYAngle;
    }

    public double getTargetArea(){
        return this.targetArea;
    }

    public double getNumTargets(){
        return this.numTargets;
    }

    public void setPIDSourceType(PIDSourceType pidST){
        
    }

    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    public double pidGet() {
        return this.targetCenterXAngle;
    }
}
