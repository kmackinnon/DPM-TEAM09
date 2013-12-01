package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import robot.MobileRobot;

/**
 * Tests rotation correction (also known as light localization)
 * 
 * @author Kevin Musgrave
 * 
 */

public class RotateCorrTest {

	public static void main(String[] args) {

		int option = 0;

		while (option == 0)
			option = Button.waitForAnyPress();

		MobileRobot robot = new MobileRobot();

		MobileRobot.corr.turnOnLightSensors();
		MobileRobot.corr.startCorrectionTimer();
		robot.liftClaw();

		robot.performRotationCorrectionLocalization();

		// robot.travelCoordinate(0,0,true);

		Sound.beep();

	}

}
