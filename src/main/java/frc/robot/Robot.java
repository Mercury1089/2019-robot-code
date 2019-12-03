package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.auton.AutonMove;
import frc.robot.commands.drivetrain.SwitchDriveDirection;
import frc.robot.sensors.Limelight.LimelightLEDState;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;
import frc.robot.util.DriveAssist.DriveDirection;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * <p>
 * GUYS, WE FOUND THE ROBOT
 */
public class Robot extends TimedRobot {

    public static DriveTrain driveTrain;

    public static MouthArticulator mouthArticulator;
    public static ClawAndIntake clawAndIntake;

    public static Elevator elevator;

    public static Stinger stinger;

    public static Fangs fangs;
    public static Legs legs;

    public static LimelightAssembly limelightAssembly;

    public static OI oi;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {

        driveTrain = new DriveTrain(DriveTrainLayout.TALONS);

        //Cargo Subsystems
        //mouthArticulator = new MouthArticulator();
        //clawAndIntake = new ClawAndIntake();

        //Elevator
        //elevator = new Elevator();

        //Hatch Panel Subsystems
        //stinger = new Stinger();

        //Climber Subsystems
        //fangs = new Fangs();
        //legs = new Legs();

        //limelightAssembly = new LimelightAssembly();

        oi = new OI();
    }

    @Override
    public void robotPeriodic() {
        oi.updateDash();
    }

    @Override
    public void disabledInit() {
        //limelightAssembly.getLimeLight().setLEDState(LimelightLEDState.OFF);
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        //limelightAssembly.getLimeLight().setLEDState(LimelightLEDState.ON);
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        //limelightAssembly.getLimeLight().setLEDState(LimelightLEDState.ON);
        //(new SwitchDriveDirection(DriveDirection.HATCH)).start();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic() {
        super.testInit();
        //limelightAssembly.getLimeLight().setLEDState(LimelightLEDState.ON);
    }
}
