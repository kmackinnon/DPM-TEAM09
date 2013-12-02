package testing;

import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;
import robot.Map;
import robot.MobileRobot;

/**
 * Test accuracy of Odometer by itself, and test Odometer with correction
 * 
 * @author Sidney Ng
 *
 */

public class OdometerTest {
	
	public static void main(String[] args) {
		
		Map.initializeMap();
		
		MobileRobot robot = new MobileRobot();

		
		RConsole.open();
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
		case Button.ID_LEFT:
			MobileRobot.corr.turnOnLightSensors();
			MobileRobot.corr.startCorrectionTimer();
			MobileRobot.corr.turnOnCorrection();
			triangleTest(robot);
			break;
		case Button.ID_RIGHT:
			MobileRobot.corr.turnOnLightSensors();
			MobileRobot.corr.startCorrectionTimer();
			MobileRobot.corr.turnOnCorrection();
			squareTest(robot);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		Button.waitForAnyPress();
		RConsole.close();
		System.exit(0);
	}
	
	
	public static void triangleTest(MobileRobot robot) {
		robot.travelCoordinate(60.96,60.96, false);
		robot.travelCoordinate(0,121.92, false);
		robot.travelCoordinate(0,0, true);
		robot.turnToOnPoint(0.0);
	}
	
	
	
	public static void squareTest(MobileRobot robot) {
		robot.travelCoordinate(0,60.96, false);
		robot.travelCoordinate(60.96,60.96, false);
		robot.travelCoordinate(60.96,0, false);
		robot.travelCoordinate(0,0, true);
		robot.turnToOnPoint(0.0);
	}
}
