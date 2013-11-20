package testing;

import java.util.ArrayList;

import lejos.nxt.comm.RConsole;
import robot.Dijkstra;
import robot.Intersection;
import robot.Map;
import robot.SensorMotorUser;

public class DijkstraTest {

	public static void main(String[] args) {
		int[] forbiddenZone = { 2, 4, 5, 6 };
		int[] targetZone = {3,9,4,10};
		
		RConsole.open();
		Map.initializeMap();
		
	

		Map.setForbiddenZone(forbiddenZone);
		Map.setTargetZone(targetZone);
		
		

//		ArrayList<Intersection> testTargetZone = Map.getTargetZone();
//		
//		for(Intersection intersection : testTargetZone){
//			
//			if(intersection.isTarget()){
//			
//				RConsole.println(intersection.getX() + " " + intersection.getY());
//			}
//		}
//		
//		RConsole.print("\n");
//		RConsole.print("\n");
//		RConsole.print("\n");
		

		Intersection startingPoint = new Intersection(0, 0);

		Intersection destination = new Intersection(10, 10);

		//ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(
			//	startingPoint, destination);
				
		ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithmForTargetZone(startingPoint);

		for (int i = 0; i < listOfWayPoints.size(); i++) {
			int x = listOfWayPoints.get(i).getX();
			int y = listOfWayPoints.get(i).getY();
			RConsole.println(x + " " + y);
		}

		/*
		 * +--+--+--+--+ +--+--+--+--+ +--+--+--+--+ +--+--+--+--+ +--+--+--+--+
		 */
		int k = 0;
		for (int x = 0; x < Map.NUM_OF_INTERSECTIONS + 2; x++) {

			RConsole.print("+  ");

		}

		RConsole.print("\n");

		for (int y = 0; y < Map.NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < Map.NUM_OF_INTERSECTIONS + 1; x++) {
				if ((k<listOfWayPoints.size()) && listOfWayPoints.get(k).getX() == (x - 1)
						&& listOfWayPoints.get(k).getY() == (y)) {
					RConsole.print("x  ");
					k++;
				}

				else if ((x > 0) && (y < Map.NUM_OF_INTERSECTIONS)
						&& Map.getIntersection((x - 1), y) == null) {
					RConsole.print("#  ");
				}
				
				else if((x > 0) && (y < Map.NUM_OF_INTERSECTIONS)
						&& Map.getIntersection((x - 1), y).isTarget()) {
					RConsole.print("$  ");
				}

				else {
					RConsole.print("+  ");
				}
			}
			RConsole.print("+\n");
		}

		for (int x = 0; x < Map.NUM_OF_INTERSECTIONS + 2; x++) {

			RConsole.print("+  ");

		}
	}
}
