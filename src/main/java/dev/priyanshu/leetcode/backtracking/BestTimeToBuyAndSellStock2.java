package dev.priyanshu.leetcode.backtracking;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 122)
public interface BestTimeToBuyAndSellStock2 {
  int maxProfit(int[] prices);
}

class BestTimeToBuyAndSellStock2Impl implements BestTimeToBuyAndSellStock2 {

  int[] prices;
  int[] dpBuy;
  int[] dpSell;

  BestTimeToBuyAndSellStock2Impl() {
    dpBuy = new int[30001];
    dpSell = new int[30001];

    for (int i = 0; i <= 30000; i++) {
      dpBuy[i] = -1;
      dpSell[i] = -1;
    }
  }

  int buy(int i) {
    if (i == prices.length) return 0;
    if (dpBuy[i] != -1) return dpBuy[i];
    return dpBuy[i] = Math.max(-prices[i] + sell(i + 1), buy(i + 1));
  }

  int sell(int i) {
    if (i == prices.length) return 0;
    if (dpSell[i] != -1) return dpSell[i];
    return dpSell[i] = Math.max(prices[i] + buy(i + 1), sell(i + 1));
  }

  public int maxProfit(int[] prices) {
    this.prices = prices;
    return buy(0);
  }
}
