package testing;

import java.util.ArrayList;

import lejos.nxt.comm.RConsole;
import robot.Dijkstra;
import robot.Intersection;
import robot.Map;

public class DijkstraTest {
	
	public static void main (String[] args) {
		int[] forbiddenZone = {2, 4, 5, 6};
		int[] startCorner = {0, 0};
		RConsole.open();
		Map.initializeMap();
		
		Intersection startingPoint = new Intersection(0,0);
		
		Intersection destination = new Intersection(10,10);
		
		ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(startingPoint,destination);
		
		for (int i = 0; i < listOfWayPoints.size(); i++) {
			int x = listOfWayPoints.get(i).getX();
			int y = listOfWayPoints.get(i).getY();
			RConsole.println(x + " " + y);
		}
		
		/*
		 * +--+--+--+--+
		 * +--+--+--+--+
		 * +--+--+--+--+
		 * +--+--+--+--+
		 * +--+--+--+--+
		 * 
		 */
		int k = 0;
		for (int y = 0; y < Map.NUM_OF_INTERSECTIONS + 2; y++) {
			for (int x = 0; x < Map.NUM_OF_INTERSECTIONS + 1; x++) {
				if (listOfWayPoints.get(k).getX() == (x - 1) && listOfWayPoints.get(k).getY() == (y - 2)) {
					RConsole.print("x--");
					k++;
				}
				RConsole.print("+--");
			}
			RConsole.print("+\n");
		}
	}
}
