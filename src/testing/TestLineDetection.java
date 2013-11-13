package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.SensorMotorUser;

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
public class TestLineDetection extends Thread {

	public static void main(String[] args) {
		
		MobileRobot robot = new MobileRobot();
		ColorSensor lcs = new ColorSensor(SensorPort.S3);
		ColorSensor rcs = new ColorSensor(SensorPort.S3);

		Color color;

		RConsole.open(); // opens a USB connection with no timeout
		robot.startMotors();
		
		int buttonChoice;
		do {
			try {
				Thread.sleep(100); // sleep for quarter second
			} catch (Exception e) {
				e.printStackTrace();
			}

			color = lcs.getColor();
			int colorValue = color.getGreen();
			color = rcs.getColor();
			int colorValue2 = color.getGreen();
			RConsole.println(colorValue + " " + colorValue2); // first is left, second is right
			
			//TODO: add line detection method
			
			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		robot.stopMotors();
		RConsole.close(); // closes the USB connection
	}

}
