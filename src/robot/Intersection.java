package robot;

import java.util.ArrayList;

/**
 * Contains information about an intersection.
 * 
 * @author Kevin Musgrave
 * 
 */

public class Intersection {

	/**
	 * The x tile coordinate of the intersection.
	 */
	private int x;

	/**
	 * The y tile coordinate of the intersection.
	 */
	private int y;

	/**
	 * true if the intersection is part of the target zone.
	 */
	private boolean isTarget;

	/**
	 * The previous intersection that leads to this intersection in the shortest
	 * path. Used only by AStar and Dijkstra.
	 */
	private Intersection previous;

	/**
	 * The minimum distance found for this intersection. Used only by AStar and
	 * Dijkstra.
	 */
	private double minDistance = Double.POSITIVE_INFINITY;

	/**
	 * The estimated distance from this intersection to the destination. Used
	 * only by AStar and Dijkstra.
	 */
	private double heuristicDistance = Double.POSITIVE_INFINITY;

	/**
	 * true if in the closed set of AStar.
	 */
	private boolean isClosed = false;

	/**
	 * true if in the open set of AStar.
	 */
	private boolean isOpen = false;

	/**
	 * A list of intersections that this intersection has paths to.
	 */
	private ArrayList<Intersection> adjacencyList = new ArrayList<Intersection>();

	/**
	 * 
	 * @param x
	 *            the x tile coordinate of the intersection
	 * @param y
	 *            the y tile coordinate of the intersection
	 */
	public Intersection(int x, int y) {

		this.x = x;
		this.y = y;

		isTarget = false;

		previous = null;

	}

	/**
	 * removes this intersection from both the open and closed set of AStar
	 */
	public void resetClosedOpenStatus() {
		isClosed = false;
		isOpen = false;
	}

	/**
	 * 
	 * @param closed
	 *            pass in true if the intersection is part of the closed set of
	 *            AStar.
	 */
	public void setIsClosed(boolean closed) {

		if (closed) {
			isClosed = true;
			isOpen = false;
		}

		else {
			isClosed = false;
		}

	}

	/**
	 * 
	 * @param open
	 *            pass in true if the intersection is part of the open set of
	 *            AStar.
	 */
	public void setIsOpen(boolean open) {

		if (open) {
			isOpen = true;
			isClosed = false;
		}

		else {
			isOpen = false;
		}
	}

	/**
	 * 
	 * @return true if the intersection is part of the open set of AStar
	 */
	public boolean getIsOpen() {
		return isOpen;
	}

	/**
	 * 
	 * @return true if the intersection is part of the closed set of AStar
	 */
	public boolean getIsClosed() {
		return isClosed;
	}

	/**
	 * Adds an intersection to the adjacency list. This means there is a path
	 * between this intersection and the input intersection.
	 * 
	 * @param intersection
	 *            the intersection to be added
	 */
	public void addToAdjacencyList(Intersection intersection) {
		adjacencyList.add(intersection);
	}

	/**
	 * Removes an intersection from the adjacency list. This means there is no
	 * path between this intersection and the input intersection.
	 * 
	 * @param intersection
	 *            the intersection to be removed
	 */
	public void removeFromAdjacencyList(Intersection intersection) {
		adjacencyList.remove(intersection);
	}

	/**
	 * 
	 * @return the adjacency list of this intersection
	 */
	public ArrayList<Intersection> getAdjacencyList() {
		return adjacencyList;
	}

	/**
	 * @param intersection
	 *            the previous intersection on the shortest path to this
	 *            intersection
	 */
	public void setPrevious(Intersection intersection) {
		previous = intersection;
	}

	/**
	 * 
	 * @return the previous intersection on the shortest path to this
	 *         intersection
	 */
	public Intersection getPrevious() {
		return previous;
	}

	/**
	 * 
	 * @param min
	 *            the minimum distance to this intersection from the source
	 *            intersection
	 */
	public void setMinDistance(double min) {
		minDistance = min;
	}

	/**
	 * 
	 * @return the minimum distance to this intersection from the source
	 *         intersection
	 */
	public double getMinDistance() {
		return minDistance;
	}

	/**
	 * 
	 * @param h
	 *            the estimated distance from this intersection to the
	 *            destination
	 */
	public void setHeuristicDistance(double h) {
		heuristicDistance = h;
	}

	/**
	 * 
	 * @return the estimated distance from this intersection to the destination
	 */
	public double getHeuristicDistance() {
		return heuristicDistance;
	}

	public void setAsTarget() {
		isTarget = true;
	}

	public boolean isTarget() {
		return isTarget;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * Centimeter coordinates are needed since this is how the odometer measures
	 * the robot's position
	 * 
	 * @return the x coordinate in centimeters.
	 */
	public double getXInCm() {
		return x * Map.TILE_SIZE;
	}

	/**
	 * Centimeter coordinates are needed since this is how the odometer measures
	 * the robot's position
	 * 
	 * @return the y coordinate in centimeters.
	 */
	public double getYInCm() {
		return y * Map.TILE_SIZE;
	}

}