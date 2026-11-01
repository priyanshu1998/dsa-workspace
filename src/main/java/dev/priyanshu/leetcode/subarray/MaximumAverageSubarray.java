package dev.priyanshu.leetcode.subarray;


public interface MaximumAverageSubarray {
    double findMaxAverage(int[] nums, int k);
}


class MaximumAverageSubarrayImpl implements MaximumAverageSubarray {

    private long sum(int[] nums, int k){
        long total = 0;
        for(int i=0; i<k; i++){
            total += nums[i];
        }

        return total;
    }

    @Override
    public double findMaxAverage(int[] nums, int k) {
        long windowSum = sum(nums, k);
        long maxSum = windowSum;

        for(int i=k; i<nums.length; i++){
            windowSum = windowSum - nums[i-k] + nums[i];
            maxSum = Math.max(maxSum, windowSum);
        }

        return (double) maxSum / k;
    }
}
