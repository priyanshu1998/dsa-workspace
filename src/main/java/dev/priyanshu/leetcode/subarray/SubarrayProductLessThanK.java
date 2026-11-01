package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id=713, name="subarray-product-less-than-k")
public interface SubarrayProductLessThanK {
    int numSubarrayProductLessThanK(int[] nums, int k);
}

class SubarrayProductLessThanKImpl implements SubarrayProductLessThanK {

    @Override
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if(k <= 1) return 0;
        long prod = 1;

        int l=0;
        int r=0;
        int cnt = 0;

        while(l < nums.length){
            // add lending element to prod
            if(r < nums.length && nums[r]*prod < k){
                prod *= nums[r];
                r++;
            } else {
                cnt += r-l;
                if (l == r) {
                    // window is empty; nums[l] was never multiplied into prod
                    r++;
                } else {
                    prod = prod / nums[l];
                }
                l++;
            }
        }
        return cnt;
    }
}
