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

		Color color;

		RConsole.open(); // opens a USB connection with no timeout
		robot.setForwardSpeed(SensorMotorUser.FORWARD_SPEED);
		
		int buttonChoice;
		do {
//			try {
//				Thread.sleep(100); // sleep for quarter second
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			color = SensorMotorUser.leftCS.getColor();
			int colorValue = color.getBlue();
			color = SensorMotorUser.rightCS.getColor();
			int colorValue2 = color.getBlue();
			RConsole.println(colorValue + " " + colorValue2); // first is left, second is right
			
			//TODO: add line detection method
			
			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		robot.stopMotors();
		RConsole.close(); // closes the USB connection
	}

}
