
public class Competitor {

	public Localizer localizer = new Localizer();
	public Explorer explorer = new Explorer();
	public BlockMover blockMover = new BlockMover();
	
	public Competitor(){
		
	}
	
	
	
	public void play(){
		
		localizer.localize();
		
		explorer.lookForStyrofoamBlocks();
		
	}
	
	
	
}
