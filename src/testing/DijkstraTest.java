package testing;

import java.util.ArrayList;
import java.util.Iterator;

import lejos.nxt.comm.RConsole;
import robot.Dijkstra;
import robot.Intersection;
import robot.Map;

public class DijkstraTest {

	public static void main(String[] args) {
		int[] forbiddenZone = { 2, 4, 5, 6 };
		int[] targetZone = { 4, 9, 5, 10 };

		RConsole.open();
		Map.initializeMap();

		Map.setForbiddenZone(forbiddenZone);
		Map.setTargetZone(targetZone);
		

		
		

		// ArrayList<Intersection> testTargetZone = Map.getTargetZone();
		//
		// for(Intersection intersection : testTargetZone){
		//
		// if(intersection.isTarget()){
		//
		// RConsole.println(intersection.getX() + " " + intersection.getY());
		// }
		// }
		//
		// RConsole.print("\n");
		// RConsole.print("\n");
		// RConsole.print("\n");

		// If you put the starting point anywhere above the first row, then the
		// list of way points will not print in the graphic
		Intersection startingPoint = new Intersection(5, 0);

		Intersection destination = new Intersection(10, 10);

		// ArrayList<Intersection> listOfWayPoints = Dijkstra.algorithm(
		// startingPoint, destination);

		ArrayList<Intersection> listOfWayPoints = Dijkstra
				.algorithmForTargetZone(startingPoint);

		Map.removeEdge(new Intersection(5,3), new Intersection(6,4));
		Map.removeEdge(new Intersection(6,3), new Intersection(6,4));
		Map.removeEdge(new Intersection(7,3), new Intersection(6,4));
		
		Map.removeEdge(new Intersection(6,3), new Intersection(7,4));
		Map.removeEdge(new Intersection(7,3), new Intersection(7,4));
		Map.removeEdge(new Intersection(8,3), new Intersection(7,4));
		
		Map.removeEdge(new Intersection(7,3), new Intersection(8,4));
		Map.removeEdge(new Intersection(8,3), new Intersection(8,4));
		Map.removeEdge(new Intersection(9,3), new Intersection(8,4));
		
		Map.removeEdge(new Intersection(8,3), new Intersection(9,4));
		Map.removeEdge(new Intersection(9,3), new Intersection(9,4));
		Map.removeEdge(new Intersection(10,3), new Intersection(9,4));
		
		Map.removeEdge(new Intersection(9,3), new Intersection(10,4));
		
		for (int i = 0; i < listOfWayPoints.size(); i++) {
			int x = listOfWayPoints.get(i).getX();
			int y = listOfWayPoints.get(i).getY();
			RConsole.println(x + " " + y);
			
			
			if(i< listOfWayPoints.size()-1 && !listOfWayPoints.get(i).getAdjacencyList().contains(listOfWayPoints.get(i+1))){
				ArrayList<Intersection> secondPartOfNewRoute = Dijkstra.algorithmForTargetZone(listOfWayPoints.get(i));
				
				for(int j=listOfWayPoints.size()-1; j>=i+1.;j--){
					listOfWayPoints.remove(j);
				}

				for(int j=0; j < secondPartOfNewRoute.size();j++){
					listOfWayPoints.add(secondPartOfNewRoute.get(j));
				}
			}
			
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
				if ((k < listOfWayPoints.size())
						&& listOfWayPoints.get(k).getX() == (x - 1)
						&& listOfWayPoints.get(k).getY() == (y)) {
					RConsole.print("x  ");
					k++;
				}

				else if ((x > 0) && (y < Map.NUM_OF_INTERSECTIONS)
						&& Map.getIntersection((x - 1), y) == null) {
					RConsole.print("#  ");
				}

				else if ((x > 0) && (y < Map.NUM_OF_INTERSECTIONS)
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
