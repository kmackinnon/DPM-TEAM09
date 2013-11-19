package testing;

import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.Odometer;
import robot.OdometryCorrection;

/**
 * Test accuracy of Odometer by itself, and test Odometer with correction
 * 
 * @author Sidney Ng
 *
 */

public class OdometerTest {
	
	public static void main(String[] args) {
		MobileRobot robot = new MobileRobot();
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
//		RConsole.open();
		
		switch(option) {
		case Button.ID_LEFT:
			OdometryCorrection corr = new OdometryCorrection(MobileRobot.odo);
			corr.turnOnCorrection();
			squareTest(robot);
			break;
		case Button.ID_RIGHT:
			squareTest(robot);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		Button.waitForAnyPress();
//		RConsole.close();
		System.exit(0);
	}
	
	public static void squareTest(MobileRobot robot) {
		robot.travelCoordinate(0,60.96, true);
		robot.travelCoordinate(60.96,60.96, true);
		robot.travelCoordinate(60.96,0, true);
		robot.travelCoordinate(0,0, true);
		robot.turnToOnPoint(0.0);
	}
}
