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
	 * Initializes the hashtable of intersections. The corner intersections are
	 * marked as "forbidden" because the robot is not allowed to enter the
	 * corner tiles
	 */
	public static void initializeMap() {

		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
				intersectionList.add(new Intersection(x,y));
			}
		}
		
		for (int y = 0; y < NUM_OF_INTERSECTIONS; y++) {
			for (int x = 0; x < NUM_OF_INTERSECTIONS; x++) {
	
			Intersection temp = get(index(x,y));	
			
			addToEdgeList(new Edge(temp, get(index(x+1,y))));
			addToEdgeList(new Edge(temp, get(index(x+1,y+1))));
			addToEdgeList(new Edge(temp, get(index(x,y+1))));
			addToEdgeList(new Edge(temp, get(index(x-1,y+1))));
			addToEdgeList(new Edge(temp, get(index(x-1,y))));
			addToEdgeList(new Edge(temp, get(index(x-1,y-1))));
			addToEdgeList(new Edge(temp, get(index(x,y-1))));
			addToEdgeList(new Edge(temp, get(index(x+1,y-1))));
		
			}
			
		}
		
		
	}
	
	

/*	*//**
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
	 *//*
	public static void setForbiddenZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		int[] topRightKey = findKeyFromCoordinates(topRightCorner);
		int[] bottomLeftKey = findKeyFromCoordinates(bottomLeftCorner);

		for (int x = bottomLeftKey[0]; x <= topRightKey[0]; x++) {
			for (int y = bottomLeftKey[1]; y <= topRightKey[1]; y++) {
				intersectionList.get(key(x, y)).setAsForbidden();
			}
		}

	}

	
	*//**
	 * Assuming the robot is a builder, this sets all intersections in and on
	 * the boundary of the greenZone as "target". (The red zone would be
	 * the target for a garbage collector)
	 * 
	 * @param topRightCorner
	 *            the xy coordinates of the top right corner of the target
	 *            zone.
	 * 
	 * @param bottomLeftCorner
	 *            the xy coordinates of the bottom left corner of the target
	 *            zone
	 * 
	 *//*
	public static void setTargetZone(Coordinates topRightCorner,
			Coordinates bottomLeftCorner) {

		int[] topRightKey = findKeyFromCoordinates(topRightCorner);
		int[] bottomLeftKey = findKeyFromCoordinates(bottomLeftCorner);

		for (int x = bottomLeftKey[0]; x <= topRightKey[0]; x++) {
			for (int y = bottomLeftKey[1]; y <= topRightKey[1]; y++) {
				intersectionList.get(key(x, y)).setAsTarget();
			}
		}

	}

	
	private static int[] findKeyFromCoordinates(Coordinates input) {

		int xKey = (int) Math.round(input.getX() / TILE_SIZE);
		int yKey = (int) Math.round(input.getY() / TILE_SIZE);

		return key(xKey, yKey);
	}*/
	
	

	private static int index(int x, int y) {

		return (y*11 + x);
		
	}
	
	private static Intersection get(int index){
		
		return intersectionList.get(index);
		
	}
	
	
	private static void addToEdgeList(Edge edge){
		
		if(!edgeList.contains(edge)){
			edgeList.add(edge);
		}
		
	}
	
	private static void removeIntersection(int index){
		
		Intersection intersection = get(index);
		
		Iterator<Edge> it = edgeList.iterator();
		
		for(;it.hasNext();){
			if(it.next().touches(intersection)){
				it.remove();
			}
		}
		
		intersectionList.set(index, null);
		
	}
	
	
	
	private static class Edge{
		Intersection a;
		Intersection b;
		
		double weight;
		
		public Edge(Intersection a, Intersection b){
			
			this.a = a;
			this.b = b;
			
			if(a.getX()==b.getX()||a.getY()==b.getY()){
				weight = 1;
			}
			
			else{
				weight = 1.414;
			}
			
		}
		
		
		public boolean equals(Object obj){
			
			Edge otherEdge = (Edge) obj;
			
			if(otherEdge.a == this.a && otherEdge.b == this.b){
				return true;
			}
			
			if(otherEdge.b == this.a && otherEdge.a == this.b){
				return true;
			}
			
			else{
				return false;
			}
		}
		
		
		public boolean touches(Intersection c){
			if(a==c || b==c){
				return true;
			}
			
			else{
				return false;
			}
		}
			
	}
	
	
	

}
