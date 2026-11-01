package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

public interface MaxConsecutiveOnes3 {
    int longestOnes(int[] nums, int k);
}

@Leetcode(id=1004, name="max-consecutive-ones-iii")
class MaxConsecutiveOnes3Impl implements MaxConsecutiveOnes3 {

    @Override
    public int longestOnes(int[] nums, int k) {
        int l = 0;
        int r = 0;

        int result = 0;


        while(r<nums.length){
            if(nums[r] == 1){
                r++;
                result = Math.max(result, r-l);
            }else if(k > 0){
                r++;
                k--;
                result = Math.max(result, r-l);
            }else{
                if(nums[l] == 0){
                    k++;
                }
                l++;
            }
        }

        return result;
    }
}
