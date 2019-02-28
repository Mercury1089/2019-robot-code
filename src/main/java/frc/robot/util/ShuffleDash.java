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
        // SmartDashboard.putString("Alliance Color", DriverStation.getInstance().getAlliance().toString());

        // SmartDashboard.putNumber("Left Enc in ticks", Robot.driveTrain.getLeftLeader().getEncTicks());
        // SmartDashboard.putNumber("Right Enc in ticks", Robot.driveTrain.getRightLeader().getEncTicks());

        SmartDashboard.putNumber("Left Enc in feet", Robot.driveTrain.getLeftEncPositionInFeet());
        SmartDashboard.putNumber("Right Enc in feet", Robot.driveTrain.getRightEncPositionInFeet());

        SmartDashboard.putNumber("Left Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getLeftLeader().getEncVelo()));
        SmartDashboard.putNumber("Right Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getRightLeader().getEncVelo()));

        SmartDashboard.putNumber("LIDAR Raw Distance (in.)", MercMath.roundFloat(Robot.driveTrain.getLidar().getRawDistance(), 10));

        SmartDashboard.putNumber("Ultrasonic Distance (Some unit)", Robot.driveTrain.getLeftUltrasonic().getDistance());

        SmartDashboard.putNumber("Lime Dist From Vertical", Robot.limelightAssembly.getLimeLight().getRawVertDistance());
        SmartDashboard.putNumber("Robot Heading Offset from Target", Robot.limelightAssembly.getLimeLight().getRobotHeadingOffset());
        SmartDashboard.putNumber("Robot Distance Offset from Target", Robot.limelightAssembly.getLimeLight().getRobotDistanceOffset());
        
        SmartDashboard.putNumber("Gyro Angle", Robot.driveTrain.getPigeonYaw());

        SmartDashboard.putString("FrontCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Panel" : "Cargo");
        SmartDashboard.putString("BackCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Cargo" : "Panel");

        SmartDashboard.putBoolean("a", true);
        SmartDashboard.putBoolean("b", Robot.driveTrain.getLeftUltrasonic().getDistance() <= 2);
        
        //SmartDashboard.putBoolean("Auton Initialized", ntInstance.getTable("AutonConfiguration").containsKey("startingPosition"));

        // SmartDashboard.putString("LED Output",Robot.claw.getCurrentLEDOutput()[0]+","+Robot.claw.getCurrentLEDOutput()[1]+","+Robot.claw.getCurrentLEDOutput()[2]);
    }
}
