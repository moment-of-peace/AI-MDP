package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import java.lang.reflect.Constructor;

import problem.ProblemSpec;
import problem.Simulator;

public class Runner {
	/** The path for the input file. */
	private static String inputPath = null;
	/** The path for the output file. */
	private static String outputPath = null;
	
	/** The default number of simulations to run. */
	public static int DEFAULT_NUM_SIMULATIONS = 1;
	/** The number of simulations to run. */
	private static int numSimulations = 0; 
	
	/** The default solver to use. */
	public static String DEFAULT_SOLVER = "solver.MySolver";
	/** The name of the solver class that will be used. */
	private static String solverName = null;
	
	/** Whether to re-create the solver for every simulation. */
	public static boolean RECREATE_SOLVER = true;

	public static void main(String[] args) throws Exception {
		parseCommandLine(args);
		Class<?> clazz = Class.forName(solverName);
		Constructor<?> ctor = clazz.getConstructor(ProblemSpec.class);
		
		ProblemSpec spec = new ProblemSpec(inputPath);

		double totalProfit = 0;
		
		Simulator simulator = new Simulator(spec);
		FundingAllocationAgent solver = null;
		if (!RECREATE_SOLVER) {
			solver = (FundingAllocationAgent)ctor.newInstance(spec);
			solver.doOfflineComputation();
		}
		for (int simNo = 0; simNo < numSimulations; simNo++) {
	        
			System.out.printf("Run #%d\n", simNo+1);
			System.out.println("-----------------------------------------------------------");
			
			simulator.reset();
			if (RECREATE_SOLVER) {
				solver = (FundingAllocationAgent)ctor.newInstance(spec);
				solver.doOfflineComputation();
			}
			
			for (int i = 0; i < spec.getNumFortnights(); i++) {
				simulator.simulateStep(solver, spec.getNumFortnights() - (i+1));
			}

			totalProfit += simulator.getTotalProfit();
			System.out.println("-----------------------------------------------------------");
		}
		
		simulator.saveOutput(outputPath);
		System.out.printf("Summary statistics from %d runs:\n", numSimulations);
		System.out.println();
		System.out.printf("Overall profit: %f\n", totalProfit);
	}
	
	/**
	 * Parses the command line arguments.
	 * 
	 * @param args
	 *            the array of command line arguments.
	 */
	public static void parseCommandLine(String args[]) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			if (inputPath == null) {
				inputPath = arg;
			} else if (outputPath == null) {
				outputPath = arg;
			} else if (solverName == null) {
			    solverName = arg;
			} else if (numSimulations == 0) {
				numSimulations = Integer.valueOf(arg);
			} 
		}
		if (inputPath == null) {
			throw new IllegalArgumentException("Input path not given.");
		}
		if (outputPath == null) {
            throw new IllegalArgumentException("Output path not given.");
		}
		if (solverName == null) {
		    solverName = DEFAULT_SOLVER;
		}
		if (numSimulations == 0) {
			numSimulations = DEFAULT_NUM_SIMULATIONS;
		}
	}

}
