import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
/*
 * Odometer Class
 * Author: Simon Lee, Sidney Ng
 */

//Measure the exact values for these data

public class HardwareInfo {
	public static final NXTRegulatedMotor rightMotor = Motor.B;
	public static final NXTRegulatedMotor leftMotor = Motor.A;
	public static final double leftRadius = 2.2875;
	public static final double rightRadius = 2.2875;
	public static final double width = 16.395; // need to measure first
}
