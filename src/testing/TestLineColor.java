package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.SensorMotorUser;

/**
 * 
 * The purpose of the test is to find a common light pattern when crossing black lines.
 * 
 * Steps for using RConsole
 * 1. Turn on the robot
 * 2. Run program
 * 3. RConsole Connect
 * 4. Press ESC on robot (down button)
 * 5. RConsole Disconnect
 * 6. Return to step 2
 * 
 * @author Keith MacKinnon and Sidney Ng
 * 
 */
public class TestLineColor /*extends Thread*/ {

	public static void main(String[] args) {
		
		MobileRobot robot = new MobileRobot();

		//Color color;

		RConsole.open(); // opens a USB connection with no timeout
		robot.setForwardSpeed(SensorMotorUser.FORWARD_SPEED);
		SensorMotorUser.leftCS.setFloodlight(true);
		SensorMotorUser.rightCS.setFloodlight(true);
		int buttonChoice;
		boolean left = false;
		boolean right = false;
		do {
//			try {
//				Thread.sleep(50); // sleep for quarter second
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

//			color = SensorMotorUser.leftCS.getColor();
//			int colorValue = color.getBlue();
//			color = SensorMotorUser.rightCS.getColor();
//			int colorValue2 = color.getBlue();
//			RConsole.println(colorValue + " " + colorValue2); // first is left, second is right
			
			//TODO: add line detection method
			if (robot.lineDetected(SensorMotorUser.leftCS, true)) {
				RConsole.println("left");
				left = true;
			}
			if (robot.lineDetected(SensorMotorUser.rightCS, false)) {
				RConsole.println("right");
				right = true;
			}
			if (left && right) {
				RConsole.println("both");
				Sound.beep();
				left = false;
				right = false;
			}
			
			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		robot.setSpeeds(0.0, 0.0);
		RConsole.close(); // closes the USB connection
	}

}
