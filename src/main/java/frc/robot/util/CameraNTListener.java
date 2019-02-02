package frc.robot.util;

import edu.wpi.first.networktables.TableEntryListener;

import java.util.Calendar;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;

public class CameraNTListener implements TableEntryListener{
    private NetworkTable nt;
    private boolean isListening;

    private double numTargets, rectCenterXAngle, rectCenterYAngle, rectArea;
    private Calendar tsNumTargetsCalendar, tsRectCenterXAngle, tsRectCenterYAngle, tsRectArea;

    public CameraNTListener(NetworkTable nt){
        this.nt = nt;
        numTargets = rectCenterXAngle = rectCenterYAngle = rectArea = 0.0;
        tsNumTargetsCalendar = tsRectCenterXAngle = tsRectCenterYAngle = tsRectArea = Calendar.getInstance();
    }

    public synchronized void run(){
        if(!isListening){
            nt.addEntryListener(this, EntryListenerFlags.kUpdate);
            isListening = true;
        }
    }

    public void valueChanged(NetworkTable nt, String key, NetworkTableEntry ne, NetworkTableValue nv, int flags){
        Calendar ts = Calendar.getInstance();
        synchronized(this){
            switch (key) {
                case "tx": {
                    rectCenterXAngle = nv.getDouble();
                    tsRectCenterXAngle = ts;
                    break; 
                }
                case "ty": {
                    rectCenterYAngle = nv.getDouble();
                    tsRectCenterYAngle = ts;
                    break;
                }
                case "ta": {
                    rectArea = nv.getDouble();
                    tsRectArea = ts;
                    break;
                }
                case "tl": {
                    numTargets = nv.getDouble();
                    tsNumTargetsCalendar = ts;
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    public double getRectCenterXAngle(){
        return this.rectCenterXAngle;
    }

    public double getRectCenterYAngle(){
        return this.rectCenterYAngle;
    }

    public double getRectArea(){
        return this.rectArea;
    }

    public double getNumTargets(){
        return this.numTargets;
    }
}
