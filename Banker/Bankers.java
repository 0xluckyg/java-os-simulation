import java.util.*;

public class Bankers {
	private int cycle = 0;
	private int terminatedCount = 0;
	private  ArrayList<Task> tasks;
	private ArrayList<Release> releases;
	private HashMap<Integer, Resource> resources;	
	
	public Bankers() {
		this.tasks = new ArrayList<Task>();
		this.releases = new ArrayList<Release>();
		this.resources = new HashMap<Integer, Resource>();		
	}
	
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
	
	public void addResource(int number, Resource resource) {
		this.resources.put(number, resource);
	}
	
	//Checking for safety
	public boolean checkSafety(Task myTask, Command command) {
		
		if (myTask.hasResource(command.resourceType) && myTask.getHas(command.resourceType) > 0) {			
			return true;
		}			
		
		//Loop through each task, see if any task already has resources
		for (Task task : this.tasks) {
			//Don't check the task that's checking or tasks that are termianted
			if (task.getTaskNumber() == myTask.getTaskNumber()) continue;
			if (task.isTerminated()) continue;
						
			//For each resource, check resource it needs, its claims, and how many resources it has for each type
			for (Map.Entry<Integer, Integer> has: task.getHasMap().entrySet()) {
				if (has.getValue() == 0) continue;				
				if (has.getKey() == command.resourceType) {					
					int leftOver = this.resources.get(command.resourceType).getHas() - command.amount;			
					int taskNeeds = task.getClaim(command.resourceType) - task.getHas(command.resourceType);					
					if (leftOver < taskNeeds) return false;
					continue;
				}
				//for resources other than the resource type that we're checking, return unsafe if the task holds any
				if (has.getValue() > 0) {
					return false;
				}
			}
		}
		return true;
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
					//If the claim amount is less than the command amount, return an error message
					if (command.amount > this.resources.get(command.resourceType).getMax()) {
						String errorMessage = "Banker aborts task " + task.getTaskNumber() + " before run begins:\n" 
											+ "       claim for resourse " + command.resourceType + "(" + command.amount + ")" 
											+ "exceeds number of units present";

						System.out.println(errorMessage);
						this.terminatedCount++;
						task.abort();
					}
					
					//Otherwise, set claim
					task.setClaim(command.resourceType, command.amount);
					task.deleteCommand();
					continue;
				}								
								
				if (command.type.equals("request")) {						
															
					Resource resource = this.resources.get(command.resourceType);					
					
					//If resource doesn't exist, make the task wait, and increment block count										
					if (resource.getHas() < command.amount) {																		
						task.incrementPriority();
						continue;
					}
					
					//Check safety before moving on to avoid deadlock
					if (!this.checkSafety(task, command)) {
						task.incrementPriority();
						continue;
					}
					
					//Check for how many resources there are available
					int request = 0;
					if (task.hasResource(command.resourceType)) {
						request = task.getHas(command.resourceType) + command.amount;						
					} else {
						request = command.amount;
					}
					
					//If the demand is more than the amount of resources the system has, abort the task
					if (resource.getMax() < request || task.getClaim(command.resourceType) < request) {
						int units = 0;
						for (Map.Entry<Integer, Integer> has: task.getHasMap().entrySet()) {
							//Release the resources
							units += has.getValue();
							this.resources.get(has.getKey()).incrementHas(has.getValue());												
						}
						
						//Throw an appropriate error message
						String errorMessage = "During cycle " + this.cycle + "-" + (this.cycle + 1) + " of Banker's algorithms\n" 
								+ "       Task " + task.getTaskNumber() + "'s request exceeds its claim; aborted; " 
								+ units + " units available next cycle";
						
						System.out.println(errorMessage);
						
						this.terminatedCount++;
						task.abort();
						continue;
					}
					
					//If possible to allocate					
					resource.decrementHas(command.amount);
					task.incrementHas(command.resourceType, command.amount);					
					task.deleteCommand();	
					//Reset waiting count so that the task now goes to the back of the list
					task.setPriority(0);	
					
					
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
		double totalRun = 0.0;
		double totalBlocked = 0.0;
		
		System.out.println("                BANKERS");
		
		//Loop through and get summary for each task
		for (int i = 0; i < this.tasks.size(); i++) {			
			Task task = this.getTask(i+1);
			totalRun += task.getTerminatedTime();
			totalBlocked += task.getWaitingTime();			
			
			String taskSummary = task.getSummary();						
			System.out.println(taskSummary);			
		}	
		
		//Print out the whole summary
		int totalPercentWait = (int) Math.round(totalBlocked / totalRun * 100);
		String totalSummary = "       total        " + (int) totalRun + "   " + (int) totalBlocked + "   " + totalPercentWait + "%\n";
		System.out.println(totalSummary);
	}
}