package dev.priyanshu.codeforces;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface SegmentsWithSmallSet {
    // good if there are no more than k
    // unique elements on this segment. Your task is to find the number of different good segments.
    long countOfGoodSegments(int n, int[] nums, long k);
}

class SegmentsWithSmallSetImpl implements SegmentsWithSmallSet {

    Map<Integer, Integer> freq = new HashMap<>();

    private void extendLeadingEnd(int num){
            freq.compute(num, (k, v) ->
                    Optional.ofNullable(v).orElse(0) + 1);
    }

    private void shrinkTailingEnd(int num){
        if(freq.get(num) == 1){
            freq.remove(num);
        }else{
            freq.compute(num, (k, v) ->
                    Optional.ofNullable(v).orElseThrow() - 1);
        }
    }


    @Override
    public long countOfGoodSegments(int n, int[] nums, long k) {
        long count = 0;

        int l = 0;
        int r = 0;

        while (r<nums.length){
            extendLeadingEnd(nums[r]);
            r++;

            while (freq.size() > k){
                shrinkTailingEnd(nums[l]);
                l++;
            }

            count += r-l;
        }

        return count;
    }
}
