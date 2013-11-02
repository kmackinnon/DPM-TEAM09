package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;

/**
 * 
 * @author Keith MacKinnon
 * 
 * Steps for using RConsole
 * 1. Turn on the robot
 * 2. Run program
 * 3. RConsole Connect
 * 4. Press ESC on robot (down button)
 * 5. RConsole Disconnect
 * 6. Return to step 2
 * 
 */
public class TestLight extends Thread {

	public static void main(String[] args) {

		ColorSensor cs = new ColorSensor(SensorPort.S1);

		RConsole.open(); // opens a USB connection with no timeout

		int buttonChoice;

		do {
			try {
				Thread.sleep(250); // sleep for quarter second
			} catch (Exception e) {
				e.printStackTrace();
			}

			int colorValue = cs.getRawLightValue();
			RConsole.println(Integer.toString(colorValue));

			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);

		RConsole.close(); // closes the USB connection
	}

}
