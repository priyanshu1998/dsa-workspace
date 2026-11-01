package dev.priyanshu.leetcode.subarray;


import java.util.HashMap;
import java.util.Map;

public interface ContainsDuplicate2 {
    boolean containsNearbyDuplicate(int[] nums, int k);
}

class ContainsDuplicate2Impl implements ContainsDuplicate2 {

    private static void add(Map<Integer, Integer> freq, int v){
        freq.put(v, freq.getOrDefault(v, 0) + 1);
    }

    private static void remove(Map<Integer, Integer> freq, int key){
        int v = freq.get(key);

        if(v == 1){
            freq.remove(key);
        }else{
            freq.put(key, v-1);
        }
    }

    private static void initialWindow(int[] nums, int k, Map<Integer, Integer> freq){
        for(int i=0; i<k+1; i++){
            add(freq, nums[i]);
        }
    }

    private static boolean isMoreThanOnePresent(Map<Integer, Integer> freq, int k){
        return freq.size() != k+1;
    }



    @Override
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();

        initialWindow(nums, k, freq);
        System.out.println(freq);

        if(isMoreThanOnePresent(freq, k)) return true;


        for(int i=k+1; i < nums.length; i++){
            remove(freq, nums[i-k-1]);
            add(freq, nums[i]);
            System.out.println(freq);

            if(isMoreThanOnePresent(freq, k)) return true;
        }
        return false;
    }
}
