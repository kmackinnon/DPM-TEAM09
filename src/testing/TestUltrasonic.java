package testing;

import robot.MobileRobot;
import lejos.nxt.Button;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;

/**
 * Test accuracy of the ultrasonic sensor.
 * 1. Start motors forward and poll ultrasonic sensor until the robot is a certain distance from wall.
 * 	  Measure actual distance, distance perceived by ultrasonic, and error - change distance, try again
 * 
 * 2. Potentially change the sleepTime. hopefully more frequent polling will help with accuracy
 * 
 * @author Sidney Ng
 *
 */

public class TestUltrasonic extends Thread {

	public static void main(String[] args) {

		int distToStop = 20; // change this variable
		int sleepTime = 250; // in milliseconds, may have to change
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);

		RConsole.open(); // opens a USB connection with no timeout

		int buttonChoice;
		MobileRobot robot = new MobileRobot();
		robot.startMotors();
		do {
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int distValue = us.getDistance();
			RConsole.println(Integer.toString(distValue));
			if (distValue <= distToStop) {
				robot.stopMotors(); // stops robot at a distance from the wall
			}
			
			buttonChoice = Button.readButtons();

		} while (buttonChoice != Button.ID_ESCAPE);

		RConsole.close(); // closes the USB connection
	}
	
}
