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
    private double discount;
    private int ventureNum;
    private int maxFund;
    private HashMap<FundState, Integer[]> policy;
    private ArrayList<Double[]> rewards;
    
    public MySolver(ProblemSpec spec) throws IOException {
        this.spec = spec;
        ventureManager = spec.getVentureManager();
        probabilities = spec.getProbabilities();
        discount = spec.getDiscountFactor();
        ventureNum = ventureManager.getNumVentures();
        maxFund = ventureManager.getMaxManufacturingFunds();
        policy = new HashMap<FundState, Integer[]>();
        rewards = new ArrayList<Double[]>();
    }
    
    public void doOfflineComputation() {
        rewards = getRewards();
        HashMap<FundState, Double> previousValues = getInitValues(ventureNum, maxFund);
        HashMap<FundState, Double> currentValues = new HashMap<FundState, Double>();
        ArrayList<Double[][]> transfer = getTransMatrix();
        do {
            valueIteration(previousValues, currentValues, transfer);
            previousValues = currentValues;
        } while (!converge(previousValues, currentValues));
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
        // TODO Auto-generated method stub
        return null;
    }
    
    // immediate max reward of each fund state 
    private ArrayList<Double[]> getRewards() {
        // TODO Auto-generated method stub
        return null;
    }

    private void valueIteration(HashMap<FundState, Double> previous, HashMap<FundState, Double> current, 
            ArrayList<Double[][]> transfer) {
        // TODO Auto-generated method stub
        for (FundState s: current.keySet()) {
            double maxValue = Double.NEGATIVE_INFINITY;
            double reward = 0;
            for (int i = 0; i < s.states.length; i++) {
                reward += this.rewards.get(i)[s.states[i]];
            }
            for (Integer[] action: allActions(s)) {
                double newValue = reward + this.discount*expectedValue(transfer, s, previous, action);
                if (newValue > maxValue) maxValue = newValue;
            }
            current.put(s, maxValue);
        }
    }
    // find all valid actions based on current fund state
    private ArrayList<Integer[]> allActions(FundState s) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return false;
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
    
    public FundState(ArrayList<Integer> funds) {
        states = new Integer[funds.size()];
        for (int i = 0; i < funds.size(); i++) {
            states[i] = funds.get(i);
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
