import java.io.*;
import java.util.*;

public class Main {	
	public static FIFO fifo;
	public static Bankers bankers;
	
	public static void main(String[] args) {
		String fileName = "";
		//If file name isn't provided, throw a message and quit
		if (args.length == 1) {
			fileName = args[0];
		} else {
			System.out.println("Please enter an input file name ex) input-01.txt");
            System.exit(0);
		}
		
		setup(fileName);		
		fifo.run();
		bankers.run();
	}
	
	//This is a function for reading in the filesystem
	public static Scanner readInput(String fileName) {
		Scanner reader = null;
		try{
    		reader = new Scanner(new BufferedReader(new FileReader("./" + fileName)));    		     		        			
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
		
		return reader;
	}
	
	//This is a function for setting up both FIFO and bankers
	public static void setup(String input) {
		Scanner reader = readInput(input);
		
		//First line is parsed first
		String firstLine = reader.nextLine();
		String[] firstLineArray = firstLine.split(" ");
				
		fifo = new FIFO();
		bankers = new Bankers();
		for (int i = 2; i < firstLineArray.length; i++) {
			Resource resource = new Resource(Integer.parseInt(firstLineArray[i]));
			fifo.addResource(i - 1, resource);
			bankers.addResource(i - 1, resource);
		}
		
		//For other lines, commands are saved into each task
		while (reader.hasNextLine()) {
			String line = reader.nextLine();				
			if (!line.equals("")) {											
				String[] taskArray = line.split("\\s+");
				String command = taskArray[0];				
				//4 numbers coming after the type of request are saved in array of commands for that task
				int taskNumber = Integer.parseInt(taskArray[1]);
				int delay = Integer.parseInt(taskArray[2]);
				int resourceType = Integer.parseInt(taskArray[3]);
				int amount = Integer.parseInt(taskArray[4]);
				
				//Add commands
				switch(command) {					
					case "initiate": {							
						Task taskFifo = fifo.getTask(taskNumber);
						Task taskBanker = bankers.getTask(taskNumber);
						
						//Adding for both fifo and bankers
						if (taskFifo == null) {							
							Task newTask = new Task(taskNumber);
							newTask.addCommand(command, delay, resourceType, amount);
							fifo.addTask(newTask);							
						} else {
							taskFifo.addCommand(command, delay, resourceType, amount);							
						}	
						
						if (taskBanker == null) {
							Task newTask = new Task(taskNumber);
							newTask.addCommand(command, delay, resourceType, amount);
							bankers.addTask(newTask);							
						} else {
							taskBanker.addCommand(command, delay, resourceType, amount);							
						}	
						
						break;
					}					
					case "request": {						
						fifo.getTask(taskNumber).addCommand(command, delay, resourceType, amount);
						bankers.getTask(taskNumber).addCommand(command, delay, resourceType, amount);
						break;
					}
					case "release": {
						fifo.getTask(taskNumber).addCommand(command, delay, resourceType, amount);
						bankers.getTask(taskNumber).addCommand(command, delay, resourceType, amount);
						break;
					}
					case "terminate": {
						fifo.getTask(taskNumber).addCommand(command, delay, 0, 0);
						bankers.getTask(taskNumber).addCommand(command, delay, 0, 0);
						break;
					}										
				}														      
			}			
		}									
	}
}

//We keep release object to keep track of which resource to release at the end of each cycle
class Release {
	int resourceType;
	int amount;
	Task task;
	
	Release(int resourceType, int amount, Task task) {
		this.resourceType = resourceType;
		this.amount = amount;
		this.task = task;		
	}	
}

//Comparator for comparing which task to give resources to
class CompareTasks implements Comparator<Task> {
  @Override
  public int compare(Task t1, Task t2) {
		if (t1.getPriority() > t2.getPriority()) {
			return -1;
		} else if (t1.getPriority() == t2.getPriority()){
			return 0;
		} else {
			return 1;
		}
  }
}
