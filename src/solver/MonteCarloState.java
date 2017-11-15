package solver;
/**
 * Each node in the Monte Carlo Search Tree has a particular state of this problem. A state consists
 * of the current funds of each venture, the number times this node has been visited in the tree, and
 * the simulated profit of this node so far.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloState {
    protected List<Integer> fundState;
    protected int visitCount;
    protected double profit;

    public MonteCarloState(List<Integer> manufacturingFunds) {
        this.fundState = new ArrayList<Integer>(manufacturingFunds);
    }

    // return all possible next state based on current state
    public List<MonteCarloState> allPossibleStates(int maxFund, int maxAdd) {
        //List<Integer> actionLimit = getActionLimit(maxFund);
        maxAdd = getMaxAdd(maxFund, maxAdd);
        List<List<Integer>> allActions = findAllActions(maxAdd, this.fundState.size());
        List<MonteCarloState> nextStates = new ArrayList<MonteCarloState>();
        // apply all possible actions to current fund state
        for (List<Integer> action: allActions) {
            nextStates.add(applyAction(action));
        }
        return nextStates;
    }
    // find all valid actions can be applied to the current fund state
    private List<List<Integer>> findAllActions(int maxAdd, int size) {
        List<List<Integer>> allActions = new ArrayList<List<Integer>>();
        if (size == 1) {
            for (int i = 0; i < maxAdd; i++) {
                List<Integer> action = new ArrayList<Integer>();
                action.add(i);
                allActions.add(action);
            }
        }
        else {
            for (int i = 0; i < maxAdd; i++) {
                for (List<Integer> action: findAllActions(maxAdd-i, size-1)) {
                    List<Integer> action2 = new ArrayList<Integer>(action); // necessary?
                    action2.add(i);
                    allActions.add(action2);
                }
            }
        }
        return allActions;
    }
    
    // return a random next state based on current state
    public MonteCarloState randNextState(int maxFund, int maxAdd) {
        //List<Integer> actionLimit = getActionLimit(maxFund);
        maxAdd = getMaxAdd(maxFund, maxAdd);
        List<Integer> action = findRandNext(maxAdd, this.fundState.size());
        return applyAction(action);
    }
    // randomly find a valid action which can be applied to current fund state
    private List<Integer> findRandNext(int maxAdd, int size) {
        int limit = maxAdd + 1;
        Random rand = new Random();
        List<Integer> action;
        if (size == 1) {
            action = new ArrayList<Integer>();
            action.add(rand.nextInt(limit));
        }
        else {
            int fund = rand.nextInt(limit);
            action = new ArrayList<Integer>(findRandNext(maxAdd-fund, size-1)); // necessary?
            action.add(fund);
        }
        return action;
    }

    // based on the max funding and max additional funding, return the limit of action
    private int getMaxAdd(int maxFund, int maxAdd) {
        int sum = 0;
        for (int i: this.fundState) {
            sum += i;
        }
        int rest = maxFund - sum;
        return rest<maxAdd ? rest:maxAdd;
    }
    
    // return the result after applying an action to current fund state
    private MonteCarloState applyAction(List<Integer> action) {
        List<Integer> newState = new ArrayList<Integer>();
        for (int i = 0; i < action.size(); i++) {
            newState.add(this.fundState.get(i) + action.get(i));
        }
        return new MonteCarloState(newState);
    }

}
