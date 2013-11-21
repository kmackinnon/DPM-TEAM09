package testing;

import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;
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

	public static void main(String[] args) {
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		switch(option) {
		case Button.ID_ENTER:
//			RConsole.open();
			Localizer local = new Localizer();
			local.localize();

//			RConsole.println("x: " + MobileRobot.odo.getX());
//			RConsole.println("y: " + MobileRobot.odo.getY());
//			RConsole.println("theta: " + MobileRobot.odo.getTheta());
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
//		RConsole.close();
		System.exit(0);

	}
	
}
