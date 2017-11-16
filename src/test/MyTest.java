package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import problem.ProblemSpec;
import solver.*;

public class MyTest {

    static int maxFund = 3;
    static int maxAdd = 3;
    
    public static void main(String[] args) throws IOException {
        ProblemSpec spec = new ProblemSpec("testcases/bronze2.txt");
        
        List<Integer> state1 = new ArrayList<Integer>();
        //state1.add(0);
        state1.add(0);
        state1.add(0);
        
        MonteCarloState mcState = new MonteCarloState(state1);
        List<MonteCarloState> allstates = mcState.allPossibleStates(maxFund, maxAdd);
        
        for (MonteCarloState s: allstates) {
            System.out.println(s + " " + isValidFund(s));
        }
        int i = 0;
        System.out.println(allstates.size());
        while (i<100) {
            MonteCarloState randnext = mcState.randNextState(maxFund, maxAdd);
            System.out.println(randnext);
            if (!isValidFund(randnext)) {
                System.out.println(randnext);
            }
            i++;
        }
        System.out.println(i);
        MonteCarloSearch mcts = new MonteCarloSearch(spec);
        List<Integer> state = mcts.findNext(state1, 10);
    }
    
    private static boolean isValidFund(MonteCarloState fund) {
        int totalFund = 0;
        for(int i=0; i<fund.fundState.size(); i++){
            totalFund = totalFund + fund.fundState.get(i);
        }
        
        if(totalFund<=maxFund){
            return true;
        }else{
            return false;
        }
    }

}
