package robot;

import java.util.Arrays;

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * SensorMotorUser contains methods that are needed when using the data received
 * by the light and ultrasonic sensors. It contains all hardware info as well.
 */

public class SensorMotorUser {

	/** Used to move the robot */
	public static final NXTRegulatedMotor leftMotor = Motor.A;

	/** Used to move the robot */
	public static final NXTRegulatedMotor rightMotor = Motor.B;

	/** Used to grab and lift a block */
	public static final NXTRegulatedMotor clawMotor = Motor.C;

	/** The front ultrasonic sensor for detecting cinder blocks */
	public static final UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(
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
	public static final int LIFTING_SPEED = 50;

	public static final double sensorWidth = 11.05;
	
	public static boolean isBuilder;

	
	
	
	
	
	public static void isBuilder(boolean willBuild){
		
		isBuilder = willBuild;
		
	}
	
	
	/**
	 * When doing a moving median or mean, the array needs to be shifted at
	 * every time step. This is just a for loop, but for loops are ugly.
	 * 
	 * @param input
	 *            array to be shifted.
	 */
	public void shiftArrayByOne(double[] input, double latestValue) {

		for(int i = 0; i<input.length-1; i++){
			input[i] = input[i+1];
		}
		
		input[input.length-1] = latestValue;	
	}
	
	
	public void shiftArrayByOne(int[] input, int latestValue) {

		for(int i = 0; i<input.length-1; i++){
			input[i] = input[i+1];
		}
		
		input[input.length-1] = latestValue;	
	}
	
	


	public double getMean(double[] input) {

		double sum = 0;

		for (int i = 0; i < input.length; i++) {
			sum += input[i];
		}

		sum /= input.length;

		return sum;
	}
	
	
	public double getMean(int[] input){
		
		double sum = 0;

		for (int i = 0; i < input.length; i++) {
			sum += input[i];
		}

		sum /= input.length;

		return sum;
	}
	
	

	public double getMedian(double[] input) {

		double[] sortedArray = new double[input.length];

		System.arraycopy(input, 0, sortedArray, 0, input.length);
		Arrays.sort(sortedArray);

		int middle = sortedArray.length / 2;

		if (sortedArray.length % 2 == 1) {
			return sortedArray[middle];
		} else {
			return (sortedArray[middle - 1] + sortedArray[middle]) / 2;
		}
	}
	
	
	
	public int getMedian(int[] input) {

		int[] sortedArray = new int[input.length];

		System.arraycopy(input, 0, sortedArray, 0, input.length);
		Arrays.sort(sortedArray);

		int middle = sortedArray.length / 2;

		if (sortedArray.length % 2 == 1) {
			return sortedArray[middle];
		} else {
			return (sortedArray[middle - 1] + sortedArray[middle]) / 2;
		}
	}
	

}
