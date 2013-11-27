package testing;

import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;
import robot.AStar;
import robot.Dijkstra;
import robot.Intersection;
import robot.Map;

//Use to compare speed of AStar vs Dijkstra
public class PathSpeedTest {

	public static void main(String[] args) {
		
		long startTime;
		
		int option = 0;
		
		while (option == 0)
			option = Button.waitForAnyPress();
		
		RConsole.open();
		
		Map.initializeMap();
		Map.setTargetZone(new int[] {7,6,8,8});
		Map.setForbiddenZone(new int[] { 0, 1, 4, 3 });
		
		//MobileRobot robot = new MobileRobot();
		
		startTime = System.currentTimeMillis();
		Dijkstra.algorithmForTargetZone(new Intersection(0,0));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		Dijkstra.algorithmForTargetZone(new Intersection(10,0));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		Dijkstra.algorithmForTargetZone(new Intersection(10,10));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		Dijkstra.algorithmForTargetZone(new Intersection(0,10));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		
		startTime = System.currentTimeMillis();
		AStar.algorithmForTargetZone(new Intersection(0,0));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		AStar.algorithmForTargetZone(new Intersection(10,0));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		AStar.algorithmForTargetZone(new Intersection(10,10));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");
		
		startTime = System.currentTimeMillis();
		AStar.algorithmForTargetZone(new Intersection(0,10));
		RConsole.println((int) (System.currentTimeMillis() - startTime) + "");

		Button.waitForAnyPress();
		RConsole.close();
		System.exit(0);
		
	}

}
