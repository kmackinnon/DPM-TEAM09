package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;

/**
 * 
 * Steps for using RConsole 
 * 1. Using data from ColorRangeTest, apply a method to do block detection 
 * 	a. Ratio of Values 
 * 	b. //TODO
 *         
 * @author Sidney Ng
 * 
 */
public class BlockTest extends Thread {

	public static void main(String[] args) {

		ColorSensor cs = new ColorSensor(SensorPort.S2);
		cs.setFloodlight(true);
		Color color;

		RConsole.open(); // opens a USB connection with no timeout

		int buttonChoice;
		do {
			try {
				Thread.sleep(100); // sleep for tenth of a second
			} catch (Exception e) {
				e.printStackTrace();
			}

			color = cs.getColor();
			int redValue = color.getRed();
			int greenValue = color.getGreen();
			int blueValue = color.getBlue();
			// boolean block;

			RConsole.println(Integer.toString(redValue) + " "
					+ Integer.toString(greenValue) + " "
					+ Integer.toString(blueValue));

			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		RConsole.close(); // closes the USB connection
	}

}
