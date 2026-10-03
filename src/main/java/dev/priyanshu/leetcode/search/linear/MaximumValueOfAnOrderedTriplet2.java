package dev.priyanshu.leetcode.search.linear;

public interface MaximumValueOfAnOrderedTriplet2 {
    long maximumTripletValue(int[] nums);
}

class MaximumValueOfAnOrderedTriplet2Impl implements MaximumValueOfAnOrderedTriplet2 {

    @Override
    public long maximumTripletValue(int[] nums) {
        int[] prefixMax = new int[nums.length];
        int[] suffixMax = new int[nums.length];

        for(int i=0; i<nums.length; i++){
            prefixMax[i] = i!=0?Math.max(prefixMax[i-1], nums[i]):nums[i];
        }

        for(int j=nums.length-1; j>=0; j--){
            suffixMax[j] = j!=nums.length-1?Math.max(suffixMax[j+1], nums[j]):nums[j];
        }

        long max = 0;
        for(int j=1; j<nums.length-1; j++){
            max = Math.max(max, (long) (prefixMax[j - 1] - nums[j]) * suffixMax[j + 1]);
        }

        return max;
    }
}
