package dev.priyanshu.striver;

import dev.priyanshu.annotation.Striver;

import java.util.HashMap;
import java.util.Map;

@Striver(groups = "",
        category = "Hashing & Prefix Sum",
        path = "practice/dsa/two-sum")
public interface TwoSum {
    int[] twoSum(int[] nums, int target);
}


class TwoSumImpl implements TwoSum {
    private final Map<Integer, Integer> index = new HashMap<>();


    private boolean wasFound(int n){
        return index.containsKey(n);
    }

    private int getPosition(int n){
        return index.get(n);
    }

    private void addPosition(int n, int i){
        index.put(n, i);
    }

    @Override
    public int[] twoSum(int[] nums, int target) {

        for(int i = 0; i < nums.length; i++){
            int v = target - nums[i];
            if(wasFound(v)){
                return new int[]{getPosition(v), i};
            }

            addPosition(nums[i], i);
        }

        return new int[]{-1, -1};
    }
}
