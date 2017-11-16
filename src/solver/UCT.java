package solver;
/**
 * Calculate the UCT (Upper Confidence Bound applied to trees)
 */
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UCT {

    public static double uctValue(int totalVisit, double nodeProfit, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeProfit / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }
    
    /*static Comparator<MonteCarloNode> mcComparator = new Comparator<MonteCarloNode>(){
    	@Override
    	public int compare (MonteCarloNode node1, MonteCarloNode node2){
    		double difference = uctValue(node1.parent.mcstate.visitCount,node1.mcstate.profit,node1.mcstate.visitCount)-
    				uctValue(node2.parent.mcstate.visitCount,node2.mcstate.profit,node2.mcstate.visitCount);
    		return (int) difference;
    		
    	}
    };*/
    public static MonteCarloNode findBestNodeWithUCT(MonteCarloNode node) {
    	
    	int parentVisit = node.mcstate.visitCount;
    	MonteCarloNode maxChild=node.children.get(0);
    	
    	for (int i=1;i<node.children.size();i++){
    		MonteCarloNode currentChild = node.children.get(i);
    		if(uctValue(parentVisit,currentChild.mcstate.profit,currentChild.mcstate.visitCount)>
    		uctValue(parentVisit,maxChild.mcstate.profit,maxChild.mcstate.visitCount)){
    			maxChild=currentChild;
    		}
    	}
    	return maxChild;
        
        /*MonteCarloNode preChild;
        for (MonteCarloNode mcnode:node.children){
        	preChild = new MonteCarloNode(mcnode);
        }
        return null;*/
        /*int parentVisit = node.mcstate.visitCount;
         *return Collections.max(
          node.children,
          Comparator.comparing(c -> uctValue(parentVisit, c.mcstate.profit, c.mcstate.visitCount)));*/
    	
    	
        //return Collections.max(node.children, mcComparator);
    }
    
    
}
