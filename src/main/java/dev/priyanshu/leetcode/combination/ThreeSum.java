package dev.priyanshu.leetcode.combination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ThreeSum {
    List<List<Integer>> threeSum(int[] nums);
}

record Triplet(int nums_a, int nums_b, int nums_c) {
    long sum() {
        return ((long) nums_a) + nums_b + nums_c;
    }
}



class ThreeSumImpl implements ThreeSum {
    Set<Triplet> bag = new HashSet<>();

    @Override
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);

        for(int i = 0; i<nums.length; i++){
            int l = i+1;
            int r = nums.length-1;

            while (l<r) {
                if (nums[i] + nums[l] + nums[r] == 0) {
                    bag.add(new Triplet(nums[i], nums[l], nums[r]));
                    l++; r--;
                } else if (nums[i] + nums[l] + nums[r] < 0){
                    l++;
                } else {
                    r--;
                }
            }
        }

        return bag.stream()
                .map(q -> List.of(q.nums_a(), q.nums_b(), q.nums_c()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
