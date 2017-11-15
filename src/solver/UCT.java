package solver;
/**
 * Calculate the UCT (Upper Confidence Bound applied to trees)
 */
import java.util.Collections;
import java.util.Comparator;

public class UCT {

    public static double uctValue(int totalVisit, double nodeProfit, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeProfit / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    static MonteCarloNode findBestNodeWithUCT(MonteCarloNode node) {
        int parentVisit = node.mcstate.visitCount;
        return Collections.max(
          node.children,
          Comparator.comparing(c -> uctValue(parentVisit, c.mcstate.profit, c.mcstate.visitCount)));
    }
}
