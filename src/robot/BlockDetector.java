package robot;

import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam.
 * 
 * @author Sidney Ng, Simon Lee, Kevin Musgrave, Keith MacKinnon
 */
public class BlockDetector extends SensorMotorUser implements TimerListener {

	private Timer blockDetectorTimer;
	private Object lock;

	/**
	 * When the median of 5 samples of the ultrasonic sensor is less than this
	 * value, then an object has been detected
	 */
	private final int DIST_TO_STOP = 11;

	/**
	 * When the median of 10 samples of the differential of the front light
	 * sensor exceeds this value, then an object has been detected.
	 */
	private final int LIGHT_DIFF = 25;

	/**
	 * The red/blue color ratio is used together with the wood ratio to
	 * determine if a block is styrofoam or wooden. For styrofoam, the obtained
	 * red/blue ratio must be less than this value.
	 */
	private final double UPPER_RED_BLUE_RATIO = 1.6;

	/**
	 * This ratio is used together with the red/blue ratio to determine if a
	 * block is styrofoam or wooden. For styrofoam, the obtained wood ratio must
	 * be less than this value
	 */
	private final double WOOD_RATIO = 1.9;

	/**
	 * True if an object is detected
	 */
	private boolean isObjectDetected = false;

	/**
	 * True if at every timeout block detection should be performed
	 */
	private boolean doBlockDetection = false;

	/**
	 * True if at every timeout a minimum distance scan should be performed.
	 */
	private boolean doMinDistanceScan = false;

	/**
	 * the minimum distance found during the scan
	 */
	private int scanMinDistance;

	/**
	 * the angle at which the minimum distance occurred.
	 */
	private double minDistanceAngle;

	/**
	 * Used for ignoring the initial values read by the front color sensor
	 */
	private int frontCSIgnoreCounter;

	// variables for object detection

	/**
	 * a moving window of ultrasonic distances
	 */
	private int[] window = { 255, 255, 255, 255, 255 };

	/**
	 * a moving window of color sensor differentials
	 */
	private int[] colorDiffWindow = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/**
	 * the previous value obtained by the color sensor
	 */
	private int prevValue = 0;

	/**
	 * true when the median differential exceeds the LIGHT_DIFF threshold
	 */
	private boolean prevLatch = false, latch = false;
	private int median, value, diff;

	public BlockDetector() {
		lock = new Object();
		blockDetectorTimer = new Timer(DEFAULT_TIMER_PERIOD, this);
	}

	/**
	 * Turns on the timer
	 */
	public void startBlockDetectorTimer() {
		blockDetectorTimer.start();
	}

	/**
	 * Turns on block detection so that at every timeout, block detection is
	 * performed
	 */
	public void turnOnBlockDetection() {
		isObjectDetected = false;
		doBlockDetection = true;
		latch = false;
		prevLatch = false;
		frontCSIgnoreCounter = 0;
	}

	/**
	 * Turns off block detection
	 */
	public void turnOffBlockDetection() {
		isObjectDetected = false;
		doBlockDetection = false;
	}

	/**
	 * Turns on minimum distance scanning mode. This is used when the robot
	 * finds a styrofoam block and wants to find the best angle for grabbing it
	 */
	public void turnOnMinDistanceScanMode() {
		scanMinDistance = US_SENSOR_255;

		doMinDistanceScan = true;
	}

	/**
	 * Turns off minimum distance scanning mode.
	 */
	public void turnOffMinDistanceScanMode() {
		doMinDistanceScan = false;
	}

	public void timedOut() {

		if (doBlockDetection) {
			if (isObjectDetected()) {

				synchronized (lock) {
					isObjectDetected = true;
				}

			} else {

				synchronized (lock) {
					isObjectDetected = false;
				}
			}
		}

		if (doMinDistanceScan) {

			minDistanceScanRoutine();

		}

	}

	/**
	 * @return the angle at which the minimum distance was detected. If the
	 *         minimum distance was 255, then this returns DOUBLE_SPECIAL_FLAG,
	 *         which is found in SensorMotorUser
	 */
	public double getMinDistanceAngle() {

		if (scanMinDistance == US_SENSOR_255) {
			minDistanceAngle = DOUBLE_SPECIAL_FLAG;
		}

		return minDistanceAngle;

	}

	/**
	 * @return the minimum distance found during the scan
	 */
	public double getMinDistance() {
		return scanMinDistance;
	}

	// returns the isObjectDetected boolean

	/**
	 * This is called by other classes when they want to know if an object is
	 * detected.
	 * 
	 * @return true if an object is detected
	 */
	public boolean isObjectInFront() {
		boolean result;

		synchronized (lock) {
			result = isObjectDetected;
		}

		return result;
	}

	/**
	 * Returns a boolean indicating whether or not a block is detected by the
	 * ultrasonic or color sensor. This method is called at every timeout.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	public boolean isObjectDetected() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		frontCSIgnoreCounter++;

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);
		// RConsole.println("Distance: " + median);
		if (frontCSIgnoreCounter >= 10) {

			if (median <= DIST_TO_STOP) {
				return true;
			}
		}

		// stopping by color sensor differential
		latch = false;
		value = frontCS.getRawLightValue();
		diff = value - prevValue;
		shiftArrayByOne(colorDiffWindow, diff);
		double colorDiffMedian = getMedian(colorDiffWindow);
		prevValue = value;
		// RConsole.println("value: " + value);

		if (frontCSIgnoreCounter >= 10) {
			if (value > 400) {
				if (colorDiffMedian > LIGHT_DIFF) {
					latch = true;
				}
				if (latch && prevLatch) {
					prevLatch = false;
					return true;
				}
			}

		}

		prevLatch = latch;

		return false;
	}

	/**
	 * @return true if the object is styrofoam
	 */
	public boolean isObjectStyrofoam() {
		if (isStyrofoamColor() && !isWoodColor()) {
			Sound.beep();
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * Returns a boolean indicating if the object passes the red/blue ratio
	 * test.
	 * 
	 * @return true if it passes, false otherwise.
	 */
	private boolean isStyrofoamColor() {
		// RConsole.println(red + " " + green + " " + blue);

		/*
		 * Color color; int redValue; int blueValue; int greenValue; double
		 * ratio; int counter = 0;
		 * 
		 * 
		 * for(int i=0; i<10; i++){ color = frontCS.getColor();
		 * 
		 * redValue = color.getRed(); greenValue = color.getGreen(); blueValue =
		 * color.getBlue();
		 * 
		 * ratio = -1.0; if (blueValue != 0) { ratio = ((redValue / blueValue) *
		 * (greenValue / blueValue)); }
		 * 
		 * // conditions for styrofoam block if (ratio > .75 && ratio < .9) {
		 * counter++; // this is only true when 5-7 cm from block }
		 * 
		 * }
		 * 
		 * if(counter>=CONFIRMATION_MINIMUM){ return true; }
		 * 
		 * else{ return false; }
		 */

		Color color;
		double redValue;
		double blueValue;
		double ratio;
		int counter = 0;

		for (int i = 0; i < DEFAULT_NUM_OF_SAMPLES; i++) {
			color = frontCS.getColor();
			redValue = color.getRed();
			blueValue = color.getBlue();

			ratio = redValue / blueValue;

			if (ratio < UPPER_RED_BLUE_RATIO && blueValue > 15) {
				counter++;
			}
		}

		if (counter >= DEFAULT_CONFIRMATION_MINIMUM) {
			return true;
		}

		else {
			return false;
		}

	}

	/**
	 * Records the minimum distance and minimum angle during minimum distance
	 * scanning.
	 */
	private void minDistanceScanRoutine() {

		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);

		if (median <= scanMinDistance) {

			scanMinDistance = median;
			minDistanceAngle = MobileRobot.odo.getTheta();
		}

	}

	/**
	 * Returns a boolean indicating if the object passes the wood ratio
	 * test.
	 * 
	 * @return true if it passes, false otherwise.
	 */
	private boolean isWoodColor() {

		Color color;
		double redValue;
		double blueValue;
		double greenValue;
		double ratio;
		int counter = 0;

		for (int i = 0; i < DEFAULT_NUM_OF_SAMPLES; i++) {
			color = frontCS.getColor();
			redValue = color.getRed();
			blueValue = color.getBlue();
			greenValue = color.getGreen();

			ratio = ((redValue / blueValue) * (greenValue / blueValue));

			if (ratio > WOOD_RATIO) {
				counter++;
			}
		}

		if (counter >= DEFAULT_CONFIRMATION_MINIMUM) {
			// Sound.beep();
			return true;
		}

		else {
			return false;
		}
	}

}
