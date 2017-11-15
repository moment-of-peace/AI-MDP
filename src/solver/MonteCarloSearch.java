package solver;

import java.util.List;

public class MonteCarloSearch {
    protected int maxFund;
    protected int maxAdd;
    protected int numVenture;
    
    public MonteCarloSearch() {
        
    }

    public List<Integer> findNext(List<Integer> manufacturingFunds, int fortnightsLeft) {
        long start = System.currentTimeMillis();
        long end = start + 25000; // 25 milliseconds for each step
        
        MonteCarloNode root = new MonteCarloNode(manufacturingFunds, fortnightsLeft);
        while (System.currentTimeMillis() < end) {
            // selection
            MonteCarloNode likelyNode = selectNode(root);
            // expand the selected node
            if (likelyNode.fortnightsLeft > 0) {
                expandNode(likelyNode);
            }
            // simulation
            MonteCarloNode nodeToExplore = likelyNode;
            if (likelyNode.children.size() > 0) {
                nodeToExplore = likelyNode.getRandomChild();
            }
            double profit = simulateProfit(nodeToExplore);
            // back-propagation update
            backPropagation(nodeToExplore, profit);
        }
        return null;
    }

    private MonteCarloNode selectNode(MonteCarloNode root) {
        // TODO Auto-generated method stub
    	MonteCarloNode node = root;
        while (node.children.size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(MonteCarloNode node) {
        List<MonteCarloState> possibleStates = node.mcstate.allPossibleStates(maxFund, maxAdd);
        int fortNights = node.fortnightsLeft - 1;
        
        possibleStates.forEach(state -> {
            MonteCarloNode newNode = new MonteCarloNode(state, fortNights);
            newNode.parent = node;
            //newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.children.add(newNode);
        });
    }

    private double simulateProfit(MonteCarloNode nodeToExplore) {
        // TODO Auto-generated method stub
    	MonteCarloNode temporalNode = new MonteCarloNode(nodeToExplore);
    	//will change with coming state change
    	MonteCarloState temporalState = temporalNode.mcstate;
    	List<Integer> manufacturingFund = temporalState.fundState;
    	
    	double profit = 0;

        return 0;
    }

    private void backPropagation(MonteCarloNode nodeToExplore, double profit) {
        MonteCarloNode tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.mcstate.visitCount++;
            tempNode.mcstate.profit += profit;
            tempNode = tempNode.parent;
        }
    }

}
