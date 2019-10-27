package frc.robot.util.interfaces;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.*;
import frc.robot.util.PIDGain;

public interface IMercMotorMaster {

    boolean isLimitSwitchClosed(LimitSwitchDirection limitSwitchDirection);

    double getClosedLoopError();
    double getClosedLoopError(int slotIdx);
    double getEncTicks();
    double getEncVelo();
    double getSpeed();

    int getPort();

    void configAllowableClosedLoopError(int slotIdx, int allowableClosedLoopError);
    void configAuxPIDPolarity(boolean invert);
    void configClosedLoopPeakOutput(int slotIdx, double peakOutput);
    void configClosedLoopPeriod(int slotIdx, int closedLoopTimeMs);
    void configFactoryReset();
    void configMotionAcceleration(int sensorUnitsPer100msPerSec);
    void configMotionCruiseVelocity(int sensorUnitsPer100ms);    
    void configPID(int slot, PIDGain gains);
    void configRemoteFeedbackFilter(int deviceID, RemoteSensorSource rss, int remoteSlotIdx);
    void configSelectedFeedbackCoefficient(double fdbkScale, int pidIdx);
    void configSelectedFeedbackSensor(FeedbackDevice FeedbackDevice, int pidIdx);
    void configSensorTerm(SensorTerm st, FeedbackDevice fd);
    void configSetParameter(ParamEnum param, double value, int subValue, int ordinal);
    void configVoltage(double nominalOutput, double peakOutput);
    void disableForwardSoftLimit();
    void disableReverseSoftLimit();
    void enableForwardSoftLimit();
    void enableReverseSoftLimit();
    void follow(IMercMotorMaster leader);
    void follow(IMercMotorMaster leader, FollowerType followerType);
    void resetEncoder();
    void selectProfileSlot(int slotIdx, int pidIdx);
    void set(ControlMode controlMode, double demand0);
    void set(ControlMode controlMode, double demand0, DemandType demand1Type, double demand1);
    void setForwardSoftLimit(int limitTicks);
    void setInverted(boolean invert);
    void setNeutralMode(NeutralMode neutralMode);
    void setPosition(double position);
    void setReverseSoftLimit(int limitTicks);
    void setSensorPhase(boolean bool);
    void stop();

    enum LimitSwitchDirection {
        FORWARD,
        REVERSE
    };
}
