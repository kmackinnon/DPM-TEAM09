package robot;

/**
 * BlockMover moves the styrofoam block to either the red or green zone. It
 * grabs the styrofoam block, travels to one of the zones, and stacks the block
 * if necessary.
 */

public class BlockMover extends MobileRobot {

	public BlockMover() {

	}

	/**
	 * Contains all the steps necessary for moving a block to a zone: grabbing,
	 * traveling to destination and releasing or stacking the block
	 */
	public void moveBlockToZone() {
		
		grabBlock();
		
		travelCoordinate(getPrevX(), getPrevY(), true);
		
		corr.turnOnCorrection();
		blockDetector.turnOnBlockDetection();
		
		travelToTargetZone();
		
		corr.turnOffCorrection();
		blockDetector.turnOffBlockDetection();
		
		getReadyForBlockRelease();

		if (isBuilder()) {
			stackBlock();
		}

		else {
			releaseBlock();
		}
		
		travelMagnitude(-12);
		
		travelCoordinate(getPrevX(), getPrevY(), true);
		
		return;
	}

	public void grabBlock() {
		corr.turnOffCorrection();
		blockDetector.turnOffBlockDetection();

		travelMagnitude(-16);
		
		dropClaw();
		
		travelMagnitude(13);

		liftClaw();

	}

	private void releaseBlock() {

		dropClaw();
		liftClaw();

	}

	private void stackBlock() {

		dropClaw();
		liftClaw();
		
	}
	
	private void getReadyForBlockRelease(){
		Intersection current = Map.getIntersection(odo.getX(),odo.getY());
		
		double xMax = -Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;
		double xMin = Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;
		
		for(Intersection intersection: Map.getTargetZone()){
			if(intersection.getX()>xMax){
				xMax = intersection.getX();
			}
			
			if(intersection.getY()>yMax){
				yMax = intersection.getY();
			}
			
			if(intersection.getX()<xMin){
				xMin = intersection.getX();
			}
			
			if(intersection.getY()<yMin){
				yMin = intersection.getY();
			}
		}
		
		if(current.getX() == xMin && current.getY() == yMin){
			turnToOnPoint(45);
		}
		
		else if(current.getX() == xMin && current.getY() == yMax){
			turnToOnPoint(135);
		}
		
		else if(current.getX() == xMax && current.getY() == yMax){
			turnToOnPoint(225);
		}
		
		else if(current.getX() == xMax && current.getY() == yMin){
			turnToOnPoint(315);
		}
		
		else if(current.getX() == xMin){
			turnToOnPoint(90);
		}
		
		else if(current.getX() == xMax){
			turnToOnPoint(270);
		}
		
		else if(current.getY() == yMin){
			turnToOnPoint(0);
		}
		
		else if(current.getY() == yMax){
			turnToOnPoint(180);
		}
	}

	public boolean pickUpStyrofoamBlock() {
		return false;
	}

}
