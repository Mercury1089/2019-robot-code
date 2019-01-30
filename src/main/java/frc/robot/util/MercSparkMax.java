package frc.robot.util;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANSparkMax.ExternalFollower;
import frc.robot.subsystems.DriveTrain;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax.IdleMode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.util.interfaces.IMercMotorController;

public class MercSparkMax implements IMercMotorController {
    private CANSparkMax sparkmax;
    private int port;
    private double setPos;

    public MercSparkMax(int port) {
        sparkmax = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
        sparkmax.setSmartCurrentLimit(20);
        this.port = port;
    }

    @Override
    public void setSpeed(double speed) {
        sparkmax.set(speed);
    }

    @Override
    public void setPosition(double ticks) {
        setPos = MercMath.encoderTicksToRevs(ticks);
        sparkmax.getPIDController().setReference(setPos, ControlType.kPosition);
    }

    @Override
    public double getSpeed() {
        return sparkmax.get();
    }
    
    @Override
    public void setInverted(boolean invert) {
        sparkmax.setInverted(invert);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void follow(IMercMotorController leader) {
        if (leader instanceof MercSparkMax)
            sparkmax.follow(((MercSparkMax)leader).get());
        else
            sparkmax.follow((ExternalFollower)leader, leader.getPort());    //Def the wrong way to do this please check
    }

    @Override
    public void stop() {
        sparkmax.stopMotor();
    }

    @Override
    public double getEncTicks() {
        return MercMath.revsToEncoderTicks(sparkmax.getEncoder().getPosition());
    }

    @Override
    public double getEncVelo() {
        return sparkmax.getEncoder().getVelocity();
    }

    @Override
    public double getClosedLoopError() {
        return setPos - sparkmax.getEncoder().getPosition();
    }

    @Override
    public void configPID(int slot, PIDGain gains) {
        sparkmax.getPIDController().setP(gains.kP, slot);
        sparkmax.getPIDController().setI(gains.kI, slot);
        sparkmax.getPIDController().setD(gains.kD, slot);
        sparkmax.getPIDController().setFF(gains.kF, slot);
    }

    @Override
    public void configVoltage(double nominalOutput, double peakOutput) {
        sparkmax.getPIDController().setOutputRange(nominalOutput, peakOutput);
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        CANSparkMax.IdleMode mode;
        if (neutralMode == NeutralMode.Brake)
            mode = IdleMode.kBrake;
        else
            mode = IdleMode.kCoast;
        sparkmax.setIdleMode(mode);
    }

    @Override
    public void setSensorPhase(boolean sensorPhase) {
        return;
    }

    @Override
    public void configAllowableClosedLoopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
        return;
    }

    @Override
    public void configSelectedFeedbackSensor(FeedbackDevice FeedbackDevice, int pidIdx, int timeoutMs) {
        return;
    }

    @Override
    public void configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
        return;
    }

    @Override
    public boolean isLimitSwitchClosed() {
        return sparkmax.getForwardLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get(); //TODO Check if this limit switch is forward or backwards
    }

//_________________________________________________________________________________
    /**
     * Get the Spark Max tied to this class
     * @return the Spark
     */
    public CANSparkMax get() {
        return sparkmax;
    }
}