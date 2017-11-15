package solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import problem.Matrix;
import problem.ProblemSpec;
import problem.VentureManager;

public class MonteCarloSearch {
    protected int maxFund;
    protected int maxAdd;
    protected int numVenture;
    //var that sampleOrder needs
	protected ProblemSpec spec;
	protected VentureManager ventureManager;
	protected List<Matrix> probabilities;
	protected Random random = new Random();
    
    public MonteCarloSearch(ProblemSpec spec) {
    	this.spec = spec;
    	ventureManager = spec.getVentureManager();
    	probabilities = spec.getProbabilities();
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
    	
    	//initial condition
    	int fortnightsLeft = temporalNode.fortnightsLeft;
    	MonteCarloState temporalState = temporalNode.mcstate;
    	List<Integer> manufacturingFund = temporalState.fundState;
    	double profit = 0;
    	
    	while(fortnightsLeft>0){
    		List<Integer> customerOrders = sampleCustomerOrders(manufacturingFund);
    		
    		for (int i=0;i<customerOrders.size();i++){
    			int orders = customerOrders.get(i);
    			int products = manufacturingFund.get(i);
    			int sold = Math.min(orders, products);
    			
    			profit += sold*0.6*spec.getSalePrices().get(i);
    			profit -= Math.min(0, products-orders)*0.25*spec.getSalePrices().get(i);
    		}
    		
    		temporalState = temporalState.randNextState(maxFund, maxAdd);
    		manufacturingFund = temporalState.fundState;
    		fortnightsLeft--;
    	}
    	
        return profit;
    }
    
	/**
	 * Uses the currently loaded stochastic model to sample customer order demand.
	 * Note that user wants may exceed the amount in the manufacturing fund
	 * @param state The manufacturing funds allocation
	 * @return Customer orders as list of item quantities
	 */
	public List<Integer> sampleCustomerOrders(List<Integer> state) {
		List<Integer> wants = new ArrayList<Integer>();
		for (int k = 0; k < ventureManager.getNumVentures(); k++) {
			int i = state.get(k);
			List<Double> prob = probabilities.get(k).getRow(i);
			wants.add(sampleIndex(prob));
		}
		return wants;
	}
	
	/**
	 * Returns an index sampled from a list of probabilities
	 * @precondition probabilities in prob sum to 1
	 * @param prob
	 * @return an int with value within [0, prob.size() - 1]
	 */
	public int sampleIndex(List<Double> prob) {
		double sum = 0;
		double r = random.nextDouble();
		for (int i = 0; i < prob.size(); i++) {
			sum += prob.get(i);
			if (sum >= r) {
				return i;
			}
		}
		return -1;
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