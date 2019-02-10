package frc.robot.util;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

/**
 * Helper class to interface with the Shuffleboard Dashboard.
 */
public class ShuffleDash {
    private NetworkTableInstance ntInstance;

    public ShuffleDash() {
        //new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();
    }

    public void updateDash() {
        //SmartDashboard.putString("Alliance Color", DriverStation.getInstance().getAlliance().toString());

        SmartDashboard.putNumber("Left Enc in ticks", Robot.driveTrain.getLeftLeader().getEncTicks());
        SmartDashboard.putNumber("Right Enc in ticks", Robot.driveTrain.getRightLeader().getEncTicks());

        SmartDashboard.putNumber("Left Enc in feet", Robot.driveTrain.getLeftEncPositionInFeet());
        SmartDashboard.putNumber("Right Enc in feet", Robot.driveTrain.getRightEncPositionInFeet());

        SmartDashboard.putString("DriveTrain", Robot.driveTrain.getCurrentCommandName());

        SmartDashboard.putNumber("Left Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getLeftLeader().getEncVelo()));
        SmartDashboard.putNumber("Right Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getRightLeader().getEncVelo()));

        SmartDashboard.putNumber("LIDAR Raw Distance (in.)", MercMath.roundFloat(Robot.driveTrain.getLidar().getRawDistance(), 10));
        //SmartDashboard.putNumber("LIDAR Period", MercMath.roundFloat(Robot.claw.getLidar().getDistance(), 10));

        SmartDashboard.putNumber("Lime Target Offset", Robot.limelightRotate.getLimeLight().getTargetCenterXAngle());
        SmartDashboard.putNumber("Lime Target Area", Robot.limelightRotate.getLimeLight().getTargetArea());
        SmartDashboard.putNumber("Lime Dist From Area", Robot.limelightRotate.getLimeLight().getAreaDistance());
        SmartDashboard.putNumber("Lime Dist From Vert", Robot.limelightRotate.getLimeLight().getVertDistance());
        SmartDashboard.putNumber("Lime Dist From Horiz", Robot.limelightRotate.getLimeLight().getHorizDistance());
        SmartDashboard.putNumber("Gyro Angle", Robot.driveTrain.getPigeonYaw());

        SmartDashboard.putBoolean("Auton Initialized", ntInstance.getTable("AutonConfiguration").containsKey("startingPosition"));

        //SmartDashboard.putString("LED Output",Robot.claw.getCurrentLEDOutput()[0]+","+Robot.claw.getCurrentLEDOutput()[1]+","+Robot.claw.getCurrentLEDOutput()[2]);
    }
}
