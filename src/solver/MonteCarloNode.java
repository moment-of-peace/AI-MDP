package solver;
/**
 * A node in Monte Carlo Search Tree
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
        int index = new Random().nextInt(this.children.size());
        return this.children.get(index);
    }
    public MonteCarloNode maxScoreChild() {
    	MonteCarloNode maxChild = this.children.get(0);
    	for (int i=1;i<this.children.size();i++){
    		MonteCarloNode currentNode = this.children.get(i);
    		if(currentNode.mcstate.visitCount>maxChild.mcstate.visitCount){
    			maxChild=currentNode;
    		}
    	}
    	return maxChild;
        /*return Collections.max(this.children, Comparator.comparing(c -> {
        	//change to profit
            return c.mcstate.visitCount;
        }));*/
    }
    
    

    @Override
    // format: [fund1,fund2,...](v:vistCount, p:profit)(L: fortnigthsLeft)
    public String toString() {
        return String.format("%s(L:%d)", mcstate.toString(), fortnightsLeft);
    }
}
