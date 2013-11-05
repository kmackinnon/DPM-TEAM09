package robot;
/**HardwareInfo contains constant values that are needed for operating the robot.
 * 
 * @author Simon Lee, Sidney Ng
 * 
 */

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


//Measure the exact values for these data
public class HardwareInfo {

	/**Used to move the robot*/
	public static final NXTRegulatedMotor leftMotor = Motor.A;
	
	/**Used to move the robot*/
	public static final NXTRegulatedMotor rightMotor = Motor.B;
	
	/**Used to grab and lift a block*/
	public static final NXTRegulatedMotor liftingMotor = Motor.C;

	/** The front ultrasonic sensor for detecting cinder blocks */
	public static final UltrasonicSensor frontUltrasonicSensor = new UltrasonicSensor(
			SensorPort.S1);

	/**
	 * The front color sensor for detecting blocks (especially when approaching
	 * corners) and identifying their type
	 */
	public static final ColorSensor frontCS = new ColorSensor(SensorPort.S2);

	/** The back left color sensor used in odometry correction */
	public static final ColorSensor leftCS = new ColorSensor(SensorPort.S3);

	/** The back right color sensor used in odometry correction */
	public static final ColorSensor rightCS = new ColorSensor(SensorPort.S4);

	/** The radius of the left wheel. */
	public static final double leftRadius = 2.2875;

	/** The radius of the right wheel. */
	public static final double rightRadius = 2.2875;

	/** The distance between the two wheels */
	public static final double width = 16.395; // need to measure first

	/**
	 * The speed of the wheel motors when the robot is moving in a straight
	 * line. The number is how many degrees the motors rotate per second
	 */
	public static final int FORWARD_SPEED = 150;

	/**
	 * The speed of the wheel motors when the robot is rotating on point. The
	 * number is how many degrees the motors rotate per second
	 */
	public static final int ROTATION_SPEED = 50;

	/**
	 * The speed of the lifting motor when the robot is lifting blocks. The
	 * number is how many degrees the motor rotates per second
	 */
	public static final int LIFTING_SPEED = 20;

}
