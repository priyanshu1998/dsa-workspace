package dev.priyanshu.leetcode.subarray;

public interface BestTimeToBuyAndSellStockUsingStrategy {
    long maxProfit(int[] prices, int[] strategy, int k);
}

class BestTimeToBuyAndSellStockUsingStrategyImpl implements BestTimeToBuyAndSellStockUsingStrategy {

    private long allSum(int[] prices, int[] strategy){
        long tot = 0;
        for(int i=0; i<prices.length; i++){
            tot += (long) prices[i] * strategy[i];
        }
        return tot;
    }

    private long initialResetWindow(int []prices, int[] strategy,  int k){
        long tot = 0;

        for(int i=0; i<k; i++){
            tot += (long) prices[i] * strategy[i];
        }
        return tot;
    }

    private long initialSetWindow(int[] prices, int k){
        long tot = 0;
        for(int i = k/2; i<k; i++){
            tot += prices[i];
        }
        return tot;
    }

    @Override
    public long maxProfit(int[] prices, int[] strategy, int k) {
        var allSum = allSum(prices, strategy);

        var optimal = allSum;
        long resetWindowSum =  initialResetWindow(prices, strategy, k);
        long setWindowSum = initialSetWindow(prices, k);

        long windowSum = allSum - resetWindowSum + setWindowSum;

        optimal = Math.max(optimal, windowSum);

        for(int i=k; i<prices.length; i++){
            resetWindowSum = resetWindowSum + ((long) prices[i] *strategy[i]) - ((long) prices[i-k] *strategy[i-k]);
            setWindowSum = setWindowSum + prices[i] - prices[i-k/2];
            windowSum = allSum - resetWindowSum + setWindowSum;

            optimal = Math.max(optimal, windowSum);
        }

        return optimal;
    }
}
