import java.util.LinkedList;
import java.util.Queue;

public class RR {

	private boolean isVerbose;
	private Processes processes;
    Queue<Process> readyQueue = new LinkedList<Process>();
    
    private int clock = 0;
	
	public RR(Processes processes, boolean isVerbose) {
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
        	this.processes.setRunning(this.processes.checkIfAnyProcessHasQuantum());
        	this.processes.checkBlocked();
        	
        	if (isVerbose) {        		
        		this.processes.printCycle(clock);
        	}        	
        	
        	
        	for (Process process : this.processes) {        		        		
        		if (!process.isTerminated()) {
        			
	        		if (process.getArrivalTime() <= clock) {
	        			
	        			if (process.checkIfBlockedTimeExists()) {               				
	        				process.setState("blocked");
	        				process.decrementBlockedTimeLeft();        				        			
	        				
	        				if (process.getCpuTimeLeft() < 0) {
	            				process.terminate(clock);	            				
	            				this.processes.setRunning(false);	            				
	            			}
	        				
	        			} else if (process.getQuantum() > 0 && process.checkIfRunningTimeExists()) {	        				
	            			process.setState("running");

	            			process.decrementRunningTimeLeft();	            			
	            			process.decrementQuantum();
	            			
	            			if (process.getCpuTimeLeft() < 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);	            				
	            			}	            				            		
	            			
	            			if (process.getRunningTimeLeft() == 0 && !process.isTerminated() && process.getCpuTimeLeft() != 0) {            				
	            				this.processes.setBlockedTimeLeft(process.getPid());
	            				process.resetQuantum();
	            			}		            		
	            			
	        			} else {
	        					            			
	        				if (process.getCpuTimeLeft() <= 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);
	            				continue;
	            			}
	        				
	            			process.setState("ready");	            				            		
	            			if (!this.readyQueue.contains(process)) {
	            				this.readyQueue.add(process);
	            			}            
	        			}        			
	        		}
	        		
        		}        		
        	}
        	
        	if (!this.readyQueue.isEmpty() && !this.processes.isRunning()) {
        		
        		Process process = this.readyQueue.peek();        		
        		this.readyQueue.poll();        		
        		
        		process.setState("running");
        		process.setQuantum();
        		process.decrementQuantum();
        		if (!process.checkIfRunningTimeExists()) {
        			this.processes.setRunningTimeLeft(process.getPid());
        		}         		
        		process.decrementRunningTimeLeft();

        		if (process.getCpuTimeLeft() < 0) {
    				process.terminate(clock);
    				this.processes.setRunning(false);
    			}
        		
       			if ((process.getRunningTimeLeft() == 0 || process.getQuantum() == 0)
       					&& !process.isTerminated() && process.getCpuTimeLeft() != 0) {            				
       				this.processes.setBlockedTimeLeft(process.getPid());
    				process.resetQuantum();
       			}        			   
        	}
        	        	
        	clock++;
        }
        
        System.out.println("The scheduling algorithm used was Round Robin\n");
        this.processes.setFooter(clock);
	}	
}
