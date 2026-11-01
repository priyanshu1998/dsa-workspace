package dev.priyanshu.leetcode.subarray;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public interface SlidingSubarrayBeauty {
    int[] getSubarrayBeauty(int[] nums, int k, int x);
}

class SlidingSubarrayBeautyImpl implements SlidingSubarrayBeauty {


    private void add(int v, Map<Integer, Integer> freq){
        freq.put(v, freq.getOrDefault(v, 0)+1);
    }

    private void remove(int v, Map<Integer, Integer> freq){
        int cnt = freq.get(v);
        if(cnt == 1){
            freq.remove(v);
        }else{
            freq.put(v, cnt - 1);
        }
    }

    private void initialize(int []nums, int k, Map<Integer, Integer> freq){
        for(int i=0; i<k; i++){
            if(nums[i] < 0){
                add(nums[i], freq);
            }
        }
    }

    private int getBeauty(Map<Integer, Integer> freq, int k, int x){
        int cnt = 0;
        for(int v: freq.keySet()){
            if(cnt + freq.get(v) < x){
                cnt += freq.get(v);
            }else{
                return v;
            }
        }

        return 0;
    }

    @Override
    public int[] getSubarrayBeauty(int[] nums, int k, int x) {
        var result = new ArrayList<Integer>();
        Map<Integer, Integer> freq = new TreeMap<>();

        initialize(nums, k, freq);
        result.add(getBeauty(freq, k, x));

        for(int i=k; i<nums.length; i++){
            if(nums[i-k] < 0){
                remove(nums[i-k], freq);
            }
            if(nums[i] < 0){
                add(nums[i], freq);
            }

            result.add(getBeauty(freq, k, x));
        }

        return result.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
