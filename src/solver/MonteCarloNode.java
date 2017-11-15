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
    
    public MonteCarloNode(MonteCarloNode node){
    	//Not sure about the constructor
    	this.mcstate = new MonteCarloState(node.mcstate.fundState);
    	if(node.parent!=null){
    		//change when node.parent change?
    		this.parent=node.parent;
    	}
    	//int 
    	this.fortnightsLeft = node.fortnightsLeft;
    	//similar deep copy?
    	this.children = new ArrayList<MonteCarloNode>(node.children);
    }
    public MonteCarloNode(List<Integer> manufacturingFunds, int fortnightsLeft) {
        this.mcstate = new MonteCarloState(manufacturingFunds);
        this.fortnightsLeft = fortnightsLeft;
        children = new ArrayList<MonteCarloNode>();
    }

    public MonteCarloNode(MonteCarloState state, int fortNights) {
        this.mcstate = state;
        this.fortnightsLeft = fortNights;
        children = new ArrayList<MonteCarloNode>();
    }

    public MonteCarloNode getRandomChild() {
        // TODO Auto-generated method stub
        return null;
    }

}
