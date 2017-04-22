import java.util.*;

public class FIFO {
	private int cycle = 0;
	private int terminatedCount = 0;
	private int blockedCount = 0;	
	private ArrayList<Task> tasks;
	private ArrayList<Release> releases;
	private HashMap<Integer, Resource> resources;	
	
	public FIFO() {
		this.resources = new HashMap<Integer, Resource>();
		this.releases = new ArrayList<Release>();
		this.tasks = new ArrayList<Task>();
	}
	
	//Adds task to the system
	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	//Gets task according to rank, instead of priority
	public Task getTask(int number) {
		for (Task task : tasks) {			
			if (task != null && task.getTaskNumber() == number) {
				return task;
			}
		}
		return null;
	}
	
	//Adds resource to the system from init
	public void addResource(int number, Resource resource) {
		this.resources.put(number, resource);
	}
	
	public void checkDeadlock() {	
		if (this.blockedCount == this.tasks.size() - this.terminatedCount) {
			//For each task from lowest to greatest in rank, abort until safe again
			for (int i = 1; i <= this.tasks.size(); i++) {
				Task task = this.getTask(i);				
				if (!task.isTerminated()) {						
					for (Map.Entry<Integer, Integer> has: task.getHasMap().entrySet()) {
						//Release the resources																	
						this.resources.get(has.getKey()).incrementHas(has.getValue());						
						this.blockedCount--;						
					}										
					task.abort();
					this.terminatedCount++;																					
				}
				
				//Check if other requests can be completed with the resource released. If not, continue aborting cycle
				boolean safe = false;
				for (Task check : tasks) {	
					Command command = check.getCommand();							
					if (command.type.equals("request")) {												
						Resource resource = this.resources.get(command.resourceType);				
						if (resource.getHas() >= command.amount) {
							safe = true; break;							
						}																
					}
				}
				//If cycle can be continued with released resource, break.
				if (safe) break;
			}
		} 			
	}
	
	public void run() {
		while (terminatedCount < tasks.size()) {
			
			//Check for releases from the previous cycle first to make resources available
			//We release resources from previous cycle
			for (Release release : this.releases) {
				Resource resource = this.resources.get(release.resourceType);					
				resource.incrementHas(release.amount);
				release.task.decrementHas(release.resourceType, release.amount);
			}
			this.releases.clear();
			
			//Check for deadlock from before the cycle
			this.checkDeadlock();
						
			for (Task task : tasks) {								
				//Don't deal with tasks that are aborted / terminated
				if (task.isTerminated() || task.isAborted()) {					
					continue;
				}								
				Command command = task.getCommand();							
				
				//if delay exists, don't run, and decrement the delay
				if (command.delay > 0) {
					
					command.delay--;
					continue;
				}
				
				//Initialize, set claim (which we don't need for FIFO), and delete initiate command
				if (command.type.equals("initiate")) {						
					task.setClaim(command.resourceType, command.amount);
					task.deleteCommand();
					continue;
				}								
								
				if (command.type.equals("request")) {						
					
					//If resource exists, give
					Resource resource = this.resources.get(command.resourceType);				
					if (resource.getHas() >= command.amount) {						
						resource.decrementHas(command.amount);
						task.incrementHas(command.resourceType, command.amount);					
						task.deleteCommand();	
						//Reset waiting count so that the task now goes to the back of the list
						task.setPriority(0);
						
						if (blockedCount > 0) {
							this.blockedCount--;
						}
						
					//If resource doesn't exist, make the task wait, and increment block count
					} else {												
						task.incrementPriority();						
						if (task.getPriority() == 1) {
							this.blockedCount++;
						}																							
					}										
					continue;
				}				
				
				//Release all the resources and give them back to the according keys in resource map
				if (command.type.equals("release")) {					
					Release release = new Release(command.resourceType, command.amount, task);
					releases.add(release);
					
					task.deleteCommand();
					continue;
				}
				
				//If terminate, set terminate to true (which sets terminate to the cycle in which task ended)
				if (command.type.equals("terminate")) {							
					this.terminatedCount++;
					task.terminate(cycle);				
				}									
			}														
			
			//Sort the collection every loop so that the task with highest wait count will go first
			Collections.sort(tasks, new CompareTasks());
			cycle++;
		}		
		
		//Print the statements
		this.printStatements();
	}
	
	public void printStatements() {
		//Gets the string from the task summary
		int totalRun = 0;
		int totalBlocked = 0;
		
		System.out.println("                FIFO");
		
		//Loop through the tasks in order and get summary
		for (int i = 0; i < this.tasks.size(); i++) {			
			Task task = this.getTask(i+1);
			totalRun += task.getTerminatedTime();
			totalBlocked += task.getWaitingTime();			
			
			String taskSummary = task.getSummary();						
			System.out.println(taskSummary);			
		}		
		
		//Print out the whole summary
		int totalPercentWait = (int) Math.round(totalBlocked / totalRun * 100);
		String totalSummary = "       total        " + totalRun + "   " + totalBlocked + "   " + totalPercentWait + "%\n";
		System.out.println(totalSummary);
	}
}