package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.cargo.RunClaw;
import frc.robot.sensors.LIDAR;
import frc.robot.util.MercVictorSPX;

public class ClawAndIntake extends Subsystem {

    public static final double CARGO_IN_ROBOT_THRESHOLD = 10.0;

    private ClawState state;

    private MercVictorSPX clawLeft, clawRight;
    private LIDAR lidar;

    public ClawAndIntake() {
        clawLeft = new MercVictorSPX(CAN.CLAW_LEFT);
        clawRight = new MercVictorSPX(CAN.CLAW_RIGHT);

        clawLeft.setInverted(true);
        clawRight.setInverted(false);

        clawRight.follow(clawLeft);

        lidar = new LIDAR(Robot.driveTrain.getCanifier(), CANifier.PWMChannel.valueOf(0), LIDAR.PWMOffset.EQUATION_C);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new RunClaw(ClawState.IDLE));
    }

    public frc.robot.sensors.LIDAR getLidar() {
        return lidar;
    }

    @Override
    public void periodic() {
        lidar.updatePWMInput();
    }

    public boolean isCargoInRobot() {
        return lidar.getRawDistance() <= CARGO_IN_ROBOT_THRESHOLD;
    }

    public ClawState getClawState() {
        return state;
    }

    public void setClawState(ClawState state) {
        this.state = state;
        switch (state) {
            
        }
    }

    public enum ClawState {
        INTAKING,
        EJECTING,
        IDLE
    }
}
