package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import lejos.nxt.UltrasonicSensor;

/**
 * 
 * Steps to run the test using RConsole 
 * 1. Get the color values when polling the color sensor for styrofoam, wooden block 
 * 2. Note distance from block.
 * 
 * @author Sidney Ng
 * 
 */
public class ColorRangeTest extends Thread {

	public static void main(String[] args) {
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		ColorSensor cs = new ColorSensor(SensorPort.S2);
		cs.setFloodlight(true);
		Color color;
		NXTRegulatedMotor clawMotor = Motor.C;

		clawMotor.setSpeed(60);
		clawMotor.rotateTo(330);

		RConsole.open(); // opens a USB connection with no timeout

		int buttonChoice;
		do {
			// try {
			// Thread.sleep(200); // sleep for fifth of a second
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			int distance = us.getDistance();
			color = cs.getColor();
			int redValue = color.getRed();
			int blueValue = color.getBlue();

			RConsole.println(Integer.toString(distance) + " "
					+ Integer.toString(redValue) + " "
					+ Integer.toString(blueValue));

			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		RConsole.close(); // closes the USB connection
	} // rotate 27 for 1 cm

}
