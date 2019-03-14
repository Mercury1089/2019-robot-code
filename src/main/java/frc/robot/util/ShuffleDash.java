package frc.robot.util;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;

public class ShuffleDash {

    private NetworkTableInstance ntInstance;
    private SendableChooser<String> sandstormFirstStep;

    public ShuffleDash() {
        //new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();

        sandstormFirstStep = new SendableChooser<>();
        sandstormFirstStep.addOption("Left Close", "LeftClose");
        sandstormFirstStep.addOption("Left Middle", "LeftMiddle");
        sandstormFirstStep.addOption("Left Far", "LeftFar");
        sandstormFirstStep.addOption("Left Rocket Close", "LeftRocketClose");
        sandstormFirstStep.addOption("Left Rocket Far", "LeftRocketFar");
        sandstormFirstStep.addOption("Mid Left", "MidLeft");
        sandstormFirstStep.addOption("Mid Right", "MidRight");
        sandstormFirstStep.addOption("Right Close", "RightClose");
        sandstormFirstStep.addOption("Right Middle", "RightMiddle");
        sandstormFirstStep.addOption("Right Far", "RightFar");
        sandstormFirstStep.setDefaultOption("Straight", "StraightProfile");
    }

    public void updateDash() {
        // SmartDashboard.putString("Alliance Color", DriverStation.getInstance().getAlliance().toString());

        // SmartDashboard.putNumber("Left Enc in ticks", Robot.driveTrain.getLeftLeader().getEncTicks());
        // SmartDashboard.putNumber("Right Enc in ticks", Robot.driveTrain.getRightLeader().getEncTicks());

        SmartDashboard.putString("direction", Robot.driveTrain.getDirection().name());

        SmartDashboard.putNumber("Left Enc in feet", Robot.driveTrain.getLeftEncPositionInFeet());
        SmartDashboard.putNumber("Right Enc in feet", Robot.driveTrain.getRightEncPositionInFeet());

        SmartDashboard.putNumber("Left Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getLeftLeader().getEncVelo()));
        SmartDashboard.putNumber("Right Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getRightLeader().getEncVelo()));

        SmartDashboard.putNumber("LIDAR Raw Distance (in.)", MercMath.roundFloat(Robot.claw.getLidar().getRawDistance(), 10));

        SmartDashboard.putNumber("Ultrasonic Distance (Some unit)", Robot.driveTrain.getLeftUltrasonic().getDistance());

        SmartDashboard.putNumber("Lime Dist From Vertical", Robot.limelightAssembly.getLimeLight().getRawVertDistance());

        SmartDashboard.putNumber("Gyro Angle", Robot.driveTrain.getPigeonYaw());

        SmartDashboard.putString("FrontCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Panel" : "Cargo");
        SmartDashboard.putString("BackCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Cargo" : "Panel");

        SmartDashboard.putData("Sandstorm First Step", sandstormFirstStep);

        SmartDashboard.putBoolean("Elevator Limit Switch Closed", Robot.elevator.getElevatorLeader().isLimitSwitchClosed(LimitSwitchDirection.REVERSE));

        SmartDashboard.putNumber("Elevator enc", Robot.elevator.getElevatorLeader().getEncTicks());
        SmartDashboard.putNumber("Hatch pos", Robot.stinger.getEjector().getEncTicks());

        SmartDashboard.putNumber("Cargo Intake Enc", Robot.mouthArticulator.getArticulator().getEncTicks());
        SmartDashboard.putBoolean("Cargo Intake Fwd Limit", Robot.mouthArticulator.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.FORWARD));
        SmartDashboard.putBoolean("Cargo Intake Rwd Limit", Robot.mouthArticulator.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE));

        SmartDashboard.putNumber("HatchPanel Intake Enc", Robot.forks.getArticulator().getEncTicks());
        SmartDashboard.putBoolean("HatchPanel Intake Fwd Limit", Robot.forks.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.FORWARD));
        SmartDashboard.putBoolean("HatchPanel Intake Rwd Limit", Robot.forks.getArticulator().isLimitSwitchClosed(LimitSwitchDirection.REVERSE));


        //SmartDashboard.putBoolean("Auton Initialized", ntInstance.getTable("AutonConfiguration").containsKey("startingPosition"));

        // SmartDashboard.putString("LED Output",Robot.claw.getCurrentLEDOutput()[0]+","+Robot.claw.getCurrentLEDOutput()[1]+","+Robot.claw.getCurrentLEDOutput()[2]);
    }

    public String getFirstStep() {
        return sandstormFirstStep.getSelected();
    }
}
