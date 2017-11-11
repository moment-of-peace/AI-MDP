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
    private HashMap<FundState, Double> rewards;
    
    public MySolver(ProblemSpec spec) throws IOException {
        this.spec = spec;
        ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
        policy = new HashMap<FundState, Integer[]>();
        rewards = new HashMap<FundState, Double>();
    }
    
    public void doOfflineComputation() {
        rewards = getRewards();
        HashMap<FundState, Double> previousValues = rewards;
        HashMap<FundState, Double> currentValues = new HashMap<FundState, Double>();
        ArrayList<Double[][]> transfer = getTransMatrix();
        do {
            valueIteration(previousValues, currentValues, transfer);
            previousValues = currentValues;
        } while (!converge(previousValues, currentValues));
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
    
    private ArrayList<Double[][]> getTransMatrix() {
        //Get estimated number of orders matrices
    	List<Matrix> probabilitiesMatrix = spec.getProbabilities();
    	ArrayList<Double[][]> transFuncs = new ArrayList<Double[][]>();
    	
    	//For every probability matrix
    	for(Matrix matrix:probabilitiesMatrix){
    		//Iterate every data in matrix
    		int rows = matrix.getNumRows();
    		int cols = matrix.getNumCols();
    		
    		Double[][] transMatrix = new Double[rows][cols];
    		
    		//for each row
    		for(int j=0; j<rows; j++){
 
    			//for each column
    			for(int k=0; k<cols; k++){
    				if(k>j){
    					double t = 0;
    					transMatrix[j][k] = t;
    				}else if(k>0 && j>=k){
    					double t = matrix.get(j, j-k);
    					transMatrix[j][k] = t;
    				}else if(k==0){
    					double t=0;
    					for(int i=j; i<cols; i++){
    						t = t + matrix.get(j, i);
    					}
    					transMatrix[j][k] = t;
    				}
    			}
    		}
    		
    		transFuncs.add(transMatrix);
    	}
    	
        return transFuncs;
    }
    
    // immediate max reward of each fund state 
    private HashMap<FundState, Double> getRewards() {
        // TODO Auto-generated method stub
        return null;
    }

    private void valueIteration(HashMap<FundState, Double> previous, HashMap<FundState, Double> current, 
            ArrayList<Double[][]> transfer) {
        // TODO Auto-generated method stub

    }
    
    // return true if the |values - previousValues| is small enough
    private boolean converge(HashMap<FundState, Double> previous, HashMap<FundState, Double> current) {
        return false;
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
