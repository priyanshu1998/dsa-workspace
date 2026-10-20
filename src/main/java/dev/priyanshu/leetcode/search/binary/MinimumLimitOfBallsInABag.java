package dev.priyanshu.leetcode.search.binary;

import java.util.function.Consumer;

public interface MinimumLimitOfBallsInABag {
    int minimumSize(int[] nums, int maxOperations);
}

class MinimumLimitOfBallsInABagImpl implements MinimumLimitOfBallsInABag {

    private int maximum(int[] nums){
        int max = nums[0];
        for(int num: nums){
            max = Math.max(num, max);
        }
        return max;
    }

    private boolean check(int[] nums, int maxOperations, int mid){
        Consumer<Long> display = v -> System.out.printf("mid = %d result = %d\n", mid, v);
        long count = 0;
        for(int num: nums){
            count += (num-1) / mid;
        }
        display.accept(count);
        return count <= maxOperations;
    }

    @Override
    public int minimumSize(int[] nums, int maxOperations) {
        int l = 1;
        int r = maximum(nums);

        while(l<r){

            int mid = l + (r-l)/2;
            System.out.printf("l = %d, mid = %d, r = %d\n", l, mid, r);
            if(check(nums, maxOperations, mid)){
                r = mid;
            }else{
                l = mid + 1;
            }
        }

        return l;
    }
}
