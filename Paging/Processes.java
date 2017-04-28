import java.util.*;

public class Processes implements Iterable<Process> {
	private ArrayList<Process> processes = new ArrayList<Process>();
	private int size;
	
	public Processes(int jobMix, int pSize, int rSize) {
		this.initializeProcesses(jobMix, pSize, rSize);
		this.setSize(this.processes.size());
	}
	
	//Setting ABC according to job number. Add process inside processes
	private void initializeProcesses(int jobMix, int pSize, int rSize) {
		double A = 0, B = 0, C = 0;
		switch (jobMix) {
			case 1:
				A = 1;
				this.processes.add(new Process(A, B, C, 1, pSize, rSize));
				break;
			case 2:				
				A = 1;
				for (int i = 1; i < 5; i ++) {
					this.processes.add(new Process(A, B, C, i, pSize, rSize));
				}
				break;
			case 3:
				for (int i = 1; i < 5; i ++) {
					this.processes.add(new Process(A, B, C, i, pSize, rSize));
				}
				break;
			case 4:				
				this.processes.add(new Process(0.75, 0.25, 0, 1, pSize, rSize));
				this.processes.add(new Process(0.75, 0, 0.25, 2, pSize, rSize));
				this.processes.add(new Process(0.75, 0.125, 0.125, 3, pSize, rSize));
				this.processes.add(new Process(0.5, 0.125, 0.125, 4, pSize, rSize));
				break;
			default:
				break;				
		}
	}
	
	public Process get(int pid) {
		return this.processes.get(pid - 1);
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public Iterator<Process> iterator() {
		Iterator<Process> iProcess = this.processes.iterator();
		return iProcess;
	}
}

//Process object keeps track of values such as termination, number of faults, evictions, residency, etc
class Process {		
	double A = 0, B = 0, C = 0, R = 0;
	int pid;
	int size;	
	int word;
	private int numRef;
	private int quantum;
	private boolean terminated;
	private int faults = 0;
	private int evictions = 0;
	private int residency = 0;	
	
	public Process(double A, double B, double C, int pid, int size, int numRef) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.R = 1 - (A + B + C);
		this.pid = pid;
		this.size = size;
		this.numRef = numRef;
		this.word = (111 * pid) % size;		
		this.setQuantum(3);
	}
	
	public int calculateNextWord(int r) {
		double y = r / (Integer.MAX_VALUE + 1d);		
		if (y < this.A) {						
			this.word = (this.word + 1 + this.size) % this.size;
			return this.word;
		}
		if (y < this.A + this.B) {			
			this.word = (this.word - 5 + this.size) % this.size;
			return this.word;
		}
		if (y < this.A + this.B + this.C) {			
			this.word = (this.word + 4 + this.size) % this.size;
			return this.word;
		}
 
		return this.size + 1;
	}
	
	public void calculateRandom(int r) {
		this.word = r % this.size; 
	}
	
	public int getWord() {
		return this.word;		
	}
	
	public void decrementNumRef() {		
		this.numRef --;
		if (this.numRef <= 0) {
			this.setTerminated(true);
		}
	}
	
	public int getNumRef() {		
		return this.numRef;
	}
	
	public void decrementQuantum() {
		this.setQuantum(this.getQuantum() - 1);
	}

	public int getQuantum() {
		return quantum;
	}

	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}
	
	public void incrementFaults() {
		this.faults++;
	}
	
	public int getFaults() {
		return this.faults;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	public int getResidency() {
		return residency;
	}

	public void incrementResidency(int amount) {
		this.residency += amount;
	}

	public int getEvictions() {
		return evictions;
	}

	public void incrementEvictions() {
		this.evictions ++;
	}
}
