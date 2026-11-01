package dev.priyanshu.leetcode.subarray;

public interface BinarySubarraysWithSum {
    int numSubarraysWithSum(int[] nums, int goal);
}

class BinarySubarraysWithSumImpl implements BinarySubarraysWithSum {

    private static int zeroCount(int[] nums, int r){
        while(r < nums.length && nums[r] == 0){
            r++;
        }
        return r;
    }


    @Override
    public int numSubarraysWithSum(int[] nums, int goal) {
        int l = 0;
        int r = 0;

        int windowSum = 0;
        int ways = 0;

        while(l<nums.length){
            if(r < nums.length && windowSum < goal){
                windowSum += nums[r];
                r++;
            }else{
                if(windowSum == goal){
                    r = Math.max(r, l+1);
//                    System.out.printf("l=%d, r=%d\n", l, r);

                    int k = zeroCount(nums, r);
                    ways += k-r+1;
                }
                windowSum -= nums[l];
                l += 1;
            }
        }

        return ways;
    }
}
