package dev.priyanshu.leetcode.subarray;

public interface MinimumOperationsToReduceXToZero {
    int minOperations(int[] nums, int x);
}

class MinimumOperationsToReduceXToZeroImpl implements MinimumOperationsToReduceXToZero {

    private long total(int[] nums){
        long tot = 0;
        for(int num:nums){
            tot += num;
        }

        return tot;
    }
    @Override
    public int minOperations(int[] nums, int x) {
        int k = Math.toIntExact(total(nums) - x);

        int l = 0;
        int r = 0;
        int ans = nums.length+1;
        long windowSum = 0;

        while(l < nums.length){
            // add leading element if updated windowSum is less than k else remove trailing element
            if(r < nums.length && windowSum + nums[r] <= k){
                windowSum += nums[r];
                r++;
            }else{
                if(windowSum == k){
                    ans = Math.min(ans, nums.length - (r-l));
                }
                windowSum -= nums[l];
                l++;
            }
        }

        return ans == nums.length+1 ? -1 : ans;
    }
}
