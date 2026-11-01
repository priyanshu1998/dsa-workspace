package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface CountSubarraysWithKDistinctIntegers {
    long countSubarrays(int[] nums, int k, int m);
}

class CountSubarraysWithKDistinctIntegersImpl implements CountSubarraysWithKDistinctIntegers {

    private void add(Map<Integer, Integer> freq, int num){
        freq.put(num, freq.getOrDefault(num, 0) + 1);
    }

    private void remove(Map<Integer, Integer> freq, int num){
        int v = freq.get(num);
        if(v == 1){
            freq.remove(num);
        }else{
            freq.put(num, v-1);
        }
    }

    private long countSubarraysAtLeastKDistinct(int[] nums, int k, int m) {
        Map<Integer, Integer> freq = new HashMap<>();
        int satified = 0;
        int l = 0;
        long ways = 0;

        for(int r=0; r<nums.length; r++){
            add(freq, nums[r]);
            if(freq.get(nums[r]) == m) satified++;

            while(freq.size() >= k && satified == k){
                if(freq.get(nums[l]) == m) satified--;
                remove(freq, nums[l]);

                l++;
            }
            ways += l;
        }
        return ways;
    }

    @Override
    public long countSubarrays(int[] nums, int k, int m) {
        return countSubarraysAtLeastKDistinct(nums, k, m) - countSubarraysAtLeastKDistinct(nums, k+1, m);
    }
}
