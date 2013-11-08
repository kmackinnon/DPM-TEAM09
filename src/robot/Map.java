package robot;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Map contains a representation of the game area. This includes the locations
 * of obstacles, the red and green zones, and the 4 corners
 */

public class Map {

	public final static double TILE_SIZE = 30.48;

	/** There are 11 intersections in both the x and y directions. */
	public final static int NUM_OF_INTERSECTIONS = 11;

	private static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();
	private static ArrayList<Edge> edgeList = new ArrayList<Edge>();

	/**
	 * Initializes the arraylist of intersections.
	 */
	public static void initializeMap() {

		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
				intersectionList.add(new Intersection(x, y));
			}
		}

		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
				addEdgesToInitialMap(x, y);
			}
		}

	}

	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the redZone as "forbidden". (The green zone would be
	 * forbidden for a garbage collector)
	 * 
	 * @param topRightCorner
	 *            the xy coordinates of the top right corner of the forbidden
	 *            zone.
	 * 
	 * @param bottomLeftCorner
	 *            the xy coordinates of the bottom left corner of the forbidden
	 *            zone
	 * 
	 */
	public static void setForbiddenZone(int[] topRightCorner,
			int[] bottomLeftCorner) {
		
		setZone(topRightCorner,bottomLeftCorner,false);
	}

	/**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the greenZone as "target". (The red zone would be the
	 * target for a garbage collector)
	 * 
	 * @param topRightCorner
	 *            the xy coordinates of the top right corner of the target zone.
	 * 
	 * @param bottomLeftCorner
	 *            the xy coordinates of the bottom left corner of the target
	 *            zone
	 * 
	 */
	public static void setTargetZone(int[] topRightCorner,
			int[] bottomLeftCorner) {

		setZone(topRightCorner,bottomLeftCorner,true);
	}
	
	
	private static void setZone(int[] topRightCorner,
			int[] bottomLeftCorner, boolean keepNode){
		
		int topRightXGrid = topRightCorner[0];
		int bottomLeftXGrid = bottomLeftCorner[0];

		int topRightYGrid = topRightCorner[1];
		int bottomLeftYGrid = bottomLeftCorner[1];

		for (int x = bottomLeftXGrid; x <= topRightXGrid; x++) {
			for (int y = bottomLeftYGrid; y <= topRightYGrid; y++) {
				if(keepNode){
					get(index(x,y)).setAsTarget();
				}
				
				else{
					removeIntersection(index(x, y));
				}
			}
		}
		
	}
	

	private static int index(int x, int y) {

		return (y * NUM_OF_INTERSECTIONS + x);

	}

	private static Intersection get(int index) {

		return intersectionList.get(index);

	}

	private static void addToEdgeList(Edge edge) {

		if (!edgeList.contains(edge)) {
			edgeList.add(edge);
		}

	}


	private static void removeIntersection(int index) {

		Intersection intersection = get(index);

		Iterator<Edge> it = edgeList.iterator();

		for (; it.hasNext();) {
			if (it.next().touches(intersection)) {
				it.remove();
			}
		}

		intersectionList.set(index, null);

	}

	
	
	
	private static void addEdgesToInitialMap(int x, int y) {

		Intersection temp = get(index(x, y));

		if (x == 0 && y == 0) {
			addTopRightCap(temp, x, y);
		}

		else if (x == 0 && y == 10) {
			addBottomRightCap(temp, x, y);
		}

		else if (x == 0) {
			addRightSide(temp, x, y);
			addTopAndBottom(temp, x, y);
		}

		else if (x == 10 && y == 0) {
			addTopLeftCap(temp, x, y);
		}

		else if (y == 0) {
			addTopSide(temp, x, y);
			addRightAndLeft(temp, x, y);
		}

		else if (x == 10 && y == 10) {
			addBottomLeftCap(temp, x, y);
		}

		else if (x == 10) {
			addLeftSide(temp, x, y);
			addTopAndBottom(temp, x, y);
		}

		else if (y == 10) {
			addBottomSide(temp, x, y);
			addRightAndLeft(temp, x, y);
		}
	}
	
	
	
	private static Intersection right(int x, int y) {
		return intersectionList.get(index(x + 1, y));
	}

	private static Intersection topRight(int x, int y) {
		return intersectionList.get(index(x + 1, y + 1));
	}

	private static Intersection bottomRight(int x, int y) {
		return intersectionList.get(index(x + 1, y - 1));
	}

	private static Intersection left(int x, int y) {
		return intersectionList.get(index(x - 1, y));
	}

	private static Intersection topLeft(int x, int y) {
		return intersectionList.get(index(x - 1, y + 1));
	}

	private static Intersection bottomLeft(int x, int y) {
		return intersectionList.get(index(x - 1, y - 1));
	}

	private static Intersection top(int x, int y) {
		return intersectionList.get(index(x, y + 1));
	}

	private static Intersection bottom(int x, int y) {
		return intersectionList.get(index(x, y - 1));
	}

	private static void addRightSide(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, bottomRight(x, y)));
		addToEdgeList(new Edge(temp, right(x, y)));
		addToEdgeList(new Edge(temp, topRight(x, y)));
	}

	private static void addLeftSide(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, topLeft(x, y)));
		addToEdgeList(new Edge(temp, left(x, y)));
		addToEdgeList(new Edge(temp, bottomLeft(x, y)));
	}

	private static void addTopAndBottom(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, top(x, y)));
		addToEdgeList(new Edge(temp, bottom(x, y)));
	}

	private static void addRightAndLeft(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, right(x, y)));
		addToEdgeList(new Edge(temp, left(x, y)));
	}

	private static void addTopSide(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, topRight(x, y)));
		addToEdgeList(new Edge(temp, top(x, y)));
		addToEdgeList(new Edge(temp, topLeft(x, y)));
	}

	private static void addBottomSide(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, bottomLeft(x, y)));
		addToEdgeList(new Edge(temp, bottom(x, y)));
		addToEdgeList(new Edge(temp, bottomRight(x, y)));
	}

	private static void addTopRightCap(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, right(x, y)));
		addToEdgeList(new Edge(temp, topRight(x, y)));
		addToEdgeList(new Edge(temp, top(x, y)));
	}

	private static void addTopLeftCap(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, top(x, y)));
		addToEdgeList(new Edge(temp, topLeft(x, y)));
		addToEdgeList(new Edge(temp, left(x, y)));
	}

	private static void addBottomRightCap(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, bottom(x, y)));
		addToEdgeList(new Edge(temp, bottomRight(x, y)));
		addToEdgeList(new Edge(temp, right(x, y)));
	}

	private static void addBottomLeftCap(Intersection temp, int x, int y) {
		addToEdgeList(new Edge(temp, left(x, y)));
		addToEdgeList(new Edge(temp, bottomLeft(x, y)));
		addToEdgeList(new Edge(temp, bottom(x, y)));
	}

	private static class Edge {
		Intersection a;
		Intersection b;

		double weight;

		public Edge(Intersection a, Intersection b) {

			this.a = a;
			this.b = b;

			if (a.getX() == b.getX() || a.getY() == b.getY()) {
				weight = 1;
			}

			else {
				weight = 1.414;
			}

		}

		public boolean equals(Object obj) {

			Edge otherEdge = (Edge) obj;

			if (otherEdge.a == this.a && otherEdge.b == this.b) {
				return true;
			}

			if (otherEdge.b == this.a && otherEdge.a == this.b) {
				return true;
			}

			else {
				return false;
			}
		}

		public boolean touches(Intersection c) {
			if (a == c || b == c) {
				return true;
			}

			else {
				return false;
			}
		}

	}

}
