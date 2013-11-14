package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import robot.MobileRobot;

/**
 * Test basic functionality of the MobileRobot ie. move forward and turning
 * 
 * @author Sidney Ng
 * 
 */

public class BasicTest {

	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();

		int option = 0; // don't bother with button inputs

		while (option == 0)
			option = Button.waitForAnyPress();

		switch (option) {
		case Button.ID_LEFT:
			robot.travelMag(60.69); // Move forward

			break;
		case Button.ID_RIGHT:
			robot.turnTo(180); // Turn
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		LCD.drawString("Theta: " + MobileRobot.odo.getAng(), 0, 0);

		Button.waitForAnyPress();
		System.exit(0);
	}
}
