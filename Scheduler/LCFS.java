import java.util.*;

public class LCFS {

	private boolean isVerbose;
	private Processes processes;
    Stack<Process> readyStack = new Stack<Process>();
    
    private int clock = 0;
	
	public LCFS(Processes processes, boolean isVerbose) {
		this.isVerbose = isVerbose;
		this.processes = processes;		
	}		
	
	public void run() {
		
        this.processes.setHeader();
                
        if (isVerbose) {
            System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        }
        
        while(!this.processes.checkIfAllTerminated()) {        	        	        
        	
        	this.processes.setRandomNumberFromFile(0, false);
        	this.processes.setRunning(this.processes.checkIfAnyProcessIsRunning());
        	this.processes.checkBlocked();
        	
        	if (isVerbose) {        		
        		this.processes.printCycle(clock);
        	}        	
        	
        	
        	for (int i = this.processes.size() - 1; i >= 0; i--) {
        		Process process = this.processes.get(i);
        		if (!process.isTerminated()) {
        			
	        		if (process.getArrivalTime() <= clock) {
	        			
	        			if (process.checkIfBlockedTimeExists()) {               				
	        				process.setState("blocked");
	        				process.decrementBlockedTimeLeft();        				        			
	        				
	        				if (process.getCpuTimeLeft() < 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);	            				
	            			}
	        				
	        			} else if (process.checkIfRunningTimeExists()) {	        				
	            			process.setState("running");
	            			process.decrementRunningTimeLeft();
	            			
	            			if (process.getCpuTimeLeft() < 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);	            				
	            			}
	            			
	            			if (process.getRunningTimeLeft() == 0  && !process.isTerminated() && process.getCpuTimeLeft() != 0) {            				
	            				this.processes.setBlockedTimeLeft(process.getPid());
	            			}		            		
	            			
	        			} else {
	        					            			
	        				if (process.getCpuTimeLeft() <= 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);
	            				continue;
	            			}
	        				
	            			process.setState("ready");	            				            		
	            			if (!this.readyStack.contains(process)) {
	            				this.readyStack.add(process);
	            			}            
	        			}        			
	        		}
	        		
        		}
        	}
        	        	
        	if (!this.readyStack.isEmpty() && !this.processes.isRunning()) {
        		
        		Process process = this.readyStack.peek();
        		
        		this.readyStack.pop();        		
        		
        		process.setState("running");
        		this.processes.setRunningTimeLeft(process.getPid());
        		process.decrementRunningTimeLeft();

        		if (process.getCpuTimeLeft() < 0) {
    				process.terminate(clock);
    				this.processes.setRunning(false);
    			}
        		
       			if (process.getRunningTimeLeft() == 0 && !process.isTerminated() && process.getCpuTimeLeft() != 0) {            				
       				this.processes.setBlockedTimeLeft(process.getPid());
       			}        			   
       			
        		
        	}
        	        	
        	clock++;
        }
        
        System.out.println("The scheduling algorithm used was LastCome First Served\n");
        this.processes.setFooter(clock);
	}	
}
