package dev.priyanshu.leetcode.search.binary;

public interface FindTheSmallestDivisorGivenAThreshold {
    int smallestDivisor(int[] nums, int threshold);
}

class FindTheSmallestDivisorGivenAThresholdImpl implements FindTheSmallestDivisorGivenAThreshold{

    private int max(int[] nums){
        int M = nums[0];
        for(int num: nums){
            M = Math.max(M, num);
        }
        return M;
    }

    private boolean check(int []nums, int threshold, int mid){
        int tot = 0;

        for(int num: nums){
            tot += (num + mid - 1) / mid;
        }

        return tot <= threshold;
    }

    @Override
    public int smallestDivisor(int[] nums, int threshold) {
        int l = 1;
        int r = max(nums);

        while(l < r){
            int mid = l + (r-l)/2;

            if(check(nums, threshold, mid)){
                r = mid;
            }else{
                l = mid + 1;
            }
        }

        return r;
    }
}
