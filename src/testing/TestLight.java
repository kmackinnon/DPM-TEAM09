package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.SensorMotorUser;

/**
 * NEW PURPOSE
 * make sure the color sensor works
 * should not have to run this test anymore
 * 
 * OLD
 * Prints varying color values to observe trends and light levels in different
 * areas.
 * 
 * @author Sidney Ng
 * 
 * 
 */
public class TestLight {

	public static void main(String[] args) {

		MobileRobot robot = new MobileRobot();
		ColorSensor cs = new ColorSensor(SensorPort.S2);
		// cs.setFloodlight(true);
		Color color;

		RConsole.open(); // opens a USB connection with no timeout
		robot.moveForward();

		int buttonChoice;
		do {
			// try {
			// Thread.sleep(250); // sleep for quarter second
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// int colorValue = cs.getRawLightValue();

			color = cs.getColor();
			int colorValue = color.getGreen();

			int leftTacho = SensorMotorUser.leftMotor.getTachoCount();
			int rightTacho = SensorMotorUser.rightMotor.getTachoCount();
			RConsole.println(Integer.toString(colorValue) + " "
					+ Integer.toString(leftTacho) + " "
					+ Integer.toString(rightTacho));

			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);
		robot.stopMoving();
		RConsole.close(); // closes the USB connection
	}

}
