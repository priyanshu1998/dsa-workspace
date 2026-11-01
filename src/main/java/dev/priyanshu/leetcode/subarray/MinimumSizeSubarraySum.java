package dev.priyanshu.leetcode.subarray;

public interface MinimumSizeSubarraySum {
    int minSubArrayLen(int target, int[] nums);
}

class MinimumSizeSubarraySumImpl implements MinimumSizeSubarraySum {

    @Override
    public int minSubArrayLen(int target, int[] nums) {
        int l=0;
        int r=0;

        long sum = 0;
        int len = nums.length + 1;

        while(l<nums.length){
            // add the leading element to sum
            if(r < nums.length && nums[r]+sum < target){
                sum += nums[r];
                r++;
            }else {
                // add the leading element to sum
                var v = (r == nums.length) ? 0 : nums[r];
                if (sum + v >= target) {
                    len = Math.min(len, r - l + 1);
                }

                // remove tailing element from sum
                sum -= nums[l];
                l++;
            }
        }

        return len != nums.length+1? len:0;
    }
}
