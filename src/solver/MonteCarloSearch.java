package solver;

import java.util.List;

public class MonteCarloSearch {
    
    public MonteCarloSearch() {
        
    }

    public List<Integer> findNext(List<Integer> manufacturingFunds, int fortnightsLeft) {
        long start = System.currentTimeMillis();
        long end = start + 25000; // 25 milliseconds for each step
        
        MonteCarloNode root = new MonteCarloNode(manufacturingFunds, fortnightsLeft);
        while (System.currentTimeMillis() < end) {
            // selection
            MonteCarloNode node = selectNode(root);
            // expand the selected node
            if (node.fortnightsLeft > 0) {
                expandNode(node);
            }
            // simulation
            MonteCarloNode nodeToExplore = node;
            if (node.children.size() > 0) {
                nodeToExplore = node.getRandomChild();
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
        // TODO Auto-generated method stub
        
    }

    private double simulateProfit(MonteCarloNode nodeToExplore) {
        // TODO Auto-generated method stub
        return 0;
    }

    private void backPropagation(MonteCarloNode nodeToExplore, double profit) {
        // TODO Auto-generated method stub
        
    }

}
