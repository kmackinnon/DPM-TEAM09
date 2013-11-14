package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import robot.Localizer;
import robot.MobileRobot;

/**
 * Graph Falling edge localization to determine best threshold and noise margin.
 * 1. turn 360 degrees while polling
 * 
 * @author Sidney Ng
 *
 */

public class TestUsLocal {

	public static void main(String[] args) throws InterruptedException {
		
		int option = 0; // don't bother with button inputs
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
		case Button.ID_ENTER:
			Localizer local = new Localizer();
			local.localize();

			LCD.drawString("x: " + MobileRobot.odo.getX(), 0, 0);
			LCD.drawString("y: " + MobileRobot.odo.getY(), 0, 1);
			LCD.drawString("theta: " + MobileRobot.odo.getAng(), 0, 2);
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		Button.waitForAnyPress();
		System.exit(0);

	}
	
}
