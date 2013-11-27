package robot;

import java.util.ArrayList;

/**
 * Dijkstra contains Dijkstra's algorithm. The code is heavily based on this
 * webpage: http://en.literateprograms.org/Dijkstra's_algorithm_(Java).
 * 
 * Java has its own Priority Queue. However, it is not included in LeJOS, so I
 * wrote a PriorityQueue class. I found this powerpoint useful while making the
 * class: http://www.cs.princeton.edu/~rs/AlgsDS07/06PriorityQueues.pdf
 */

public class Dijkstra {

	static int counter = 0;

	/**
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

	private static double getEdgeWeight(Intersection a, Intersection b) {

		if (a.getX() == b.getX() || a.getY() == b.getY()) {
			return 1;
		}

		else {
			return 1.414;
		}

	}

	private static class PriorityQueue {

		private ArrayList<Intersection> queue;

		public PriorityQueue() {
			queue = new ArrayList<Intersection>();
			queue.add(null);
		}

		public Intersection poll() {
			Intersection highestPriority = queue.get(1);

			exchange(1, queue.size() - 1);

			queue.remove(queue.size() - 1);

			sinkDown(1);

			return highestPriority;

		}

		private void add(Intersection intersection) {

			queue.add(intersection);

			swimUp(queue.size() - 1);

		}

		private void swimUp(int k) {

			while (k > 1 && firstLowerThanSecond(k / 2, k)) {
				exchange(k / 2, k);

				k = k / 2;
			}
		}

		private boolean firstLowerThanSecond(int first, int second) {

			if (queue.get(second).getMinDistance() < queue.get(first)
					.getMinDistance()) {
				return true;
			}

			else {
				return false;
			}

		}

		private void exchange(int parent, int child) {

			Intersection parentIntersection = queue.get(parent);

			queue.set(parent, queue.get(child));
			queue.set(child, parentIntersection);

		}

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

		private boolean isEmpty() {
			return queue.isEmpty();
		}

		private int indexOf(Intersection intersection) {
			return queue.indexOf(intersection);
		}

		private boolean contains(Intersection intersection) {
			return queue.contains(intersection);
		}

	}

}