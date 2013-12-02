package robot;

import java.util.ArrayList;

/**
 * Dijkstra contains Dijkstra's algorithm. We started off by using Dijkstra
 * only, but we switched to A* when we found out that it works faster in most
 * cases. We still use Dijkstra for finding the shortest path to the target zone
 * since it works faster than A* in this one case. The code is heavily based on
 * this webpage: http://en.literateprograms.org/Dijkstra's_algorithm_(Java).
 * 
 * Java has its own Priority Queue. However, it is not included in LeJOS, so I
 * wrote a PriorityQueue class. I found this powerpoint useful while making the
 * class: http://www.cs.princeton.edu/~rs/AlgsDS07/06PriorityQueues.pdf
 * 
 * @author Kevin Musgrave
 */

public class Dijkstra {

	static int counter = 0;

	/**
	 * Gets the shortest path between two intersections.
	 * 
	 * @param source
	 *            the intersection we want to find the shortest path FROM
	 * @param destination
	 *            the intersection we want to find the shortest path TO
	 * @return an arraylist of waypoints that comprise the shortest path
	 */
	public static ArrayList<Intersection> algorithm(Intersection source,
			Intersection destination) {

		computePaths(source);
		return getShortestPathTo(destination);

	}

	/**
	 * Gets the shortest path to the target zone. This is the only method that
	 * we actually ended up using.
	 * 
	 * @param source
	 *            the intersection we want to find the shortest path FROM
	 * @return an arraylist of waypoints that comprise the shortest path to the
	 *         target zone
	 */
	public static ArrayList<Intersection> algorithmForTargetZone(
			Intersection source) {

		computePaths(source);

		Intersection destination = Map.getTargetZone().get(0);

		double minimumDistance = destination.getMinDistance();

		for (Intersection intersection : Map.getTargetZone()) {

			if (intersection.getMinDistance() < minimumDistance) {
				destination = intersection;
				minimumDistance = intersection.getMinDistance();
			}

		}

		return getShortestPathTo(destination);

	}

	/**
	 * This is where all the Dijkstra computation happens.
	 * 
	 * @param input
	 *            the starting intersection
	 * @param input2
	 *            the destination intersection
	 */
	private static void computePaths(Intersection input) {

		Intersection source = Map.getIntersection(input);

		source.setMinDistance(0);

		PriorityQueue intersectionQueue = new PriorityQueue();

		intersectionQueue.add(source);

		while (!intersectionQueue.isEmpty()) {

			if (intersectionQueue.queue.size() == 1) {
				break;
			}

			Intersection current = intersectionQueue.poll();

			for (Intersection adjacent : current.getAdjacencyList()) {

				adjacent = Map.getIntersection(adjacent);

				if (adjacent != null) {

					double weight = getEdgeWeight(current, adjacent);

					double distanceThroughCurrent = current.getMinDistance()
							+ weight;

					if (distanceThroughCurrent < adjacent.getMinDistance()) {
						adjacent.setMinDistance(distanceThroughCurrent);
						adjacent.setPrevious(current);

						if (intersectionQueue.contains(adjacent)) {
							intersectionQueue.swimUp(intersectionQueue
									.indexOf(adjacent));
						}

						else {
							intersectionQueue.add(adjacent);
						}

					}

				}

			}
		}

	}

	/**
	 * After the paths are computed, this method forms an arraylist using the
	 * reference in each intersection that points to the preceding intersection
	 * that lies on the shortest path.
	 * 
	 * @param destination
	 *            the intersection we want to find the shortest path to
	 * @return an arraylist of waypoints that make up the shortest path.
	 */
	private static ArrayList<Intersection> getShortestPathTo(
			Intersection destination) {

		ArrayList<Intersection> reversePath = new ArrayList<Intersection>();
		Intersection temp = Map.getIntersection(destination);

		for (; temp.getPrevious() != null; temp = temp.getPrevious()) {

			reversePath.add(temp);

		}

		Map.resetAllPreviousAndDistance();

		ArrayList<Intersection> correctPath = new ArrayList<Intersection>();

		for (int i = reversePath.size() - 1; i >= 0; i--) {
			temp = reversePath.get(i);

			correctPath.add(temp);
		}

		return correctPath;

	}

	/**
	 * Calculates the weight of an edge (path) connecting two intersections.
	 * Straight edges have a weight of 1, while diagonal edges have weight of
	 * 1.414
	 * 
	 * @param a
	 *            the first intersection
	 * @param b
	 *            the second intersection
	 * @return the weight of the edge connection intersections "a" and "b"
	 */
	private static double getEdgeWeight(Intersection a, Intersection b) {

		if (a.getX() == b.getX() || a.getY() == b.getY()) {
			return 1;
		}

		else {
			return 1.414;
		}

	}

	/**
	 * Lejos does not have a PriorityQueue, so this class was made so that A*
	 * and Dijkstra could work. I decided to make this a nested class because
	 * there is no need for any other classes to see it.
	 * 
	 * 
	 * @author Kevin
	 * 
	 */
	private static class PriorityQueue {

		/**
		 * The representation of the priority queue
		 */
		private ArrayList<Intersection> queue;

		/**
		 * The constructor adds a null element to the beginning so that the swim
		 * and sink functions are easier to code.
		 */
		public PriorityQueue() {
			queue = new ArrayList<Intersection>();
			queue.add(null);
		}

		/**
		 * Removes the highest priority element from the queue.
		 * 
		 * @return the highest priority element
		 */
		public Intersection poll() {
			Intersection highestPriority = queue.get(1);

			exchange(1, queue.size() - 1);

			queue.remove(queue.size() - 1);

			sinkDown(1);

			return highestPriority;

		}

		/**
		 * Adds an intersection to the queue.
		 * 
		 * @param intersection
		 *            the intersection to add to the queue
		 */
		private void add(Intersection intersection) {

			queue.add(intersection);

			swimUp(queue.size() - 1);

		}

		/**
		 * Rearranges an element in the queue so that the elements are ordered
		 * as they should be in a priority queue
		 * 
		 * @param k
		 *            the element to be rearranged
		 */
		private void swimUp(int k) {

			while (k > 1 && firstLowerThanSecond(k / 2, k)) {
				exchange(k / 2, k);

				k = k / 2;
			}
		}

		/**
		 * Determines if an element has higher priority than another element.
		 * 
		 * @param first
		 *            the first element
		 * @param second
		 *            the second element
		 * @return true if "first" has lower priority than "second", false
		 *         otherwise
		 */
		private boolean firstLowerThanSecond(int first, int second) {

			if (queue.get(second).getMinDistance() < queue.get(first)
					.getMinDistance()) {
				return true;
			}

			else {
				return false;
			}

		}

		/**
		 * exchanges two elements in the priority queue
		 * 
		 * @param parent
		 *            this element changes place with "child"
		 * @param child
		 *            this element changes place with "parent"
		 */
		private void exchange(int parent, int child) {

			Intersection parentIntersection = queue.get(parent);

			queue.set(parent, queue.get(child));
			queue.set(child, parentIntersection);

		}

		/**
		 * Rearranges an element in the queue so that the elements are ordered
		 * as they should be in a priority queue
		 * 
		 * @param k
		 *            the element to be rearranged
		 */
		private void sinkDown(int k) {

			int N = queue.size() - 1;

			while (2 * k <= N) {
				int j = 2 * k;

				if (j < N && firstLowerThanSecond(j, j + 1)) {
					j++;
				}

				if (!firstLowerThanSecond(k, j)) {
					break;
				}

				exchange(k, j);

				k = j;
			}

		}

		/**
		 * 
		 * @return true if the priority queue is empty
		 */
		private boolean isEmpty() {
			return queue.isEmpty();
		}

		/**
		 * 
		 * @param intersection
		 *            the intersection that we want the index of
		 * @return the index of the intersection in the queue
		 */
		private int indexOf(Intersection intersection) {
			return queue.indexOf(intersection);
		}

		/**
		 * 
		 * @param intersection
		 *            the intersection we are looking for
		 * @return true if the queue contains the intersection
		 */
		private boolean contains(Intersection intersection) {
			return queue.contains(intersection);
		}

	}

}