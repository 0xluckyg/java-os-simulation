import java.util.*;

public class Machine {
	Scanner scanner;
	int numberOfFrames;
	int machineSize;
	int pageSize;
	int numberOfPages;
	String algorithm;
	ArrayList<Page> frames;
	
	public Machine(int machineSize, int pageSize, String algorithm, Scanner scanner) {
		this.machineSize = machineSize;
		this.pageSize = pageSize;
		this.algorithm = algorithm;
		this.numberOfPages = 0;
		this.initializeFrames();
		this.scanner = scanner;
	}
	
	//Fill all the frames will null
	public void initializeFrames() {
		this.numberOfFrames = machineSize / pageSize;
		this.frames = new ArrayList<Page>(numberOfFrames);
		for (int i = 0; i < numberOfFrames; i++) {
			this.frames.add(i, null);
		}
	}
	
	//Check if it's a hit or miss
	public boolean refer(int word, Process process, int time) {
		int pageId = word / this.pageSize;		
		boolean hit = false;
		//Loop through, if process id and page id matches, it's a hit
		for (int i = 0; i < this.frames.size(); i ++) {
			Page page = this.frames.get(i);
			if (page == null) continue;
			if (page.process.pid == process.pid && pageId == page.pageId) {
				hit = true;
				page.lastUpdated = time;				
				break;
			}
		}
		
		//return the hit or miss value
		return hit;
	}
	
	//If it's not hit, addPage is called.
	public int addPage(int word, int timeEntered, Process process) {		
		int pageId = word / this.pageSize;				
		
		int frameNum;
		//If the array is full, then evict
		if (this.numberOfPages >= this.frames.size()) {			
			frameNum = this.evict(timeEntered);
			Page page =  new Page(pageId, timeEntered, timeEntered, process, frameNum);
			frames.set(frameNum, page);
			
			if (page.loadTime == -1) page.loadTime = timeEntered;
			
		//If array is not full, just add the page
		} else {			
			Page page =  new Page(pageId, timeEntered, timeEntered, process, this.numberOfPages);
			frames.set(this.numberOfPages, page);
			frameNum = this.numberOfPages;
			if (page.loadTime == -1) page.loadTime = timeEntered;			
		}
		
		//Increment the number of pages since we cannot track it with array size (array is full of nulls)
		this.numberOfPages++;
		frameNum = this.numberOfFrames - frameNum - 1;
		return frameNum;
	}
	
	//Function for evicting a page
	public int evict(int time) {		
		//Make copy of the frames to sort according to the algorithm
		ArrayList<Page> sortList = new ArrayList<Page>(this.frames);
		
		//FIFO: sort using time entered
		if (this.algorithm.equals("fifo")) {
			Collections.sort(sortList, new Comparator<Page>() {
			    @Override
			    public int compare(Page p1, Page p2) {
			    	if (p2 ==  null || p1 == null) return -1;
			        return p1.timeEntered - p2.timeEntered;
			    }
			});
			
			int frame = sortList.get(0).frame;
			Page page = this.frames.get(frame);
			
			//Add the residency time
			page.process.incrementResidency(time - page.loadTime);
			page.process.incrementEvictions();
			page.loadTime = -1;
			
			this.frames.set(frame, null);
			return frame;
		}
		//LRU: sort using time updated
		if (this.algorithm.equals("lru")) {
			Collections.sort(sortList, new Comparator<Page>() {
			    @Override
			    public int compare(Page p1, Page p2) {			    	
			    	if (p2 ==  null || p1 == null) return -1; 
			        return p1.lastUpdated - p2.lastUpdated;
			    }
			});
						
			int frame = sortList.get(0).frame;
			Page page = this.frames.get(frame);
			page.process.incrementResidency(time - page.loadTime);
			page.process.incrementEvictions();
			page.loadTime = -1;			
			
			this.frames.set(frame, null);
			return frame;
		}
		//RANDOM: get random number from the file
		if (this.algorithm.equals("random")) {
			int randomNumber = this.scanner.nextInt();			
			int r = this.numberOfFrames - (randomNumber % this.numberOfFrames) - 1;
			
			Page page = this.frames.get(r);
			page.process.incrementResidency(time - page.loadTime);			
			page.process.incrementEvictions();
			page.loadTime = -1;			
			
			this.frames.set(r, null);
			return r;
		}		
		return 0;
	}
}

//Page object. Keeps track of pid, process it belongs to, time entered, last updated, etc
class Page {	
	int pageId;
	int timeEntered = 0;
	int lastUpdated = 0;
	int frame = 0;
	int loadTime = -1;
	Process process = null;	
	
	public Page(int pageId, int timeEntered, int lastUpdated, Process process, int frameNum) {
		this.pageId = pageId;		
		this.timeEntered = timeEntered;
		this.lastUpdated = lastUpdated;
		this.process = process;
		this.frame = frameNum;
	}
	
	public void putProcess(Process process) {
		this.process = process;
	}
	
	public void evictProcess() {
		this.process = null;
	}
}