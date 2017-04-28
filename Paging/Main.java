
public class Main {
	public static void main(String[] args) {
//		String[] mockArgs1 = {"10", "10", "20", "1", "10", "lru"};
//		String[] mockArgs2 = {"10", "10", "10", "1", "100", "lru"};
//		String[] mockArgs3 = {"10", "10", "10", "2", "10", "lru"};
//		String[] mockArgs4 = {"20", "10", "10", "2", "10", "lru"};
//		String[] mockArgs5 = {"20", "10", "10", "2", "10", "random"};
//		String[] mockArgs6 = {"20", "10", "10", "2", "10", "fifo"};
//		String[] mockArgs7 = {"20", "10", "10", "3", "10", "lru"};
//		String[] mockArgs8 = {"20", "10", "10", "3", "10", "fifo"};
//		String[] mockArgs9 = {"20", "10", "10", "4", "10", "lru"};
//		String[] mockArgs10 = {"20", "10", "10", "4", "10", "random"};
//		String[] mockArgs11 = {"90", "10", "40", "4", "100", "lru"};
//		String[] mockArgs12 = {"40", "10", "90", "1", "100", "lru"};
//		String[] mockArgs13 = {"40", "10", "90", "1", "100", "fifo"};
//		String[] mockArgs14 = {"800", "40", "400", "4", "5000", "lru"};
//		String[] mockArgs15 = {"10", "5", "30", "4", "3", "random"};
//		String[] mockArgs16 = {"1000", "40", "400", "4", "5000", "fifo"};
				
		//If argument is less than or equal to 6, throw error 
		if (args.length < 6) {
			System.out.println("Error: Please make sure you entered all arguments: ");			
            System.exit(0);
		}
		
		Driver driver = setupDriver(args);
		driver.run();
	}
	
	//Set up driver with all the arguments
	public static Driver setupDriver(String[] args) {
		System.out.println("The machine size is " + args[0] + ".");
		System.out.println("The page size is " + args[1] + ".");
		System.out.println("The process size is " + args[2] + ".");
		System.out.println("The job mix number is " + args[3] + ".");
		System.out.println("The number of references per process is " + args[4] + ".");
		System.out.println("The replacement algorithm is " + args[5] + ".");
		
		int M = Integer.parseInt(args[0]);
		int P = Integer.parseInt(args[1]);
		int S = Integer.parseInt(args[2]);
		int J = Integer.parseInt(args[3]);
		int N = Integer.parseInt(args[4]);
		String R = args[5];
		
		return new Driver(M, P, S, J, N, R);
	}
}
