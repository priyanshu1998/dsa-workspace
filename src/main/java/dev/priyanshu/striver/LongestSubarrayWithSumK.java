package dev.priyanshu.striver;

import java.util.HashMap;
import java.util.Map;

public interface LongestSubarrayWithSumK {
    int longestSubarray(int[] nums, int k);
}

class LongestSubarrayWithSumKImpl implements LongestSubarrayWithSumK {
    Map<Integer, Integer> firstOccurrence = new HashMap<>();

    @Override
    public int longestSubarray(int[] nums, int k) {
        firstOccurrence.put(0, -1);

        int runningSum = 0;
        int max = 0;

        for (int r = 0; r < nums.length; r++) {
            runningSum += nums[r];

            int key = runningSum - k; // key = extra
            if(firstOccurrence.containsKey(key)){
                int l = firstOccurrence.get(key);
                max = Math.max(max, r - l);
            }

            firstOccurrence.putIfAbsent(runningSum, r);
        }

        return max;
    }
}
