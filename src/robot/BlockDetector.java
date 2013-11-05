package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.UltrasonicSensor;

/**
 * BlockDetector uses the ultrasonic sensor and light sensor to detect blocks
 * that are close by. The light sensor identifies the block as wooden or
 * styrofoam
 * 
 */
public class BlockDetector extends SensorUser{

	private UltrasonicSensor ultrasonicSensor;
	private ColorSensor colorSensor;
	
	public BlockDetector() {
		
		ultrasonicSensor = HardwareInfo.ultrasonicSensor;
		colorSensor = HardwareInfo.frontCS;

	}
	
	// TODO fill in
	/**
	 * Returns a boolean indicating whether or not a block is detected by the ultrasonic or color sensor.
	 * 
	 * @return true if a block is detected, false otherwise.
	 */
	public boolean frontDetectBlock() {
		
		return false;
	}

	// TODO fill in
	/**Returns a boolean indicating whether or not the block is styrofoam.
	 * 
	 * @return true if styrofoam, false otherwise.
	 */
	public boolean isStyrofoam() {

		return false;
	}

	// TODO fill in
	/**Returns a boolean indicating whether or not the block is wooden.
	 * 
	 * @return true if wooden, false otherwise.
	 */
	public boolean isWood() {

		return false;
	}

}
