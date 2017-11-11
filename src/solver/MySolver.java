package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problem.VentureManager;
import problem.Matrix;
import problem.ProblemSpec;

public class MySolver implements FundingAllocationAgent {
	
	private ProblemSpec spec = new ProblemSpec();
	private VentureManager ventureManager;
    private List<Matrix> probabilities;
    private HashMap<FundState, Integer[]> policy;
    private HashMap<FundState, Double> values;
	
	public MySolver(ProblemSpec spec) throws IOException {
	    this.spec = spec;
		ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
        policy = new HashMap<FundState, Integer[]>();
        values = new HashMap<FundState, Double>();
	}
	
	public void doOfflineComputation() {
	    // initially, V(s) = R(s), i.e. compute the immediate best rewards for all states
	    HashMap<FundState, Double> previousValues = rewards();
	    while (true) {
	        valueIteration();
	        previousValues = values;
	    }
	}

    public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
														  int numFortnightsLeft) {
		// Example code that allocates an additional $10 000 to each venture.
		// TODO Replace this with your own code.

		List<Integer> additionalFunding = new ArrayList<Integer>();

		int totalManufacturingFunds = 0;
		for (int i : manufacturingFunds) {
			totalManufacturingFunds += i;
		}
		
		int totalAdditional = 0;
		for (int i = 0; i < ventureManager.getNumVentures(); i++) {
			if (totalManufacturingFunds >= ventureManager.getMaxManufacturingFunds() ||
			        totalAdditional >= ventureManager.getMaxAdditionalFunding()) {
				additionalFunding.add(0);
			} else {
				additionalFunding.add(1);
				totalAdditional ++;
				totalManufacturingFunds ++;
			}
		}

		return additionalFunding;
	}
    
    private HashMap<FundState, Double> rewards() {
        // TODO Auto-generated method stub
        return null;
    }
    
    private void valueIteration() {
        // TODO Auto-generated method stub
        
    }
}

class FundState {
    protected Integer[] states;
    
    public FundState(Integer[] funds) {
        states = new Integer[funds.length];
        for (int i = 0; i < funds.length; i++) {
            states[i] = funds[i];
        }
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < states.length; i++) {
            h = 31*h + states[i];
        }
        return h;
    }
}