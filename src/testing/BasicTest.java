package testing;

import lejos.nxt.Button;
import robot.MobileRobot;

/**
 * Test basic functionality of the MobileRobot ie. move forward and turning
 * hopefully don't have to get to the point of running these tests again
 * 
 * @author Sidney Ng
 * 
 */

public class BasicTest {

	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();

		int option = 0;

		while (option == 0)
			option = Button.waitForAnyPress();

		switch (option) {
		case Button.ID_LEFT:
			robot.moveForward(); // Move forward
			break;
		case Button.ID_RIGHT:
			robot.turnToOnPoint(180); // Turn
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}

		Button.waitForAnyPress();
		System.exit(0);
	}
}
