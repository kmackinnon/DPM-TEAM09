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

public class AStar {

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
		
		computePaths(source,destination);
		
		Map.resetClosedOpenStatus();
		
		return getShortestPathTo(destination);

	}

	public static ArrayList<Intersection> algorithmForTargetZone(
			Intersection source) {

		Intersection closestOne = Map.getTargetZone().get(0);
		double minimumDistance = closestOne.getMinDistance();
		

		for (Intersection destination : Map.getTargetZone()) {

			computePaths(source,destination);
			
			if (destination.getMinDistance() < minimumDistance) {
				closestOne = destination;
				minimumDistance = closestOne.getMinDistance();
			}
			
			Map.resetClosedOpenStatus();

		}

		return getShortestPathTo(closestOne);

	}

	private static void computePaths(Intersection input, Intersection input2) {

		Intersection source = Map.getIntersection(input);
		Intersection destination = Map.getIntersection(input2);

		source.setMinDistance(0);

		PriorityQueue intersectionQueue = new PriorityQueue();

		intersectionQueue.add(source);
		source.setIsOpen(true);

		while ((intersectionQueue.queue.size() != 1) && (intersectionQueue.get(1)!=destination)) {

			Intersection current = intersectionQueue.poll();
			
			current.setIsClosed(true);
			
			for (Intersection adjacent : current.getAdjacencyList()) {

				adjacent = Map.getIntersection(adjacent);

				if (adjacent != null) {

					double weight = getEdgeWeight(current, adjacent);

					double distanceThroughCurrent = current.getMinDistance()
							+ weight;
					
					if(adjacent.getIsOpen()&&(distanceThroughCurrent < adjacent.getMinDistance())){	
	
						intersectionQueue.remove(adjacent);
						adjacent.setIsOpen(false);
					}
					
					if(adjacent.getIsClosed() && (distanceThroughCurrent < adjacent.getMinDistance())){
						adjacent.setIsClosed(false);
					}
					
					if((!adjacent.getIsOpen()) && (!adjacent.getIsClosed())){
						adjacent.setMinDistance(distanceThroughCurrent);
						adjacent.setHeuristicDistance(heuristic(adjacent,destination));
						adjacent.setPrevious(current);
						intersectionQueue.add(adjacent);
						adjacent.setIsOpen(true);
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

	private static double heuristic(Intersection a, Intersection b) {

		double dx = Math.abs(a.getX() - b.getX());
		double dy = Math.abs(a.getY() - b.getY());

		return (dx + dy) + ((1.414 - 2) * Math.min(dx, dy));

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

			if ((queue.get(second).getMinDistance() + queue.get(second)
					.getHeuristicDistance()) < (queue.get(first)
					.getMinDistance() + queue.get(first).getHeuristicDistance())) {
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

		public void remove(Intersection a){
			
			int i = queue.indexOf(a);
			int size = size();

			if(i==size-1){
				queue.remove(i);
			}
			
			else{
				exchange(i,size - 1);
				queue.remove(size - 1);
				swimUp(i);
				sinkDown(i);
			}
			
		}
		
		private int size(){
			return queue.size();
		}
		
		private Intersection get(int i){
			return queue.get(i);
		}

	}

}