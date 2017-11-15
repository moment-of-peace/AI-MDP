package solver;
/**
 * A node in Monte Carlo Search Tree
 */
import java.util.ArrayList;
import java.util.List;

public class MonteCarloNode {
    protected MonteCarloState mcstate;
    protected MonteCarloNode parent;
    protected List<MonteCarloNode> children;
    protected int fortnightsLeft;
    
    public MonteCarloNode(List<Integer> manufacturingFunds, int fortnightsLeft) {
        this.mcstate = new MonteCarloState(manufacturingFunds);
        this.fortnightsLeft = fortnightsLeft;
        children = new ArrayList<MonteCarloNode>();
    }

    public MonteCarloNode getRandomChild() {
        // TODO Auto-generated method stub
        return null;
    }

}
