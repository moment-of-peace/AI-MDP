package test;

import java.util.ArrayList;
import java.util.List;

import solver.*;

public class MyTest {

    static int maxFund = 8;
    static int maxAdd = 5;
    
    public static void main(String[] args) {
        
        
        List<Integer> state1 = new ArrayList<Integer>();
        state1.add(0);
        state1.add(2);
        state1.add(1);
        
        MonteCarloState mcState = new MonteCarloState(state1);
        List<MonteCarloState> allstates = mcState.allPossibleStates(maxFund, maxAdd);
        
        for (MonteCarloState s: allstates) {
            System.out.println(s + " " + isValidFund(s));
        }
        int i = 0;
        System.out.println(allstates.size());
        while (i<10000) {
            MonteCarloState randnext = mcState.randNextState(maxFund, maxAdd);
            if (!isValidFund(randnext)) {
                System.out.println(randnext);
            }
            i++;
        }
        System.out.println(i);
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
