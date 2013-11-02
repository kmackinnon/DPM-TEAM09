package robot;
/**
 * Map contains a representation of the game area. This includes the locations
 * of obstacles, the red and green zones, and the 4 corners
 */

import java.util.ArrayList;

public class Map {

	public static ArrayList<Coordinates> obstacles;

	public static double topBoundary;
	public static double bottomBoundary;
	public static double leftBoundary;
	public static double rightBoundary;

	public static SquareRegion redZone;
	public static SquareRegion greenZone;
	public static SquareRegion targetZone;

	public static SquareRegion cornerX1;
	public static SquareRegion cornerX2;
	public static SquareRegion cornerX3;
	public static SquareRegion cornerX4;

	
	public static void addObstacle(double x, double y){
		
	}
	
	
	public static void setTargetZone(SquareRegion target) {
		targetZone = target;
	}

	public static void setRedZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		redZone = new SquareRegion(topRightCorner, bottomLeftCorner);
	}

	public static void setGreenZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {
		greenZone = new SquareRegion(topRightCorner, bottomLeftCorner);
	}

	public boolean isNearForbiddenRegion(double x, double y) {

		if (redZone.isNear(x, y)) {
			return true;
		}

		else if (greenZone.isNear(x, y)) {
			return true;
		}

		else if (cornerX1.isNear(x, y)) {
			return true;
		}

		else if (cornerX2.isNear(x, y)) {
			return true;
		}

		else if (cornerX3.isNear(x, y)) {
			return true;
		}

		else if (cornerX4.isNear(x, y)) {
			return true;
		}

		else {
			return false;
		}
	}

	// TODO fill in
	public ArrayList<Coordinates> findBestPath(Coordinates current, Coordinates target){

		return new ArrayList<Coordinates>();
	}

}
