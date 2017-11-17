package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import problem.ProblemSpec;
import problem.Simulator;

public class Runner {
	/** The path for the input file. */
	private static String inputPath;
	/** The path for the output file. */
	private static String outputPath;
	
	/** The default number of simulations to run. */
	private static int numSimulations = 1; 
	
	/** Whether to re-create the solver for every simulation. */
	public static boolean RECREATE_SOLVER = true;
	
	/** Use Monte Carlo if true, otherwise use value iteration */
	protected static boolean useMC = true; // use Monte Carlo by default

	public static void main(String[] args) throws Exception {
		parseCommandLine(args);
		
		ProblemSpec spec = new ProblemSpec(inputPath);
		double expectedProfit = spec.getNumFortnights() * spec.getVentureManager().getMaxManufacturingFunds() * 0.5;

		double totalProfit = 0;
		int totalPass = 0;
		
		Simulator simulator = new Simulator(spec);
		FundingAllocationAgent solver = null;
		if (!RECREATE_SOLVER) {
			solver = new MySolver(spec,useMC);
			if (!useMC) {    // only value iteration needs offline computation
			    solver.doOfflineComputation();
			}
		}
		for (int simNo = 0; simNo < numSimulations; simNo++) {
	        
			System.out.printf("Run #%d\n", simNo+1);
			System.out.println("-----------------------------------------------------------");
			
			simulator.reset();
			if (RECREATE_SOLVER) {
				solver = new MySolver(spec,useMC);
				if (!useMC) {    // only value iteration needs offline computation
	                solver.doOfflineComputation();
	            }
			}
			
			for (int i = 0; i < spec.getNumFortnights(); i++) {
				simulator.simulateStep(solver, spec.getNumFortnights() - (i+1));
			}
			double tempProfit = simulator.getTotalProfit();
			if (tempProfit >= expectedProfit) {
			    totalPass++;
			}
			totalProfit += tempProfit;
			System.out.println("-----------------------------------------------------------");
		}
		
		simulator.saveOutput(outputPath);
		System.out.printf("Summary statistics from %d runs:\n", numSimulations);
		System.out.println();
		System.out.printf("Overall profit: %f\n", totalProfit);
		System.out.printf("Overall pass: %f\n", totalPass);
	}
	
	/**
	 * Parses the command line arguments.
	 * 
	 * @param args
	 *            the array of command line arguments.
	 */
	public static void parseCommandLine(String args[]) {
	    if (args.length < 2) {
	        throw new IllegalArgumentException("Require at least two arguements");
	    }
	    inputPath = args[0];
	    outputPath = args[1];
	    
	    if (args.length > 2) {
	        if (args[2] == "v"){
                useMC = false;
            }
	    }
	    if (args.length > 3) {
	        numSimulations = Integer.valueOf(args[3]);
	    }
	}

}
