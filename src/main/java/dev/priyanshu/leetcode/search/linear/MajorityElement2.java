package dev.priyanshu.leetcode.search.linear;

import java.util.ArrayList;
import java.util.List;

public interface MajorityElement2 {
    List<Integer> majorityElement(int[] nums);
}

class MajorityElement2Impl implements MajorityElement2{

    @Override
    public List<Integer> majorityElement(int[] nums) {
        var result = new ArrayList<Integer>();
        if(nums.length == 0) return result;

        // At most 2 elements can appear more than n/3 times, so track two
        // candidates simultaneously using the Boyer-Moore voting algorithm.
        int candidate1 = 0, candidate2 = 0;
        int count1 = 0, count2 = 0;

        for(int num: nums){
            if(count1 > 0 && num == candidate1){
                count1++;
            } else if(count2 > 0 && num == candidate2){
                count2++;
            } else if(count1 == 0){
                candidate1 = num;
                count1 = 1;
            } else if(count2 == 0){
                candidate2 = num;
                count2 = 1;
            } else {
                count1--;
                count2--;
            }
        }

        // Candidates may not be true majorities, so verify actual counts.
        count1 = 0;
        count2 = 0;
        for(int num: nums){
            if(num == candidate1) count1++;
            else if(num == candidate2) count2++;
        }

        if(count1 > nums.length/3) result.add(candidate1);
        if(count2 > nums.length/3) result.add(candidate2);

        return result;
    }
}

