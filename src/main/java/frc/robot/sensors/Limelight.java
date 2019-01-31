package frc.robot.sensors;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class LimeLight implements PIDSource {
    private static NetworkTableInstance nti = NetworkTableInstance.getDefault();

    public double getTargetOffset() {
        return nti.getEntry("<tx>").getDouble(0);
    }

    //use this to get distance to target
    public double getTargetArea() {
        return nti.getEntry("<ta>").getDouble(0);
    }

    public double getTargetDistance() {
        //implement this
        return 0;
    }

    public void setPIDSourceType(PIDSourceType pst) {
        
    }

    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    public double pidGet() {
        return getTargetOffset();
    }
}
