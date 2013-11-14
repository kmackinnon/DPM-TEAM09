package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import robot.MobileRobot;
import robot.SensorMotorUser;

/**
 * Test accuracy of the ultrasonic sensor.
 * 
 * 1. Start motors forward and poll ultrasonic sensor until the robot is a
 * certain distance from wall. Measure actual distance, distance perceived by
 * ultrasonic, and error - change distance.
 * 
 * 2. Potentially change the sleepTime. More frequent polling will help with
 * accuracy
 * 
 * @author Sidney Ng
 * 
 */

public class TestUltrasonic extends Thread {

	public static void main(String[] args) {

		int distToStop = 20; // change this variable
		int sleepTime = 250; // in milliseconds, may have to change

		RConsole.openBluetooth(10000); // opens a Bluetooth connection

		SensorMotorUser.clawMotor.setSpeed(60);
		SensorMotorUser.clawMotor.rotateTo(320);

		int buttonChoice;
		MobileRobot robot = new MobileRobot();
		robot.startMotors();

		do {
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int distValue = robot.getUSDistance();
			RConsole.println(Integer.toString(distValue));
			LCD.clear();
			LCD.drawInt(distValue, 0, 0);

			if (distValue <= distToStop) {
				robot.setSpeeds(0, 0);
			}

			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);

		RConsole.close(); // closes the USB connection
	}

}
