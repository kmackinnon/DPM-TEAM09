package robot;



/**
 * Explorer looks for styrofoam blocks. As soon as it finds a styrofoam block,
 * it takes a rest. It does not move styrofoam blocks or lift them.
 */

public class Explorer extends MobileRobot {

	int rowNumber;
	int columnNumber;
	int rowCounter;

	public Explorer() {

	}

	/**
	 * This contains the searching algorithm
	 */
	public void lookForStyrofoamBlocks() {
		
		liftClaw();
		
		corr.turnOnCorrection();
		blockDetector.turnOnBlockDetection();


		
		if (getXStart() == getYStart()) {
			rowNumber = getYStart();
		}

		else {
			rowNumber = getXStart();
		}

		rowCounter = 0;

		for (int initialRow = rowNumber; loopCondition(rowNumber, initialRow); loopAfterthought(initialRow)) {
			
			travelTo(endOfCurrentRow());
			
			travelTo(nextRow());
		}

	}

	
	
	private Intersection endOfCurrentRow(){
		return getNextDestination(rowNumber);
	}
	
	private Intersection nextRow(){
		if(getXStart()==0 && getYStart()==0 && rowNumber!=Map.NUM_OF_INTERSECTIONS-1){
			return getNextDestination(rowNumber+1);
		}
		
		else if(getXStart()==0 && getYStart()== Map.NUM_OF_INTERSECTIONS-1 && rowNumber!=Map.NUM_OF_INTERSECTIONS-1){
			return getNextDestination(rowNumber+1);
		}
		
		else if(getXStart()==Map.NUM_OF_INTERSECTIONS-1 && getYStart()== Map.NUM_OF_INTERSECTIONS-1 && rowNumber!=0){
			return getNextDestination(rowNumber-1);
		}
		
		else if(getXStart()==Map.NUM_OF_INTERSECTIONS-1 && getYStart()== 0 && rowNumber!=0){
			return getNextDestination(rowNumber-1);
		}
		
		return Map.getIntersection(0,0);
	}
	
	
	
	private Intersection getValidIntersection(int tempRowNumber, int defaultDestination, boolean yIsRow){
	
		Intersection intersection;
		
		for(int i = 1; i<= Map.NUM_OF_INTERSECTIONS; i++){
			
			if(yIsRow){
				
				intersection = Map.getIntersection(Math.abs(defaultDestination-i),tempRowNumber);
				
			}
			
			else{
				
				intersection = Map.getIntersection(tempRowNumber,Math.abs(defaultDestination-i));
				
			}
			
			if(intersection!=null){
				return intersection;
			}
		
		}
		
		return Map.getIntersection(0,0);
	}
	
	
	private Intersection getNextDestination(int tempRowNumber){
		
		if(rowCounter%2 == 0){
			
			if(getXStart()==0 && getYStart()==0){
				return getValidIntersection(tempRowNumber,Map.NUM_OF_INTERSECTIONS,true);
			}
			
			else if(getXStart() == 0 && getYStart()==Map.NUM_OF_INTERSECTIONS-1){
				return getValidIntersection(tempRowNumber,1,false);
			}
			
			else if(getXStart() == Map.NUM_OF_INTERSECTIONS-1 && getYStart()==Map.NUM_OF_INTERSECTIONS-1){
				return getValidIntersection(tempRowNumber,1,true);
			}
			
			else if(getXStart() == Map.NUM_OF_INTERSECTIONS-1 && getYStart()==0){
				return getValidIntersection(tempRowNumber,Map.NUM_OF_INTERSECTIONS,false);
			}
			
		}
		
		else if(rowCounter%2 != 0){
				
			if(getXStart()==0 && getYStart()==0){
				return getValidIntersection(tempRowNumber,1,true);
			}
			
			else if(getXStart() == 0 && getYStart()==Map.NUM_OF_INTERSECTIONS-1){
				return getValidIntersection(tempRowNumber,Map.NUM_OF_INTERSECTIONS,false);
			}
			
			else if(getXStart() == Map.NUM_OF_INTERSECTIONS-1 && getYStart()==Map.NUM_OF_INTERSECTIONS-1){
				return getValidIntersection(tempRowNumber,Map.NUM_OF_INTERSECTIONS,true);
			}
			
			else if(getXStart() == Map.NUM_OF_INTERSECTIONS-1 && getYStart()==0){
				return getValidIntersection(tempRowNumber,1,false);
			}
			
		}
		
		return Map.getIntersection(0,0);
		
	}
	
	
	
	private boolean loopCondition(int currentPosition, int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			return currentPosition < Map.NUM_OF_INTERSECTIONS;
		} else {
			return currentPosition >= 0;
		}
	}

	private void loopAfterthought(int startPosition) {

		if (startPosition < (Map.NUM_OF_INTERSECTIONS - 1)) {
			rowNumber++;
		} else {
			rowNumber--;
		}

		rowCounter++;

	}
	
	public void styrofoamBlockDecision(){
		
		
	}


}
