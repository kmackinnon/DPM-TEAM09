package robot;

import lejos.nxt.ColorSensor.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam.
 * 
 */
public class BlockDetector extends SensorMotorUser implements TimerListener {

	private Timer blockDetectorTimer;
	private Object lock;

	private final int DIST_TO_STOP = 11;
	private final int LIGHT_DIFF = 5;
	private final double RED_BLUE_RATIO = 1.8;
	private final double WOOD_RATIO = 1.9;

	private boolean isObjectDetected = false;
	private boolean doBlockDetection = false;
	private boolean doMinDistanceScan = false;

	private int scanMinDistance;
	private double minDistanceAngle;

	// variables for object detection
	private int[] window = { 255, 255, 255, 255, 255 };
	private int prevValue = 0;
	private boolean prevLatch = false;
	private int median, value, diff;
	private boolean latch;

	public BlockDetector() {
		lock = new Object();
		blockDetectorTimer = new Timer(DEFAULT_TIMER_PERIOD, this);
	}

	public void startBlockDetectorTimer() {
		blockDetectorTimer.start();
	}

	public void turnOnBlockDetection() {
		isObjectDetected = false;
		doBlockDetection = true;
	}

	public void turnOffBlockDetection() {
		doBlockDetection = false;
	}

	public void turnOnMinDistanceScanMode() {
		scanMinDistance = US_SENSOR_255;

		doMinDistanceScan = true;
	}

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

	public double getMinDistanceAngle() {

		if (scanMinDistance == US_SENSOR_255) {
			minDistanceAngle = DOUBLE_SPECIAL_FLAG;
		}

		return minDistanceAngle;

	}

	public double getMinDistance() {
		return scanMinDistance;
	}

	// returns the isObjectDetected boolean
	public boolean isObjectInFront() {
		boolean result;

		synchronized (lock) {
			result = isObjectDetected;
		}

		return result;
	}

	/**
	 * Returns a boolean indicating whether or not a block is detected by the
	 * ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	public boolean isObjectDetected() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);
		// RConsole.println("Distance: " + median);
		if (median <= DIST_TO_STOP) {
			return true;
		}

		// stopping by color sensor differential
		latch = false;
		value = frontCS.getRawLightValue();
		diff = value - prevValue;
		// RConsole.println("diff: " + diff);
		if (diff > LIGHT_DIFF) {
			latch = true;
		}
		if (latch && prevLatch) {
			prevLatch = false;
			return true;
		}
		prevValue = value;
		prevLatch = latch;

		return false;
	}
	
	public boolean isObjectStyrofoam(){
		if(isStyrofoamColor() && !isWoodColor()){
			return true;
		}
		
		else{
			return false;
		}
	}

	/**
	 * Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
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

			if (ratio < RED_BLUE_RATIO) {
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

	private void minDistanceScanRoutine() {

		shiftArrayByOne(window, getUSDistance());
		median = getMedian(window);

		if (median <= scanMinDistance) {

			scanMinDistance = median;
			minDistanceAngle = MobileRobot.odo.getTheta();
		}

	}

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
			return true;
		}

		else {
			return false;
		}
	}

}
