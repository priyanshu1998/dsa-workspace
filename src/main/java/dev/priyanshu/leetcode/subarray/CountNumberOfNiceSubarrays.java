package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

import java.util.HashMap;
import java.util.Map;

@Leetcode(id = 1248, name = "count-number-of-nice-subarrays")
public interface CountNumberOfNiceSubarrays {
    int numberOfSubarrays(int[] nums, int k);
}

class CountNumberOfNiceSubarraysImpl implements CountNumberOfNiceSubarrays {

    private final Map<Integer, Integer> lastEvenCache = new HashMap<>();

    private int lastEven(int[] nums, int r){
        Integer cached = lastEvenCache.get(r);
        if(cached != null){
            return cached;
        }
        int result = r;
        while(result < nums.length && nums[result] % 2 == 0){
            result++;
        }
        lastEvenCache.put(r, result);
        return result;
    }

    @Override
    public int numberOfSubarrays(int[] nums, int k) {
        int l=0;
        int r=0;

        int result = 0;

        int oddCount = 0;
        while(l<nums.length){
            if(r < nums.length && oddCount < k){
                if(nums[r]%2 == 1){
                    oddCount+=1;
                }
                r++;
            }else{
                if(oddCount == k){
                    int p = lastEven(nums, r);
                    result += (p-1)-(r-1)+1;
                }
                if(nums[l]%2==1){
                    oddCount-=1;
                }
                l++;
            }
        }
        return result;
    }
}
