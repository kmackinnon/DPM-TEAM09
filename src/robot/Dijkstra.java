package robot;

import java.util.ArrayList;

import robot.Map.Edge;

public class Dijkstra {
	
	
	public static ArrayList<Intersection> algorithm(Intersection source, Intersection destination){
		
		computePaths(source);
		return getShortestPathTo(destination);
		
	}
	

	private static void computePaths(Intersection source) {

		source.setMinDistance(0);

		PriorityQueue intersectionQueue = new PriorityQueue();
		
		
		intersectionQueue.add(source);
		

		while (!intersectionQueue.isEmpty()) {
			
			if(intersectionQueue.queue.size()==1){
				break;
			}
			
			Intersection current = intersectionQueue.poll();

			for (Edge edge : Map.getEdgeList()) {

				if (edge.touches(current)) {
					Intersection adjacent = edge
							.getAdjacentIntersection(current);
					double weight = edge.getWeight();

					double distanceThroughCurrent = current.getMinDistance() + weight;
					
					if(distanceThroughCurrent < adjacent.getMinDistance()){
						adjacent.setMinDistance(distanceThroughCurrent);
						adjacent.setPrevious(current);
						
						if(intersectionQueue.contains(adjacent)){
							intersectionQueue.swimUp(intersectionQueue.indexOf(adjacent));
						}
						
						else{
							intersectionQueue.add(adjacent);
						}
						
						
					}
					
				}

			}
		}

	}
	
	
	
	
	
	private static ArrayList<Intersection> getShortestPathTo(Intersection destination){
		
		ArrayList<Intersection> reversePath = new ArrayList<Intersection>();
		Intersection temp;
		
		for(temp = destination; temp != null; temp = temp.getPrevious()){
			
			reversePath.add(temp);
			
		}
		
		
		ArrayList<Intersection> correctPath = new ArrayList<Intersection>();
		
		
		for(int i = reversePath.size()-1; i>=0; i--){
			temp = reversePath.get(i);
			
			correctPath.add(temp);	
		}
		
		return correctPath;
			
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
		
		private int indexOf(Intersection intersection){
			return queue.indexOf(intersection);
		}
		
		private boolean contains(Intersection intersection){
			return queue.contains(intersection);
		}

	}

}
