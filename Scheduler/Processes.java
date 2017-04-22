import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class Processes implements Iterable<Process> {
	
	private ArrayList<Process> processes;
	private File randomNumFile;
    private Scanner scanner;
    private int randomNumberFromFile = 0;
    private String randomNumberUsage = "";
    private boolean isRunning = false;
    private int totalBlockedNumber = 0;
    private Process runningProcess;
    private String type;
    
	public Processes(String type) {
        this.processes = new ArrayList<Process>();
        this.type = type;
        setRandomNumFile();        
        sort();        
	}
	
	public void add(Process p) {
		this.processes.add(p);
	}
	
	public int size() {
		return this.processes.size();
	}
	
	public void sort() {
		Collections.sort(this.processes);
		for (int i = 0; i < this.processes.size(); i++) {
			this.processes.get(i).setPid(i);
			int cpuTime = this.processes.get(i).getCpuTime();
            int arrivalTime = this.processes.get(i).getArrivalTime();			
			this.processes.get(i).setCpuTimeLeft(cpuTime);
			this.processes.get(i).setArrivalTime(arrivalTime);
		}
	}
	
	public Process get(int i) {
		return this.processes.get(i);
	}
	
	public int randomOS(int U, boolean running) {
		int X = scanner.nextInt();		
        int result = (1+(X%U));
        
        if (running) {
        	this.setRandomNumberFromFile(X, running);
        } else {
        	this.setRandomNumberFromFile(X, running);
        }
        
        return result;
	}
	
	public void setRunningTimeLeft(int i) {
		int cpuBurst = this.processes.get(i).getCpuBurst();
		this.processes.get(i).setRunningTimeLeft(this.randomOS(cpuBurst, true));
	}
	
	public void setBlockedTimeLeft(int i) {
		int ioBurst = this.processes.get(i).getIoBurst();
		this.processes.get(i).setBlockedTimeLeft(this.randomOS(ioBurst, false));
	}
	
	public void setRandomNumFile(){
        this.randomNumFile = new File("./random-numbers.txt");
        try{
            this.scanner = new Scanner(randomNumFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
		
	public void printCycle(int clock) {
		String str = this.returnHeaderString(clock);		
        for(int i=0; i< this.size(); i++){
        	if (this.get(i).getState().equals("running")) {
        		if (this.type == "rr") {
        			if (this.get(i).getCpuTimeLeft() == 0) {
        				str += this.getStatusString(this.get(i), 1);
        			} else {
        				str += this.getStatusString(this.get(i), this.get(i).getQuantum() + 1);
        			}        			
        		} else {
        			str += this.getStatusString(this.get(i), this.get(i).getRunningTimeLeft() + 1);
        		}                            		
        	} else {
                str += this.getStatusString(this.get(i), this.get(i).getBlockedTimeLeft() + 1);            		
        	}
        }
        str += ".";
        System.out.println(str);    	      
	}	
	
	public String getProcessesString() {
		String str = "" + this.size();
		for (int i = 0; i < this.size(); i++) {
			str += "  " + this.get(i).getProcessString();
		}
		return str;
	}

	private String returnHeaderString(int num) {
		if (num < 10) {
			return String.format("Before cycle    %d: ", num);
		} else if (num < 100) {
			return String.format("Before cycle   %d: ", num);
		} else if (num < 1000){
			return String.format("Before cycle  %d: ", num);
		} else {
			return String.format("Before cycle %d: ", num);
		}
	}
	
	private String getStatusString(Process process, int num) {
		if (process.getState().equals("ready")) {
			process.incrementWaitingTime();
			if (this.type == "psjf") {				
				return "      ready" + returnNumberString(process.getRunningTimeLeft());
			}
			return "      ready  0";
		}
		if (process.getState().equals("running")) {			
			return "    running"  + returnNumberString(num);
		}
		if (process.getState().equals("terminated")) {
			return " terminated  0";
		}
		if (process.getState().equals("unstarted")) {
			return "  unstarted  0";
		}
		if (process.getState().equals("blocked")) {
			process.incrementIoTime();
			return "    blocked"  + returnNumberString(num);
		}
		return null;
	}
	
	private String returnNumberString(int num) {
		if (num < 10) {
			return String.format("  %d", num);
		} else if (num < 100) {
			return String.format(" %d", num);
		} else {
			return String.format("%d", num);
		}
	}	
	
	public boolean checkIfAnyProcessIsRunning() {
		for (Process process : this.processes) {
			if (process.checkIfRunningTimeExists()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfAnyProcessIsRunningForSJF() {
		for (Process process : this.processes) {
			if (process.isRunning()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfAnyProcessHasQuantum() {
		for (Process process: this.processes) {
			if (process.getQuantum() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfAllTerminated() {
		for (Process process : this.processes) {
			if (!process.isTerminated()) {
				return false;
			}
		}
		return true;
	}
	
	
	
	@Override
    public String toString(){
        String string = "";
        for(int i=0; i< this.size(); i++){
            string = string + this.processes.get(i).toString()+"\n\n";
        }
        return string;
    }

	@Override
	public Iterator<Process> iterator() {
		Iterator<Process> iProcess = this.processes.iterator();
		return iProcess;
	}

	public int getRandomNumberFromFile() {
		if (randomNumberFromFile != 0) {
			System.out.println(this.randomNumberUsage + randomNumberFromFile);
		}		
		return randomNumberFromFile;
	}

	public void setRandomNumberFromFile(int randomNumberFromFile, boolean running) {
		if (running) {
			this.randomNumberUsage = "running";
		} else {
			this.randomNumberUsage = "blocking";
		}
		this.randomNumberFromFile = randomNumberFromFile;
	}
	
	public void checkBlocked() {
		for (Process process : this.processes) {
			if (process.getState().equals("blocked")) {
				this.totalBlockedNumber ++;
				break;
			}
		}
	}

	public void setHeader() {
		System.out.println("The original input was: " + this.getProcessesString());
        this.sort();
        System.out.println("The (sorted) input is: " + this.getProcessesString() + "\n");                
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public void setFooter(int clock) {
		int cpuTime = 0;
		int turnaroundTime = 0;
		int waitTime = 0;		
		clock--;
		
		for (Process process : this.processes) {
			cpuTime += process.getCpuTime();
			turnaroundTime += process.getTurnaroundTime();
			waitTime += process.getWaitingTime();
		}
	    DecimalFormat formatter = new DecimalFormat("#0.00000");
		
		int finishingTime = clock;
        String cpuUtilization = formatter.format((cpuTime+0.0)/(clock));
        String ioUtilization = formatter.format((this.totalBlockedNumber+0.0)/(clock));
        String throughput = formatter.format(((this.size()/(clock+0.0))*100.0));
        String avgTurnaround = formatter.format((turnaroundTime+0.0)/this.size());
        String totalWaitTime = formatter.format((waitTime+0.0)/this.size());
           
		String str =
				this.toString()
				+ "Summary Data:"
                + "\n	Finishing time: " + finishingTime
                + "\n	CPU Utilization: " + cpuUtilization
                + "\n	I/O Utilization: " + ioUtilization
        		+ "\n	Throughput: " + throughput + " processes per hundred cycles"
        		+ "\n	Average turnaround time: " + avgTurnaround
        		+ "\n	Average waiting time: " + totalWaitTime;
		
		System.out.println(str);
		System.out.print("\n\n\n");
	}

	public Process getRunningProcess() {
		return runningProcess;
	}

	public void setRunningProcess(Process runningProcess) {
		this.runningProcess = runningProcess;
	}
}
