package solver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import problem.Matrix;
import problem.ProblemSpec;
import problem.VentureManager;

public class MonteCarloSearch {
    protected int maxFund;
    protected int maxAdd;
    protected int totalDays;
    protected double discount;
    //protected int numVenture;
    //var that sampleOrder needs
	protected ProblemSpec spec;
	protected VentureManager ventureManager;
	protected List<Matrix> probabilities;
	protected List<Double[]> rewards;
	protected Random random = new Random();
    
    public MonteCarloSearch(ProblemSpec spec) {
    	this.spec = spec;
    	ventureManager = spec.getVentureManager();
    	probabilities = spec.getProbabilities();
    	maxFund = ventureManager.getMaxManufacturingFunds();
    	maxAdd = ventureManager.getMaxAdditionalFunding();
    	totalDays = spec.getNumFortnights();
    	discount = spec.getDiscountFactor();
    }

    public List<Integer> findNext(List<Integer> manufacturingFunds, int fortnightsLeft) {
        long start = System.currentTimeMillis();
        long end = start + 20000; // 25 milliseconds for each step
        fortnightsLeft++;
        MonteCarloNode root = new MonteCarloNode(manufacturingFunds, fortnightsLeft);
        int i = 0;  // for debug only
        while (System.currentTimeMillis() < end || i < 1000) {
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
            i++;    // for debug
        }
        /*try {
            printMCTree(root, fortnightsLeft);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return root.maxScoreChild().mcstate.fundState;
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
    	if (fortnightsLeft > 0) {
    	    // immediate reward
    	    profit += getImmediateReward(temporalState);
            // random future reward
            profit += discount*simulateProfit(new MonteCarloNode(temporalState.randNextState(maxFund, maxAdd), 
                    fortnightsLeft-1));
    	}
    	/*
    	while(fortnightsLeft>0){
    		List<Integer> customerOrders = sampleCustomerOrders(manufacturingFund);
    		
    		for (int i=0;i<customerOrders.size();i++){
    			int orders = customerOrders.get(i);
    			int products = manufacturingFund.get(i);
    			int sold = Math.min(orders, products);
    			
    			profit += sold*0.6*spec.getSalePrices().get(i);
    			profit -= Math.min(0, products-orders)*0.25*spec.getSalePrices().get(i);
    			//profit *= Math.pow(discount, totalDays-fortnightsLeft);
    			manufacturingFund.set(i, manufacturingFund.get(i)-sold);
    		}
    		
    		temporalState = temporalState.randNextState(maxFund, maxAdd);
    		manufacturingFund = temporalState.fundState;
    		fortnightsLeft--;
    	}*/
    	
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
        int childVisit = 1;
        while (tempNode != null) {
            profit = profit * discount + getImmediateReward(tempNode.mcstate);
            tempNode.mcstate.visitCount++;
            //tempNode.mcstate.profit += profit;
            tempNode.mcstate.profit = (tempNode.mcstate.profit*childVisit + profit)/(childVisit+1);
            childVisit = tempNode.mcstate.visitCount;
            tempNode = tempNode.parent;
        }
    }
    
    private double getImmediateReward(MonteCarloState temporalState) {
        double profit = 0;
        for (int i = 0; i < rewards.size(); i++) {
            profit += rewards.get(i)[temporalState.fundState.get(i)];
        }
        return profit;
    }
    
    private void printMCTree(MonteCarloNode root, int num) throws IOException {
        FileWriter output = new FileWriter(String.format("mctree_%d.txt", num));
        output.write("1:" + root.toString() + "\n");
        for (MonteCarloNode child: root.children) {
            printNode(output, child, " ", 2);
        }
        output.close();
    }

    private void printNode(FileWriter output, MonteCarloNode node, String indent, int depth) throws IOException {
        output.write(String.format("%s%d:%s\n", indent, depth, node.toString()));
        if (node.children.size() > 0) {
            for (MonteCarloNode child: node.children) {
                printNode(output, child, indent+" ", depth+1);
            }
        }
    }

}
