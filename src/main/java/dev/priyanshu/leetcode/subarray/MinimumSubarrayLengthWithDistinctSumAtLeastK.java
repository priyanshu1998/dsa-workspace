package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface MinimumSubarrayLengthWithDistinctSumAtLeastK {
    int minLength(int[] nums, int k);
}

class MinimumSubarrayLengthWithDistinctSumAtLeastKImpl implements MinimumSubarrayLengthWithDistinctSumAtLeastK {
    @Override
    public int minLength(int[] nums, int k) {
        int n = nums.length;
        int minLen = Integer.MAX_VALUE;
        int left = 0;
        int distinctSum = 0;
        Map<Integer, Integer> freq = new HashMap<>();

        for (int right = 0; right < n; right++) {
            // a value only contributes to distinctSum the first time it enters the window
            if (freq.merge(nums[right], 1, Integer::sum) == 1) {
                distinctSum += nums[right];
            }

            while (distinctSum >= k) {
                minLen = Math.min(minLen, right - left + 1);
                // a value is removed from distinctSum only once its last copy leaves the window
                if (freq.merge(nums[left], -1, Integer::sum) == 0) {
                    distinctSum -= nums[left];
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }
}
