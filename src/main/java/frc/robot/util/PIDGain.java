package frc.robot.util;

public class PIDGain {
	public final double kP;
	public final double kI;
	public final double kD;
	public final double kF;
	
	public PIDGain(double _kP, double _kI, double _kD, double _kF){
		kP = _kP;
		kI = _kI;
		kD = _kD;
		kF = _kF;
	}
}