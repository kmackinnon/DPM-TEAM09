package testing;

import lejos.nxt.Button;
import robot.MobileRobot;

/**
 * Test functionality of the Mobile Robot and accuracy of the Odometer.
 * 1. Drive Robot in a square - measure x and y from actual position and theta from actual heading
 * 	  Use the travelMag methods
 * 2. Do the same as (1) but use the travelCoordinate method and travel in a triangle
 * 
 * @author Sidney Ng
 *
 */

public class OdometerTest {
	
	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();

		int option = 0; // don't bother with button inputs
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
		case Button.ID_LEFT:
			squareTest(robot);
			break;
		case Button.ID_RIGHT:
			triangleTest(robot);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	public static void squareTest(MobileRobot robot) {
		robot.travelMag(60.96);
		robot.turnTo(MobileRobot.odo.getAng() + 90.0);
		robot.travelMag(60.96);
		robot.turnTo(MobileRobot.odo.getAng() + 90.0);
		robot.travelMag(60.96);
		robot.turnTo(MobileRobot.odo.getAng() + 90.0);
		robot.travelMag(60.96);
		robot.turnTo(0.0);
	}
	
	public static void triangleTest(MobileRobot robot) {
		robot.travelCoordinate(60.0, 30.0);
		robot.travelCoordinate(30.0, 30.0);
		robot.travelCoordinate(30.0, 60.0);
		robot.travelCoordinate(60.0, 0.0);
	}
}
