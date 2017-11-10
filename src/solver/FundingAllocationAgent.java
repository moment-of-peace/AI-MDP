package solver;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import java.util.List;

/** A generic interface for the automatic funding allocation system  */
public interface FundingAllocationAgent {

    /** 
     * Perform any computations that should be performed offline,
     * before the simulation begins
     */
    public void doOfflineComputation();
    
    /** 
     * Represents the policy of this solver. Given the current manufacturing
     * funding allocation and number of fortnights to go, this method should
     * return a valid additional funding allocation.
     * @param manufacturingFunds the allocation of funds to each ventures
     *                           manufacturing fund
     * @param numFortnightsLeft the number of fortnights left after this one;
     *                     a value of 0 means this is the last fortnight.
     * @return 
     */
    public List<Integer> generateAdditionalFundingAmounts(List<Integer> manufacturingFunds,
                                                          int numFortnightsLeft);
}
