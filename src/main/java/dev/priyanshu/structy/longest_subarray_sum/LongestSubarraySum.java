package dev.priyanshu.structy.longest_subarray_sum;

public interface LongestSubarraySum {
  int longestSubarraySum(int[] nums, long target);
}

class NaiveSolution implements LongestSubarraySum {

  private long getSubarraySum(int[] nums, int l, int r) {
    long sum = 0;
    for (int i = l; i < r; i++) {
      sum += nums[i];
    }

    return sum;
  }

  @Override
  public int longestSubarraySum(int[] nums, long target) {
    int maxLength = 0;

    for (int i = 0; i < nums.length; i++) {
      for (int j = 1; j <= nums.length; j++) {
        long sum = getSubarraySum(nums, i, j);

        if (sum == target) {
          maxLength = Math.max(maxLength, j - i);
        } else if (sum > target) {
          break;
        }
      }
    }

    return maxLength != 0 ? maxLength : -1;
  }
}

class OptimizedSolution implements LongestSubarraySum {

  @Override
  public int longestSubarraySum(int[] nums, long target) {
    int maxLength = 0;

    int l = 0;
    int r = 0;

    long sum = 0;
    while (r < nums.length) {
      sum += nums[r];

      while (sum > target) {
        sum -= nums[l];
        l++;
      }

      if (sum == target) {
        maxLength = Math.max(maxLength, r - l + 1);
      }
      r++;
    }

    return maxLength != 0 ? maxLength : -1;
  }
}
