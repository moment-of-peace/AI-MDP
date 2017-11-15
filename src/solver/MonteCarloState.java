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

}
