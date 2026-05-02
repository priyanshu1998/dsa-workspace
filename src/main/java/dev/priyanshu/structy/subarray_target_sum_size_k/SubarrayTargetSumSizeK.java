package dev.priyanshu.structy.subarray_target_sum_size_k;

import dev.priyanshu.annotation.Structy;

@Structy(tag = "sliding-window")
public interface SubarrayTargetSumSizeK {
  int subarrayTargetSumSizeK(int[] nums, long target, int k);
}

class OptimizedSolution implements SubarrayTargetSumSizeK {

  @Override
  public int subarrayTargetSumSizeK(int[] nums, long target, int k) {
    long sum = 0L;
    for (int i = 0; i < k; i++) {
      sum += nums[i];
    }

    int cnt = (sum == target) ? 1 : 0;

    int l = 0;
    int r = k;

    while (r < nums.length) {
      sum = sum - nums[l] + nums[r];
      if (sum == target) cnt++;
      l++;
      r++;
    }

    return cnt;
  }
}
