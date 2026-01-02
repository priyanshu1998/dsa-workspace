package dev.priyanshu.leetcode;

import java.util.HashMap;
import java.util.Map;

public interface MaximumGoodSubarraySum {
  long maximumSubarraySum(int[] a, int k);
}

class MaximumGoodSubarraySumImpl implements MaximumGoodSubarraySum {
  private void updateMinPrefixSum(Map<Integer, Long> prefixSum, int aj, long runningSum) {
    prefixSum.compute(aj, (k, v1) -> (v1 != null) ? Math.min(runningSum, v1) : runningSum);
  }

  @Override
  public long maximumSubarraySum(int[] a, int k) {
    Map<Integer, Long> prefixSum = new HashMap<>();

    long ans = -99999999900001L;

    long sum = 0;
    for (int aj : a) {
      int ai1 = aj - k;
      int ai2 = aj + k;

      sum += aj;

      if (prefixSum.containsKey(ai1)) {
        ans = Math.max(ans, sum - prefixSum.get(ai1));
      }
      if (prefixSum.containsKey(ai2)) {
        ans = Math.max(ans, sum - prefixSum.get(ai2));
      }

      updateMinPrefixSum(prefixSum, aj, sum - aj);
    }

    return ans != -99999999900001L ? ans : 0;
  }
}
