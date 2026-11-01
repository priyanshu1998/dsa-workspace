package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

import java.util.HashMap;
import java.util.Map;

@Leetcode(id = 2537, name = "count-the-number-of-good-subarrays")
public interface CountTheNumberOfGoodSubarrays {
    long countGood(int[] nums, int k);
}

class CountTheNumberOfGoodSubarraysImpl implements CountTheNumberOfGoodSubarrays {

    private void add(Map<Integer, Integer> freq, int num){
        freq.put(num, freq.getOrDefault(num, 0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int num){
        int v = freq.get(num);
        if(v == 1){
            freq.remove(num);
        }else{
            freq.put(num, v-1);
        }
    }

    @Override
    public long countGood(int[] nums, int k) {
        int l = 0;
        long pairs = 0;
        long answer = 0;
        Map<Integer, Integer> freq = new HashMap<>();

        for(int r=0; r<nums.length; r++){

            // add leading element
            add(freq, nums[r]);
            pairs += freq.get(nums[r])-1;

            // remove trailing elements while constraint is violated
            while(pairs >= k){
                pairs -= (freq.get(nums[l])-1);
                remove(freq, nums[l]);
                l++;
            }

            // for l, condition is violated, for index in [0, l) condition is satisfied.
            answer += l;
        }

        return answer;
    }
}
