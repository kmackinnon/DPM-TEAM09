package robot;
/**
 * BlockMover moves the styrofoam block to either the red or green zone. It
 * grabs the styrofoam block, travels to one of the zones, and stacks the block
 * if necessary.
 */

public class BlockMover extends MobileRobot {

	public BlockMover(){
		
	}
	
	
	/**Contains all the steps necessary for moving a block to a zone: grabbing, traveling to destination and releasing or stacking the block*/
	public void moveBlockToZone(){
		
		grabBlock();
/*		
		if(Map.targetZone == Map.greenZone){
			stackBlock();
		}
		
		else{
			releaseBlock();
		}
		*/
	}
	

	private void grabBlock(){
		
	}
	
	private void releaseBlock(){
		
	}
	
	private void stackBlock(){
		
	}
	

	
	

	
	
	
}
