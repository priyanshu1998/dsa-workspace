package dev.priyanshu.leetcode.search.binary;

public interface SplitArrayLargestSum {
    int splitArray(int[] nums, int k);
}

class SplitArrayLargestSumImpl implements SplitArrayLargestSum {

    private int max(int[] nums){
        int M = nums[0];

        for (int num : nums) {
            M = Math.max(M, num);
        }

        return M;
    }

    private int sum(int[] nums){
        int tot = 0;

        for (int num : nums) {
            tot += num;
        }

        return tot;
    }

    private boolean isAMaximumSum(int[] nums, int k, int sum){
        int cnt = 1;
        int runningSum = 0;

        for(int num: nums){
            if(runningSum + num > sum){
                cnt++;
                runningSum = num;
            }else{
                runningSum += num;
            }
        }

        return cnt <= k;
    }

    @Override
    public int splitArray(int[] nums, int k) {
        int l = max(nums);
        int r = sum(nums);

        while(l < r){
            int mid = l + (r-l)/2;

            if(isAMaximumSum(nums, k, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}
