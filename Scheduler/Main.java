import java.util.*;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		
		String fileName = "";
        boolean isVerbose = false;
        if(args.length == 1){
        	fileName = args[0];
        }
        else if(args.length == 2){
            if(args[0].equals("--verbose")){
                isVerbose = true;
                fileName = args[1];
            }
            else{
                System.out.println("Error: Please make sure you entered a verbose flag");
                System.exit(0);
            }
        }
        else{
            System.out.println("Error: Too many arguments");
            System.exit(0);
        }
        
		fcfs(fileName, isVerbose);
		lcfs(fileName, isVerbose);
		rr(fileName, isVerbose);
		psjf(fileName, isVerbose);
        
	}	
	
	public static void fcfs(String input, boolean isVerbose) {
		Processes processes = readInput(input, "fcfs");
		FCFS fcfs = new FCFS(processes, isVerbose);
		fcfs.run();		
	}
	
	public static void rr(String input, boolean isVerbose) {
		Processes processes = readInput(input, "rr");
		RR rr = new RR(processes, isVerbose);
		rr.run();	
	}
	
	public static void lcfs(String input, boolean isVerbose) {
		Processes processes = readInput(input, "lcfs");
		LCFS lcfs = new LCFS(processes, isVerbose);
		lcfs.run();	
	}
	
	public static void psjf(String input, boolean isVerbose) {
		Processes processes = readInput(input, "psjf");
		PSJF psjf = new PSJF(processes, isVerbose);
		psjf.run();	
	} 
		
	public static Processes readInput(String fileName, String type) {
		
		Processes processes = new Processes(type);
        try{
    		Scanner reader = new Scanner(new BufferedReader(new FileReader("./" + fileName)));    		     		    
    				
        	int numberOfProcesses = Integer.parseInt(reader.next());        	
        	for (int i = 0; i < numberOfProcesses; i++) {
        		ArrayList<Integer> rawProcess = new ArrayList<Integer>();
        		
        		for (int j = 0; j < 4; j++) {
        			int value = Integer.parseInt(reader.next());
        			rawProcess.add(value);
        		}
        		
        		processes.add(new Process(rawProcess));
        		        		
        	}        	        	
        	reader.close();
        	        	        	        
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        return processes;
	}
}
