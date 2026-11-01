package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface LengthOfLongestSubarrayWithAtMostKFrequency {
    int maxSubarrayLength(int[] nums, int k);
}

class LengthOfLongestSubarrayWithAtMostKFrequencyImpl implements LengthOfLongestSubarrayWithAtMostKFrequency {

    private int get(Map<Integer, Integer> map, int num){
        return map.getOrDefault(num, 0);
    }

    private void add(Map<Integer, Integer> map, int num){
        map.put(num, map.getOrDefault(num, 0)+1);
    }

    private void remove(Map<Integer, Integer>map, int num){
        int v = map.get(num);
        if(v == 1){
            map.remove(num);
        }else{
            map.put(num, v-1);
        }
    }

    @Override
    public int maxSubarrayLength(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();

        int len = 0;
        int l = 0;

        for(int r=0; r<nums.length; r++){
            // add the leading element to sum
            add(freq, nums[r]);

            // remove trailing elements while constraint is violated
            while(freq.get(nums[r]) > k){
                remove(freq, nums[l]);
                l++;
            }

            len = Math.max(len, r-l+1);
        }

        return len;
    }
}
