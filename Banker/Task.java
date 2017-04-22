import java.util.*;

public class Task {
	private HashMap<Integer, Integer> claim;
	private HashMap<Integer, Integer> has;	
	private int taskNumber;
	private int priority;
	private double waitingTime = 0;
	double terminated = 0;
	boolean aborted = false;
	public LinkedList<Command> commands;
	
	//Task object keeps track of the tasks that fifo and bankers use
	public Task(int taskNumber) {
		commands = new LinkedList<Command>();
		this.taskNumber = taskNumber;
		this.priority = 0;
		this.claim = new HashMap<Integer, Integer>();						
		this.has= new HashMap<Integer, Integer>();		
	}
	
	//Helper funcitons are self explanatory, so I won't add detailed comments
	
	public void addCommand(String type, int delay, int resourceType, int amount) {
		Command command = new Command(type, delay, resourceType, amount);		
		commands.add(command);
	}
		
	public Command getCommand() {		
		return commands.getFirst();
	}
	
	public void deleteCommand() {
		commands.remove(0);		
	}
	
	public HashMap<Integer, Integer> getClaims() {
		return this.claim;
	}
	
	public int getClaim(int resource) {
		return claim.get(resource);
	}

	public void setClaim(int resource, int claim) {
		this.claim.put(resource, claim);
	}
	
	public HashMap<Integer, Integer> getHasMap() {
		return this.has;
	}

	public int getHas(int resource) {
		return this.has.get(resource);
	}
	
	public boolean hasResource(int resource) {
		if (this.has.containsKey(resource)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void incrementHas(int resource, int amount) {
		//First check if the resource is there to avoid null pointer error
		if (this.has.get(resource) == null) {
			this.has.put(resource, amount);
		} else {
			this.has.put(resource, this.has.get(resource) + amount);
		}		
	}
	
	public void decrementHas(int resource, int amount) {		
		this.has.put(resource, this.has.get(resource) - amount);
	}
	
	public void incrementPriority() {
		this.waitingTime++;
		this.priority++;
	}
	
	public void decrementPriority() {
		this.priority--;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return this.priority;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}
	
	public boolean isTerminated() {
		if (this.terminated > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void terminate(int cycle) {
		this.terminated = cycle;
	}
	
	public int getTerminatedTime() {
		return (int) this.terminated;
	}
	
	public int getWaitingTime() {
		return (int) this.waitingTime;
	}
	
	public boolean isAborted() {
		return this.aborted;
	}
	
	//Aborting terminates the task automatically
	public void abort() {
		this.terminated = 1;
		this.aborted = true;
	}
	
	//Prints out the summary of the task: total run time, blocked time, percentage blocked
	public String getSummary() {		
		String taskSummary = "       Task " + this.getTaskNumber() + "       ";
		int percentWait = (int) Math.round(this.waitingTime / this.terminated * 100);
		
		if (this.isAborted()) {
			taskSummary += "aborted";
		} else {
			taskSummary += (int) this.terminated + "   " + (int) this.waitingTime + "   " + percentWait + "%";
		}
		return taskSummary;
	}
	
}

//Command keeps track of the commands the task will run. ex) initiate, request, release, terminate
class Command {
	String type;	
	int delay;
	int resourceType;	
	int amount;
	
	Command(String type, int delay, int resourceType, int amount) {
		this.type = type;		
		this.delay = delay;
		this.resourceType = resourceType;
		this.amount = amount;
	}
}