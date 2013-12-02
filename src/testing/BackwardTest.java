package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import robot.MobileRobot;

/**
 * Tests the robot's travel method when it is told to go back to its previous
 * target. This action is needed for when the robot finds an obstacle or
 * gets ready to pick up a styrofoam block.
 * 
 * @author Kevin
 * 
 */

public class BackwardTest {

	public static void main(String[] args) {

		int option = 0;

		while (option == 0)
			option = Button.waitForAnyPress();

		MobileRobot robot = new MobileRobot();

		MobileRobot.corr.startCorrectionTimer();
		MobileRobot.corr.turnOnCorrection();

		robot.initializePrevTarget(-30.48, -30.48);

		MobileRobot.corr.turnOffCorrection();
		robot.travelCoordinate(-30.48, -30.48, true);

		Sound.beep();

		Button.waitForAnyPress();

		System.exit(0);

	}

}
