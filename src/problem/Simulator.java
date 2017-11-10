package problem;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import solver.FundingAllocationAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {
    private Random random = new Random();

	private int currentFortnight;
	private ProblemSpec problemSpec;
	private List<Integer> fundsAllocation;
	private ArrayList<List<Integer>> fundsAllocationHistory;
	private ArrayList<List<Integer>> additionalFundsHistory;
	private ArrayList<List<Integer>> customerOrderHistory;
    private double totalProfit = 0;
	private VentureManager ventureManager;
	private List<Matrix> probabilities;
	private boolean verbose = true;
	
	/** 
	 * True if you want the venture manager to start off being full, with random
	 * initial manufacturing funds allocation.
	 */
	public static boolean RANDOM_INITIAL_CONTENTS = false;
	
	/**
	 * Constructor
	 * @param problemSpecPath path to input file
	 * @throws IOException
	 */
	public Simulator(String problemSpecPath) throws IOException {
	    this(new ProblemSpec(problemSpecPath));
	}
	
	/**
	 * Constructor
	 * @param spec A ProblemSpec
	 */
	public Simulator(ProblemSpec spec) {
	    problemSpec = spec;
		ventureManager = problemSpec.getVentureManager();
		probabilities = problemSpec.getProbabilities();
	
        reset();
		
		if (verbose) {
			System.out.println("Problem spec loaded.");
			System.out.println("VentureManager: " + ventureManager.getName());
			System.out.println("Discount factor: " + 
					problemSpec.getDiscountFactor());
		}
	}
	
	public void reset() {
	    currentFortnight = 1;
	    fundsAllocation = problemSpec.getInitialFunds();
	    fundsAllocationHistory = new ArrayList<List<Integer>>();
	    additionalFundsHistory = new ArrayList<List<Integer>>();
	    customerOrderHistory = new ArrayList<List<Integer>>();
        totalProfit = 0;

	}
	
	/**
	 * Simulate a fortnight. A runtime exception is thrown if the additional
	 * funds allocation is invalid. If the additional funds allocation is valid,
	 * the customer order demand is sampled and the current fortnight is
	 * advanced.
	 * @param solver
     * @param numFortnightsLeft
	 */
	public void simulateStep(FundingAllocationAgent solver, int numFortnightsLeft) {
		if (verbose && currentFortnight > problemSpec.getNumFortnights()) {
			System.out.println("Warning: problem spec num fortnights exceeded.");
		}

		// compute profit for this week
		double profit = 0.0;

        // record manufacturing funds at start of fortnight
        fundsAllocationHistory.add(fundsAllocation);
        ArrayList<Integer> fortnightStartManufacturingFunds = new ArrayList<Integer>(fundsAllocation);

        // ##### Simulate customer orders
        List<Integer> orders = sampleCustomerOrders(fundsAllocation);

        for (int j = 0; j < orders.size(); j++) {
            // compute profit from sales
            int sold = Math.min(orders.get(j), fundsAllocation.get(j));
            profit += (sold * problemSpec.getSalePrices().get(j) * 0.6);

            // compute missed opportunity penalty
            int missed = orders.get(j) - sold;
            profit -= (missed * problemSpec.getSalePrices().get(j) * 0.25);

            // update manufacturing fund levels
            fundsAllocation.set(j, fundsAllocation.get(j) - sold);
        }

        // Add customer orders to history
        customerOrderHistory.add(orders);

        // record manufacturing fund levels after customer orders
        List<Integer> afterOrderFunds = new ArrayList<Integer>(fundsAllocation);


        // ##### Get additional funding amounts
        List<Integer> additionalFunding = solver.generateAdditionalFundingAmounts(afterOrderFunds, numFortnightsLeft);

		if (additionalFunding.size() != ventureManager.getNumVentures()) {
			throw new IllegalArgumentException("Invalid additional funding list size");
		}


        // ##### Apply additional funds to manufacturing fund levels
        int totalAdditional = 0;
		int totalFunds = 0;
		for (int i = 0; i < additionalFunding.size(); i++) {
			totalAdditional += additionalFunding.get(i);
			fundsAllocation.set(i, fundsAllocation.get(i) + additionalFunding.get(i));
			totalFunds += fundsAllocation.get(i);
		}
		if(totalAdditional > ventureManager.getMaxAdditionalFunding()) {
		    throw new IllegalArgumentException("Amount of additional funding is too large.");
		}
		if(totalFunds > ventureManager.getMaxManufacturingFunds()) {
			throw new IllegalArgumentException("Maximum manufacturing funds exceeded.");
		}

        ArrayList<Integer> afterAdditionalFundsAllocation = new ArrayList<Integer>(fundsAllocation);

        // add additional funding amount to history
        additionalFundsHistory.add(additionalFunding);

        // update total profit
        totalProfit += (Math.pow(problemSpec.getDiscountFactor(), currentFortnight - 1) * profit);
		
		if (verbose) {
			System.out.println();
			System.out.println("Fortnight " + currentFortnight);
			System.out.println("Start manufacturing funds:\t\t\t\t" + fortnightStartManufacturingFunds);
            System.out.println("Customer orders:\t\t\t\t" + orders);
            System.out.println("Funds after customer orders:\t" + afterOrderFunds);
			System.out.println("Additional funding:\t\t\t\t\t\t" + additionalFunding);
			System.out.println("Funds after additional funding:\t\t\t\t\t" + afterAdditionalFundsAllocation);
			System.out.println("End:\t\t\t\t\t\t" + fundsAllocation);
			System.out.println("Profit this fortnight: " + profit);
		
			if (currentFortnight == problemSpec.getNumFortnights()) {
				System.out.println();
                System.out.println("Total discounted profit: " + totalProfit);
			}
		}	
		currentFortnight++;
	}
	
	/**
	 * Uses the currently loaded stochastic model to sample customer order demand.
	 * Note that user wants may exceed the amount in the manufacturing fund
	 * @param state The manufacturing funds allocation
	 * @return Customer orders as list of item quantities
	 */
	public List<Integer> sampleCustomerOrders(List<Integer> state) {
		List<Integer> wants = new ArrayList<Integer>();
		for (int k = 0; k < ventureManager.getNumVentures(); k++) {
			int i = state.get(k);
			List<Double> prob = probabilities.get(k).getRow(i);
			wants.add(sampleIndex(prob));
		}
		return wants;
	}
	
	/**
	 * Returns an index sampled from a list of probabilities
	 * @precondition probabilities in prob sum to 1
	 * @param prob
	 * @return an int with value within [0, prob.size() - 1]
	 */
	public int sampleIndex(List<Double> prob) {
		double sum = 0;
		double r = random.nextDouble();
		for (int i = 0; i < prob.size(); i++) {
			sum += prob.get(i);
			if (sum >= r) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Saves the current history and total penalty to file
	 * @param filename The path to the text file to save to
	 * @throws IOException
	 */
	public void saveOutput(String filename) throws IOException {
		problemSpec.saveOutput(filename, customerOrderHistory, additionalFundsHistory);
	}

	/**
	 * Set verbose to true for console output
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	/**
	 * Get funds allocation from history
	 * @precondition fortnight < currentFortnight
	 * @param fortnight The fortnight to retrieve. Fortnight starts at 1.
	 * @return the fundsAllocation for that week.
	 */
	public List<Integer> getFnudsAllocationAt(int fortnight) {
	    return fundsAllocationHistory.get(fortnight - 1);
	}
	
	/**
     * Get additional funding from history
     * @precondition fortnight < currentFortnight
     * @param fortnight The fortnight to retrieve. Fortnight starts at 1.
     * @return the additional funding for that fortnight.
     */
	public List<Integer> getAdditionalFundingAt(int fortnight) {
	    return additionalFundsHistory.get(fortnight - 1);
	}
	
	 /**
     * Get customer orders from history
     * @precondition fortnight < currentFortnight
     * @param fortnight The fortnight to retrieve. Fortnight starts at 1.
     * @return the user request for that fortnight.
     */
    public List<Integer> getCustomerOrderAt(int fortnight) {
        return customerOrderHistory.get(fortnight - 1);
    }

    /**
     * @return the total profit so far
     */
    public double getTotalProfit() {
        return totalProfit;
    }

	public int getCurrentFortnight() {
		return currentFortnight;
	}
	
	public List<Integer> getFundsAllocation() {
		return new ArrayList<Integer>(fundsAllocation);
	}
}
