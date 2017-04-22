import java.util.*;

public class Process implements Comparable<Process>{
    	
	private int pid;
	
	private int arrivalTime;
	private int cpuBurst;
	private int cpuTime;
	private int ioBurst;
	
	private int finishingTime = 0;
	private int turnaroundTime = 0;
	private int ioTime  = 0;
    private int waitingTime = 0;
    
    private int cpuTimeLeft;
    private int runningTimeLeft = 0;
    private int blockedTimeLeft = 0;    
        
    private String state = "unstarted";
    private boolean terminated = false;
    
    private int quantum = 0;    
    private boolean running = false;
    private boolean blocked = false;
    
	public Process(ArrayList<Integer> process) {
		this.setArrivalTime(process.get(0));		
		this.setCpuBurst(process.get(1));
		this.setCpuTime(process.get(2));
		this.setCpuTimeLeft(process.get(2));
		this.setIoBurst(process.get(3));	
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getCpuBurst() {
		return cpuBurst;
	}

	public void setCpuBurst(int cpuBurst) {
		this.cpuBurst = cpuBurst;
	}

	public int getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(int cpuTime) {
		this.cpuTime = cpuTime;
	}

	public int getIoBurst() {
		return ioBurst;
	}

	public void setIoBurst(int ioBurst) {
		this.ioBurst = ioBurst;
	}

	public int getFinishingTime() {
		return finishingTime;
	}

	public void setFinishingTime(int finishingTime) {
		this.finishingTime = finishingTime;
	}

	public int getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public int getIoTime() {
		return ioTime;
	}

	public void setIoTime(int ioTime) {
		this.ioTime = ioTime;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public int getCpuTimeLeft() {
		return cpuTimeLeft;
	}
	
	public void setCpuTimeLeft(int cpuTimeLeft) {
		this.cpuTimeLeft = cpuTimeLeft;
	}
	
	public int getRunningTimeLeft() {
		return runningTimeLeft;
	}
	
	public void setRunningTimeLeft(int runningTimeLeft) {
		this.runningTimeLeft = runningTimeLeft;
	}
	
	public void decrementRunningTimeLeft() {
		this.cpuTimeLeft--;
		this.runningTimeLeft --;
	}
	
	public int getBlockedTimeLeft() {
		return blockedTimeLeft;
	}
	
	public void setBlockedTimeLeft(int blockedTimeLeft) {
		this.blockedTimeLeft = blockedTimeLeft;
	}
	
	public void decrementBlockedTimeLeft() {
		this.blockedTimeLeft --;
	}

	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {		
		this.state = state;
	}
	
	public boolean isTerminated() {
		return this.terminated;
	}
	
	public void incrementIoTime() {
		this.ioTime ++;
	}
	
	public void incrementWaitingTime() {
		this.waitingTime ++;
	}
	
	public boolean checkIfRunningTimeExists() {
		if (this.runningTimeLeft != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkIfBlockedTimeExists() {
		if (this.blockedTimeLeft != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void terminate(int clock) {
		this.setRunningTimeLeft(0);
		this.resetQuantum();
		this.setState("terminated");
		this.setRunning(false);
		this.terminated = true;
		this.finishingTime = clock;
		this.turnaroundTime = clock - this.arrivalTime;
	}
	
	public int getQuantum() {
		return quantum;
	}

	public void setQuantum() {
		this.quantum = 2;
	}
	
	public void decrementQuantum() {
		this.quantum --;
	}
	
	public void resetQuantum() {
		this.quantum = 0;
	}
	
	public int compareTo(Process p){
        if(this.arrivalTime < p.getArrivalTime()){
            return -1;
        }
        else if(this.arrivalTime > p.getArrivalTime()){
            return 1;
        }
        else{
            return 0;
        }
    }
	
	public String getProcessString() {
		return String.format("%d %d %d %d", this.arrivalTime, this.cpuBurst, this.cpuTime, this.ioBurst);
	}
	
	@Override
    public String toString(){
        return "Process: "+this.pid +":\n"
                + String.format("	(A, B, C, IO) = (%s)", this.getProcessString())
        		+ "\n	Finishing time: " + this.finishingTime
        		+ "\n	Turnaround time: " + this.turnaroundTime
        		+ "\n	I/O time: " + this.ioTime
        		+ "\n	Waiting time: " + this.waitingTime;
    }

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
