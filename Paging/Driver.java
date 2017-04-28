import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {
	Processes processes;
	Machine machine;
	File randomNumFile;
    Scanner scanner;
    int terminated = 0;
    int time = 0;
	
	public Driver(int machineSize, int pageSize, int processSize, int jobMix, int numberOfReferences, String algorithm) {
		this.setRandomNumFile();
		this.processes = new Processes(jobMix, processSize, numberOfReferences);
		this.machine = new Machine(machineSize, pageSize, algorithm, this.scanner);		
	}
	
	public void run() {
		
		//Loop until all processes meet the reference number requirements
		while (terminated < this.processes.getSize()) {
			
			//Loop all process according to pid
			for (Process process : this.processes) {
				if (process.isTerminated()) break;
				if (process.getQuantum() == 0) process.setQuantum(3);			
				
				//Loop for quantum = 3
				while (process.getQuantum() > 0) {
					time++;
					
					boolean hit = this.machine.refer(process.word, process, time);
					
					//If fault, add page
					if (!hit) {
						this.machine.addPage(process.word, time, process);
						process.incrementFaults();
					}
					
					process.decrementNumRef();
					process.decrementQuantum();									
					
					//Prepare next word
					int nextWord = process.calculateNextWord(this.getRandomNum());
					if (nextWord > process.size) {
						process.calculateRandom(this.getRandomNum());
					}							
					
					//If terminated, increment the overall termianted count
					if (process.isTerminated()) {
						this.terminated++;
						break;
					}
				}							
			}
		}
		
		this.printSummary();
	}
	
	//Setting random file from fs
	public void setRandomNumFile(){
        this.randomNumFile = new File("./random-numbers.txt");
        try{
            this.scanner = new Scanner(randomNumFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
	
	public int getRandomNum() {			        
        return scanner.nextInt();
	}
	
	public void printSummary() {
		int totalFaults = 0;
		int totalResidency = 0;
		int totalEvictions = 0;		
		
		System.out.println("");
		
		//Loop through all processes and get their summaries, while preparing for total Summary
		for (Process process : this.processes) {
			totalResidency += process.getResidency();
			totalEvictions += process.getEvictions();
			
			String summary;
			if (process.getEvictions() == 0) {
				summary = "Process " + process.pid + " had " + process.getFaults() + " faults. "
						+ "With no evictions, the average residence is undefined. ";						
			} else {
				double residency = (double) process.getResidency() / process.getEvictions();
				summary = "Process " + process.pid + " had " + process.getFaults() + " faults and "
						+ residency + " average residency.";
			}			
			
			totalFaults += process.getFaults();			
			
			System.out.println(summary);
		}
		
		//Write total summary after individual summaries are finished
		String totalSummary;
		if (totalEvictions == 0) {
			totalSummary = "The total number of faults is " + totalFaults 
					+ ". With no evictions, the overall average residence is undefined.";
		} else {
			double avgResidency = (double) totalResidency / totalEvictions ;		
			totalSummary = "The total number of faults is " + totalFaults 
							+ " and the overall average residency is " + avgResidency + ".";
		}		
		
		System.out.println("\n" + totalSummary);
	}
	
}
