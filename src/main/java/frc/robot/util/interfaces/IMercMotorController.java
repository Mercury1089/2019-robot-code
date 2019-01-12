package frc.robot.util.interfaces;


public interface IMercMotorController {
    public void setSpeed(double x);
    public double getSpeed();
    public void setInverted(boolean invert);
    public void follow(IMercMotorController leader);
    public int getPort();
    public void stop();
    public double getEncPos();
    public double getEncVelo();
}