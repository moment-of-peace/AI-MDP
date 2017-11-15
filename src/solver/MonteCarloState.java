package solver;
/**
 * Each node in the Monte Carlo Search Tree has a particular state of this problem. A state consists
 * of the current funds of each venture, the number times this node has been visited in the tree, and
 * the simulated profit of this node so far.
 */
import java.util.ArrayList;
import java.util.List;

public class MonteCarloState {
    protected List<Integer> fundState;
    protected int visitCount;
    protected double profit;

    public MonteCarloState(List<Integer> manufacturingFunds) {
        this.fundState = new ArrayList<Integer>(manufacturingFunds);
    }

    public List<MonteCarloState> allPossibleStates(int maxFund, int maxAdd) {
        List<Integer> actionLimit = new ArrayList<Integer>();
        for (int i = 0; i < this.fundState.size(); i++) {
            actionLimit.add(maxFund-this.fundState.get(i));
        }
        List<List<Integer>> allActions = findAllActions(actionLimit, maxAdd, actionLimit.size());
        List<MonteCarloState> nextStates = new ArrayList<MonteCarloState>();
        for (List<Integer> action: allActions) {
            nextStates.add(applyAction(action));
        }
        return nextStates;
    }

    private MonteCarloState applyAction(List<Integer> action) {
        List<Integer> newState = new ArrayList<Integer>();
        for (int i = 0; i < action.size(); i++) {
            newState.add(this.fundState.get(i) + action.get(i));
        }
        return new MonteCarloState(newState);
    }

    private List<List<Integer>> findAllActions(List<Integer> actionLimit, int maxAdd, int size) {
        List<List<Integer>> allActions = new ArrayList<List<Integer>>();
        int index = size - 1;   // ?
        if (size == 1) {
            for (int i = 0; i < Math.min(actionLimit.get(index), maxAdd); i++) {
                List<Integer> action = new ArrayList<Integer>();
                action.add(i);
                allActions.add(action);
                //maxAdd--;
            }
        }
        else {
            for (int i = 0; i < Math.min(actionLimit.get(i), maxAdd); i++) {
                for (List<Integer> action: findAllActions(actionLimit, maxAdd-i, size-1)) {
                    List<Integer> action2 = new ArrayList<Integer>(action); // necessary?
                    action2.add(i);
                    allActions.add(action2);
                }
            }
        }
        return allActions;
    }
    
    public MonteCarloState randNextState(int maxFund, int maxAdd) {
        List<Integer> actionLimit = new ArrayList<Integer>();
        for (int i = 0; i < this.fundState.size(); i++) {
            actionLimit.add(maxFund-this.fundState.get(i));
        }
        
        maxAdd = getMaxAdd(maxAdd);
        return null;
    }

    private int getMaxAdd(int maxAdd) {
        // TODO Auto-generated method stub
        return 0;
    }

}
