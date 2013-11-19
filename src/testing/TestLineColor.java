package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.SensorMotorUser;
import robot.OdometryCorrection;

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
public class TestLineColor extends OdometryCorrection {
	
	
	public TestLineColor() {
		super();
	}

	public static void main(String[] args) {
		
		MobileRobot robot = new MobileRobot();
		TestLineColor tester = new TestLineColor();

		//Color color;

//		RConsole.open(); // opens a USB connection with no timeout
		robot.moveForward();
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

			// Tested line color
//			color = SensorMotorUser.leftCS.getColor();
//			int colorValue = color.getBlue();
//			color = SensorMotorUser.rightCS.getColor();
//			int colorValue2 = color.getBlue();
//			RConsole.println(colorValue + " " + colorValue2); // first is left, second is right
			
			if (tester.lineDetected(SensorMotorUser.leftCS)) {
//				RConsole.println("left"); // for debugging
				left = true;
				//Sound.beep();
			}
			if (tester.lineDetected(SensorMotorUser.rightCS)) {
//				RConsole.println("right"); // for debugging
				right = true;
				//Sound.beep();
			}
			if (left && right) {
				RConsole.println("both");
				Sound.beep();
				left = false;
				right = false;
			}
			
			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		robot.stopMoving();
		RConsole.close(); // closes the USB connection
	}

}
