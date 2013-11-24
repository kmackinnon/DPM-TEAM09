package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import robot.AStar;
import robot.Dijkstra;
import robot.Intersection;
import robot.Map;
import robot.MobileRobot;

//Use to compare speed of AStar vs Dijkstra
public class PathSpeedTest {

	public static void main(String[] args) {
		
		long startTime;
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		Map.initializeMap();
		
		MobileRobot robot = new MobileRobot();
		
//		startTime = System.currentTimeMillis();
//		Dijkstra.algorithm(new Intersection(0,0), new Intersection(2,0));
//		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 2);
//		
//		startTime = System.currentTimeMillis();
//		Dijkstra.algorithm(new Intersection(2,0), new Intersection(2,2));
//		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 3);
//		
//		startTime = System.currentTimeMillis();
//		Dijkstra.algorithm(new Intersection(2,2), new Intersection(0,2));
//		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 4);
//		
//		startTime = System.currentTimeMillis();
//		Dijkstra.algorithm(new Intersection(0,2), new Intersection(0,0));
//		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 5);
		
		
		startTime = System.currentTimeMillis();
		AStar.algorithm(new Intersection(0,0), new Intersection(2,0));
		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 2);
		
		startTime = System.currentTimeMillis();
		AStar.algorithm(new Intersection(2,0), new Intersection(2,2));
		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 3);
		
		startTime = System.currentTimeMillis();
		AStar.algorithm(new Intersection(2,2), new Intersection(0,2));
		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 4);
		
		startTime = System.currentTimeMillis();
		AStar.algorithm(new Intersection(0,2), new Intersection(0,0));
		LCD.drawInt((int) (System.currentTimeMillis() - startTime), 0, 5);

		Button.waitForAnyPress();
		System.exit(0);
		
	}

}
