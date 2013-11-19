package robot;

import lejos.nxt.comm.RConsole;


/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam
 * 
 */
public class BlockDetector extends SensorMotorUser {

	private static final int DIST_TO_STOP = 11;

	private static final int LIGHT_DIFF = 5;

	public BlockDetector() {

	}

	private int[] window = { 255, 255, 255, 255, 255 };
	private int prevValue = 0;
	private boolean prevLatch = false;

	/**
	 * Returns a boolean indicating whether or not a block is detected by the
	 * ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	
	public boolean objectDetected() {
		// SensorMotorUser.frontCS.setFloodlight(true);

		// stopping by ultrasonic sensor
		shiftArrayByOne(window, ultrasonicSensor.getDistance());
		int median = getMedian(window);
//		RConsole.println("Distance: " + median);
		if (median <= DIST_TO_STOP) {
			return true;
		}

		// stopping by color sensor differential
		boolean latch = false;
		int value = frontCS.getRawLightValue();
		int diff = value - prevValue;
//		RConsole.println("diff: " + diff);
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

	/**
	 * Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	public boolean isStyrofoam(double red, double green, double blue) {

		//RConsole.println(red + " " + green + " " + blue);

		double testValue = -1.0;
		if (blue != 0) {
			testValue = ((red / blue) * (green / blue));
		}

		//RConsole.println("testValue: " + testValue);

		if (testValue > .75 && testValue < .9 ) {
			return true; // this is only true when 5-7 cm from block
		}

		return false;
	}

	/**
	 * Returns a boolean indicating whether or not the block is wooden.
	 * 
	 * @return true if wooden, false otherwise.
	 */
	public boolean isWood(double red, double green, double blue) {
		
		double testValue = -1.0;
		
		testValue = ((red / blue) * (green / blue));
		
		if (testValue > 1.9){
			return true;
		}

		return false;
	}

}
