package testing;

import lejos.nxt.Button;
import robot.MobileRobot;

/**
 * Very basic integration testing. Put together Odometry Correction and Block Detection.
 * NOTE: robot will only move straight
 * Make sure there is an obstacle along the path the robot will move
 * 
 * @author Sidney Ng
 *
 */
public class OdoAndBlock {

	public static void main (String[] args) {
		MobileRobot robot = new MobileRobot();
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
			case Button.ID_ENTER:
				robot.liftClaw();
				MobileRobot.corr.turnOnCorrection();
				MobileRobot.blockDetector.turnOnBlockDetection();
				robot.travelCoordinate(0, 152.4, true);
//				robot.moveForward();
//				int buttonChoice;
//				do {
//					if (MobileRobot.blockDetector.isObjectInFront()) {
//						robot.stopMoving();
//					}
//					buttonChoice = Button.readButtons();
//				} while (buttonChoice != Button.ID_ESCAPE);
				break;
			default:
				System.out.println("Error - invalid button");
				System.exit(-1);
				break;
		} // end of switch
		
		System.exit(0);
	} // end of main
	
}
