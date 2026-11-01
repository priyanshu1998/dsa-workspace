package dev.priyanshu.leetcode.subarray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FindThePowerOfKSizeSubarrays2 {
    int[] resultsArray(int[] nums, int k);
}

class FindThePowerOfKSizeSubarrays2Impl implements FindThePowerOfKSizeSubarrays2 {

    private void add(Map<Integer, Integer> freq, int key){
        freq.put(key, freq.getOrDefault(key, 0)+1);
    }


    private void remove(Map<Integer, Integer> freq, int key){
        int v = freq.get(key);
        if(v==1){
            freq.remove(key);
        }else{
            freq.put(key, v-1);
        }
    }

    private boolean check(Map<Integer, Integer> freq, int cnt){
        // System.out.println(freq);
        return (freq.size() == 1 && freq.containsKey(1));
    }

    public int[] resultsArray(int[] nums, int k) {
        if(k == 1) return nums;

        List<Integer> result = new ArrayList<>();
        int[] diff = new int[nums.length-1];

        for(int i=0; i<nums.length-1; i++){
            diff[i] = nums[i+1]-nums[i];
        }

        Map<Integer, Integer> freq = new HashMap<>();
        int t = k-1;
        for(int i=0; i < t; i++){
            add(freq, diff[i]);
        }
        // System.out.println(freq);

        if(check(freq, t)){
            result.add(nums[k-1]);
        }else{
            result.add(-1);
        }

        for(int i=t; i<diff.length; i++){
            remove(freq, diff[i-t]);
            add(freq, diff[i]);

            if(check(freq, t)){
                result.add(nums[i+1]);
            }else{
                result.add(-1);
            }
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
