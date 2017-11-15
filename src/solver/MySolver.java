package solver;

import java.io.FileWriter;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import problem.VentureManager;
import problem.Matrix;
import problem.ProblemSpec;

public class MySolver implements FundingAllocationAgent {
    
    private ProblemSpec spec = new ProblemSpec();
    private VentureManager ventureManager;
    private List<Matrix> probabilities;
    private double discount;
    private int ventureNum;
    private int maxFund;
    private int maxAdd;
    private HashMap<FundState, Integer[]> policy;
    private ArrayList<Double[]> rewards;
    
    public MySolver(ProblemSpec spec) throws IOException {
        this.spec = spec;
        ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
        discount = spec.getDiscountFactor();
        ventureNum = ventureManager.getNumVentures();
        maxFund = ventureManager.getMaxManufacturingFunds();
        maxAdd = ventureManager.getMaxAdditionalFunding();
        policy = new HashMap<FundState, Integer[]>();
        rewards = new ArrayList<Double[]>();
    }
    
    public void doOfflineComputation() {
        rewards = getRewards();
        HashMap<FundState, Double> previousValues;
        HashMap<FundState, Double> currentValues = getInitValues(ventureNum, maxFund);
        ArrayList<Double[][]> transfer = getTransMatrix();
        double maxError;
        do {
            previousValues = currentValues;
            currentValues = new HashMap<FundState, Double>();
            maxError = valueIteration(previousValues, currentValues, transfer);
        } while (maxError > 0.0000001);
        try {
            printPolicy();
            printValue(currentValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Compute initial values using max immediate rewards
    private HashMap<FundState, Double> getInitValues(int ventureNum, int maxFund) {
        ArrayList<ArrayList<Integer>> allStates = findAllStates(ventureNum, maxFund);
        HashMap<FundState, Double> initValues = new HashMap<FundState, Double>();
        for (ArrayList<Integer> state: allStates) {
            FundState fund = new FundState(state);
            if (isValidFund(fund)) {
                double reward = 0;
                for (int i = 0; i < state.size(); i++) {
                    reward += this.rewards.get(i)[state.get(i)];
                }
                initValues.put(fund, reward);
            }
        }
        return initValues;
    }
    
    // generate all possible states
    private ArrayList<ArrayList<Integer>> findAllStates(int ventureNum, int maxFund) {
        ArrayList<ArrayList<Integer>> allStates = new ArrayList<ArrayList<Integer>>();
        if (ventureNum == 1) {
            for (int j = 0; j <= maxFund; j++) {
                ArrayList<Integer> state = new ArrayList<Integer>();
                state.add(j);
                allStates.add(state);
            }
            return allStates;
        } else {
            for (ArrayList<Integer> s: findAllStates(ventureNum-1, maxFund)) {
                for (int j = 0; j <= maxFund; j++) {
                    ArrayList<Integer> state = new ArrayList<Integer>(s);
                    state.add(j);
                    allStates.add(state);
                }
            }
            return allStates;
        }
    }

    public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
                                                          int numFortnightsLeft) {
        // Example code that allocates an additional $10 000 to each venture.
        // TODO Replace this with your own code.
        List<Integer> additionalFunding = new ArrayList<Integer>();;
/*        Integer[] states = policy.get(new FundState(manufacturingFunds)); // this is using value iteration
        for (Integer i: states) {
            additionalFunding.add(i);
        }
*/        
        // use monte carlo tree search
        MonteCarloSearch mcsearch = new MonteCarloSearch(spec);
        List<Integer> nextState = mcsearch.findNext(manufacturingFunds, numFortnightsLeft);
        for (int i = 0; i < nextState.size(); i++) {
            additionalFunding.add(nextState.get(i)-manufacturingFunds.get(i));
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

    // immediate max reward of each fund state, use index to represent fund state
    private ArrayList<Double[]> getRewards() {
        // TODO Auto-generated method stub
        int rewardLength = this.maxFund + 1;
        
        //rewards is predefined
     
        for (int i = 0; i <probabilities.size(); i++) {
            Double[] reward = new Double[rewardLength];
            for (int j = 0; j < rewardLength; j++) {
                reward[j] = 0.0;
                for (int k = 0; k < rewardLength; k++) {
                    double tempProb= probabilities.get(i).get(j, k);
                    //i is the venture number
                    double profit = Math.min(j, k) * spec.getSalePrices().get(i)*0.6;
                    int missed = Math.min(0, j-k);
                    profit += missed*spec.getSalePrices().get(i)*0.25;
                    
                    //multiply??
                    profit *= tempProb;
                    reward[j] += profit;
                }
            }
            rewards.add(reward);
        }
        return rewards;
    }

    private double valueIteration(HashMap<FundState, Double> previous, HashMap<FundState, Double> current, 
            ArrayList<Double[][]> transfer) {
        double error = 0;
        for (FundState s: previous.keySet()) {
            double maxValue = Double.NEGATIVE_INFINITY;
            // iterate all actions
            for (FundState state: previous.keySet()) {
                Integer[] action = state.states.clone();
                if (isValidAction(s, action)) {
                    // compute immediate reward
                    double reward = 0;
                    for (int i = 0; i < s.states.length; i++) {
                        reward += this.rewards.get(i)[s.states[i]+action[i]];
                    }
                    double newValue = reward + this.discount*expectedValue(transfer, s, previous, action);
                    if (newValue > maxValue) {
                        // update max value and policy
                        maxValue = newValue;
                        this.policy.put(s, action);
                    }
                    
                }
            }
            current.put(s, maxValue);   // update value function
            double newError = Math.abs(maxValue-previous.get(s));// update max error
            if (newError > error) {
                error = newError;
            }
        }
        return error;
    }

    // The expected values, i.e. sum(P(s'|s,a)*v(s'))
    private double expectedValue(ArrayList<Double[][]> transfer, FundState current, HashMap<FundState, Double> previous, 
            Integer[] action) {
        double sum = 0;
        Integer[] states1 = current.states;
        for (FundState s: previous.keySet()) {
            Integer[] states2 = s.states;
            double prob = 1;
            for (int i = 0; i < states1.length; i ++) {
                prob *= transfer.get(i)[states1[i]+action[i]][states2[i]];
            }
            sum = sum + prob * previous.get(s);
        }
        return sum;
    }
    
    private boolean isValidFund(FundState fund) {
        int totalFund = 0;
        for(int i=0; i<fund.states.length; i++){
        	totalFund = totalFund + fund.states[i];
        }
    	
    	if(totalFund<=maxFund){
        	return true;
        }else{
            return false;
        }
    }
    
    // return true only if sum(action) < maxAdd && sum(newState) < maxFund
    private boolean isValidAction(FundState state, Integer[] action) {
        // TODO Auto-generated method stub
    	//maxAdd, maxFund
    	int actSum=0;
    	int totalSum=0;
    	for (int i=0;i<action.length; i++){
    		actSum+=action[i];
    		totalSum+=action[i]+state.states[i];
    	}
    	if(actSum>maxAdd||totalSum>maxFund){
    		return false;
    	}
        return true;
    }
    
/*    // return true if the |values - previousValues| is small enough
    private boolean converge(HashMap<FundState, Double> previous, HashMap<FundState, Double> current) {
        double diff = 0;
        for (FundState s: previous.keySet()) {
            diff = diff + Math.abs(previous.get(s) - current.get(s));
        }
        return diff <= 0.0000001;
    }
*/    
    // write policy to a file: state; action
    private void printPolicy() throws IOException {
        FileWriter output = new FileWriter("policy.txt");
        for (FundState s: this.policy.keySet()) {
            output.write(toStr(s.states));
            Integer[] action = policy.get(s);
            output.write("; " + toStr(action) + "; ");
            // the result after the action
            Integer[] result = new Integer[action.length];
            for (int i = 0; i < action.length; i++) {
                result[i] = s.states[i] + action[i];
            }
            output.write(toStr(result) + "\n");
        }
        output.close();
    }
    // write value function to a file: state; value
    private void printValue(HashMap<FundState, Double> values) throws IOException {
        FileWriter output = new FileWriter("value.txt");
        for (FundState s: values.keySet()) {
            output.write(toStr(s.states));
            output.write("; " + values.get(s).toString() + "\n");
        }
        output.close();
    }
    // convert array to string
    private String toStr(Integer[] states) {
        String result = "[";
        String sep = "";
        for (Integer i: states) {
            result += sep + i.toString();
            sep = ", ";
        }
        return result + "]";
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
    
    public FundState(List<Integer> funds) {
        states = new Integer[funds.size()];
        for (int i = 0; i < funds.size(); i++) {
            states[i] = funds.get(i);
        }
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < states.length; i++) {
            h = 31*h + states[i];
        }
        return h;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FundState) {
            FundState temp = (FundState)obj;
            Integer[] states2 = temp.states;
            for (int i = 0; i < states2.length; i++) {
                if (this.states[i] != states2[i]) return false;
            }
            return true;
        }
        return false;
    }
}
