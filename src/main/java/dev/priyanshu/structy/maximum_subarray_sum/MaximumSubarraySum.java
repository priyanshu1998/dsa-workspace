package dev.priyanshu.structy.maximum_subarray_sum;

import dev.priyanshu.annotation.Structy;

@Structy(tag="sliding-window")
public interface MaximumSubarraySum {
  long maximumSubarraySum(int[] nums, int k);
}

class NaiveSolution implements MaximumSubarraySum {
  private long subarraySum(int[] nums, int start, int k) {
    long sum = 0;
    for (int i = 0; i < k; i++) {
      var idx = start + i;
      sum += nums[idx];
    }

    return sum;
  }

  @Override
  public long maximumSubarraySum(int[] nums, int k) {
    long M = 0;
    for (int i = 0; i <= nums.length - k; i++) {
      M = Math.max(M, subarraySum(nums, i, k));
    }
    return M;
  }
}

class RuntimeOptimizedSolution implements MaximumSubarraySum {

  private long subarraySum(long[] prefixSum, int start, int k) {
    int end = start + k - 1;
    return prefixSum[end] - (start > 0 ? prefixSum[start - 1] : 0L);
  }

  @Override
  public long maximumSubarraySum(int[] nums, int k) {
    long[] prefixSum = new long[nums.length];

    prefixSum[0] = nums[0];
    for (int i = 1; i < nums.length; i++) {
      prefixSum[i] = prefixSum[i - 1] + nums[i];
    }

    long M = 0;
    for (int i = 0; i <= nums.length - k; i++) {
      M = Math.max(M, subarraySum(prefixSum, i, k));
    }

    return M;
  }
}

class OptimizedSolution implements MaximumSubarraySum {

  @Override
  public long maximumSubarraySum(int[] nums, int k) {
    long sum = 0;
    for (int i = 0; i < k; i++) {
      sum += nums[i];
    }

    // subarray sum = sum(a[l:r)) includes l excludes r
    int l = 0;
    int r = k;

    long M = sum;
    while (r < nums.length) {
      sum = sum - nums[l] + nums[r];
      M = Math.max(M, sum);
      l++;
      r++;
    }

    return M;
  }
}
