import java.util.*;

public class PSJF {

	private boolean isVerbose;
	private Processes processes;
    PriorityQueue<Process> readyQueue;
    
    private int clock = 0;
	
	public PSJF(Processes processes, boolean isVerbose) {
		this.isVerbose = isVerbose;
		this.processes = processes;
		this.readyQueue = new PriorityQueue<Process>(this.processes.size(),
	    	new Comparator<Process>() {
	    		public int compare(Process p1, Process p2) {    	
		    		if (p1.getCpuTimeLeft() < p2.getCpuTimeLeft()) {
		    			return -1;
		    		} else if (p1.getCpuTimeLeft() == p2.getCpuTimeLeft()){
		    			if (p1.getPid() < p2.getPid()) {
		    				return -1;
		    			} else {
		    				return 1;
		    			}
		    		} else {
		    			return 1;
		    		}
	    		}
	    	}
		);
	}		
	
	public boolean checkIfMinimum(Process p1) {				
		for (Process p2 : this.processes) {			
			if (!p2.isBlocked() &&
					p2.getArrivalTime() <= clock && 
					p2.getCpuTimeLeft() > 0 && 					
					p2.getCpuTimeLeft() < p1.getCpuTimeLeft()) {
				return false;
			}
		}		
		return true;
	}
	
	public void resetBlocked() {
		for (Process process : this.processes) {
			if (process.getBlockedTimeLeft() <= 0) {
				process.setBlocked(false);
			}
		}
	}
	
	public void run() {
		
        this.processes.setHeader();
                
        if (isVerbose) {
            System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        }
        
        while(!this.processes.checkIfAllTerminated()) {        	        	                	        	
        	this.processes.setRandomNumberFromFile(0, false);
        	this.processes.setRunning(this.processes.checkIfAnyProcessIsRunningForSJF());
        	this.processes.checkBlocked();
        	this.resetBlocked();
        	
        	if (isVerbose) {        		
        		this.processes.printCycle(clock);
        	}        	
        	
        	
        	for (Process process : this.processes) {        		        		
        		if (!process.isTerminated()) {
        			
	        		if (process.getArrivalTime() <= clock) {
	        			
	        			if (process.checkIfBlockedTimeExists()) {               				
	        				process.setState("blocked");
	        				process.setBlocked(true);
	        				process.decrementBlockedTimeLeft();        				        				        					        			
	        				process.setRunning(false);
	        				if (process.getCpuTimeLeft() < 0) {
	            				process.terminate(clock);
	            				this.processes.setRunning(false);	            				
	            			}	        					        			
	        				
	        			} else if (process.isRunning() && this.checkIfMinimum(process)) {
	        					        				
	        				process.setState("running");
		            		process.setBlocked(false);
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
	            			process.setBlocked(false);
	            			if (!this.readyQueue.contains(process)) {
	            				this.readyQueue.add(process);	            				
	            			}            
	        			}        			
	        		}	        		
        		}

            	if (!this.readyQueue.isEmpty() && this.checkIfMinimum(this.readyQueue.peek())) {
            		Process runningProcess =  this.processes.getRunningProcess();            		
    	        	if (runningProcess != null) {
    	        		if (this.readyQueue.peek().getCpuTimeLeft() < runningProcess.getCpuTimeLeft()) {    	        			
    	        			runningProcess.setRunning(false);    	        			
    	        			this.processes.setRunningProcess(this.readyQueue.peek());
    	        			 	        			
    	        		}
    	        	}        		        		
            	}        	         		
        	}        	        	

        	if (!this.readyQueue.isEmpty() && !this.processes.checkIfAnyProcessIsRunningForSJF()) {
        		
        		Process process = this.readyQueue.peek();
        		
        		if (this.checkIfMinimum(process)) {
        		
        		this.readyQueue.remove();        		
        		
        		process.setState("running");
        		process.setBlocked(false);
        		this.processes.setRunningProcess(process);
        		if (!process.checkIfRunningTimeExists()) {        			
        			this.processes.setRunningTimeLeft(process.getPid());
        		}        		
        		process.setRunning(true);
        		process.decrementRunningTimeLeft();

        		if (process.getCpuTimeLeft() < 0) {
    				process.terminate(clock);
    				this.processes.setRunning(false);    				
    			}
        		
       			if (process.getRunningTimeLeft() == 0 && !process.isTerminated() && process.getCpuTimeLeft() != 0) {            				
       				this.processes.setBlockedTimeLeft(process.getPid());
       				process.setRunning(false);
       			}        			   
       			
        		}
        	}
        	        	
        	clock++;
        }
        
        System.out.println("The scheduling algorithm used was Preemptive Shortest Job First\n");
        this.processes.setFooter(clock);
	}	
}
