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
 * 
 * @author Kevin Musgrave
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
			SensorPort.S4);

	/**
	 * The front color sensor for detecting blocks (especially when approaching
	 * corners) and identifying their type
	 */
	public static final ColorSensor frontCS = new ColorSensor(SensorPort.S2);

	/** The back left color sensor used in odometry correction */
	public static final ColorSensor leftCS = new ColorSensor(SensorPort.S1);

	/** The back right color sensor used in odometry correction */
	public static final ColorSensor rightCS = new ColorSensor(SensorPort.S3);

	/** The radius of the left wheel. */
	public static final double LEFT_RADIUS = 2.11;

	/** The radius of the right wheel. */
	public static final double RIGHT_RADIUS = 2.11;

	/** The distance between the two wheels */
	public static final double WIDTH = 10.97;

	/**
	 * The speed of the wheel motors when the robot is moving in a straight
	 * line. The number is how many degrees the motors rotate per second
	 */
	public static final int FORWARD_SPEED = 360;

	public static final int SLOW_FORWARD_SPEED = 180;

	/**
	 * The speed of the wheel motors when the robot is rotating on point. The
	 * number is how many degrees the motors rotate per second
	 */
	public static final int ROTATION_SPEED = 180;

	/**
	 * The speed of the slower wheel when turning while moving.
	 */
	public static final int TURNING_SPEED = 270;

	/**
	 * The speed of the lifting motor when the robot is lifting blocks. The
	 * number is how many degrees the motor rotates per second
	 */
	public static final int LIFTING_SPEED = 120;

	/**
	 * The angle that the claw motor must turn to for the claw to be fully
	 * raised.
	 */
	public static final int LIFTING_DEGREE = 335;

	/**
	 * The angle that the claw motor turns to for the claw to be ready to grab a
	 * block.
	 */
	public static final int DROPPING_DEGREE = 0;

	/**
	 * The distance between the two back color sensors
	 */
	public static final double SENSOR_WIDTH = 11.05;

	/**
	 * The distance from the back light sensors to the wheels
	 */
	public static final double SENSOR_TO_WHEEL_DISTANCE = 10.6;

	public static final int DEFAULT_TIMER_PERIOD = 25;

	public static final int US_SENSOR_255 = 255;

	/**
	 * A value that is not normally obtained and that can be used as a flag or
	 * "error" value.
	 */
	public static final int INT_SPECIAL_FLAG = -Integer.MAX_VALUE;

	public static final double DOUBLE_SPECIAL_FLAG = -Double.MAX_VALUE;

	/**
	 * The default number of samples to obtain for various methods that require
	 * more than one datapoint to make a decision.
	 */
	public static final int DEFAULT_NUM_OF_SAMPLES = 10;

	/**
	 * The default threshold to confirm that something is true based on the
	 * default number of samples
	 */
	public static final int DEFAULT_CONFIRMATION_MINIMUM = 7;

	private static boolean isBuilder;

	private static boolean isBuilderHasBeenSet = false;

	private static int[] startCorner = new int[2];

	private static boolean startCornerHasBeenSet = false;

	/**
	 * 
	 * @param willBuild
	 *            true if the robot should become builder
	 */
	public static void becomeBuilder(boolean willBuild) {
		if (!isBuilderHasBeenSet) {
			isBuilder = willBuild;
			isBuilderHasBeenSet = true;
		}

	}

	public static boolean isBuilder() {
		return isBuilder;
	}

	/**
	 * 
	 * @param coordinates
	 *            the xy tile coordinates of the start corner
	 */
	public static void setStartCorner(int[] coordinates) {

		if (!startCornerHasBeenSet) {

			startCorner[0] = coordinates[0];
			startCorner[1] = coordinates[1];
			startCornerHasBeenSet = true;
		}
	}

	public static int getXStart() {
		return startCorner[0];
	}

	public static int getYStart() {
		return startCorner[1];
	}

	/**
	 * When doing a moving median or mean, the array needs to be shifted at
	 * every time step. This is just a for loop, but for loops are ugly.
	 * 
	 * @param input
	 *            array to be shifted.
	 */
	public void shiftArrayByOne(double[] input, double latestValue) {

		for (int i = 0; i < input.length - 1; i++) {
			input[i] = input[i + 1];
		}

		input[input.length - 1] = latestValue;
	}

	public void shiftArrayByOne(int[] input, int latestValue) {

		for (int i = 0; i < input.length - 1; i++) {
			input[i] = input[i + 1];
		}

		input[input.length - 1] = latestValue;
	}

	public double getMean(double[] input) {

		double sum = 0;

		for (int i = 0; i < input.length; i++) {
			sum += input[i];
		}

		sum /= input.length;

		return sum;
	}

	public double getMean(int[] input) {

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

	public int getUSDistance() {
		return ultrasonicSensor.getDistance();
	}

}
