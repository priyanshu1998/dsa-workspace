package dev.priyanshu.structy.maximum_subarray_product;

public interface MaximumSubarrayProduct {
    long maximumSubarrayProduct(long[] nums, int k);
}


class Solution implements MaximumSubarrayProduct {

    @Override
    public long maximumSubarrayProduct(long[] nums, int k) {
        long product = 1;

        int l = 0;
        int r = k;

        long prod = 1;
        for(int i=0; i<k; i++){
            prod = prod * nums[i];
        }

        long max = prod;

        while(r<nums.length){
            prod = prod / nums[l];
            prod = prod * nums[r];

            max = Math.max(max, prod);
            l++;
            r++;
        }

        return max;
    }
}
