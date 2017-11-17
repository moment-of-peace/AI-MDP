package solver;
/**
 * Calculate the UCT (Upper Confidence Bound applied to trees)
 */
public class UCT {

    public static double uctValue(int totalVisit, double nodeProfit, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeProfit / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }
    
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
    }
}
