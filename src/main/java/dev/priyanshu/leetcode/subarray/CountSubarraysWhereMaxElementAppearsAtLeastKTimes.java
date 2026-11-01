package dev.priyanshu.leetcode.subarray;

public interface CountSubarraysWhereMaxElementAppearsAtLeastKTimes {
    long countSubarrays(int[] nums, int k);
}

class CountSubarraysWhereMaxElementAppearsAtLeastKTimesImpl implements CountSubarraysWhereMaxElementAppearsAtLeastKTimes {

    private int max(int[] nums){
        int M = nums[0];
        for(int num: nums){
            M = Math.max(M, num);
        }

        return M;
    }

    private boolean canAppend(int[] nums, int r, int M, int count, int k){
        if(r == nums.length-1) return false;

        if(nums[r] == M){
            return count + 1 < k;
        }else{
            return count < k;
        }
    }



    /*
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] == M) count++;
            while (count == k) {          // shrink until the window has only k-1 M's
                if (nums[l] == M) count--;
                l++;
            }
            result += l;                  // starts 0..l-1 all give ≥ k M's ending at r
        }
     */

    @Override
    public long countSubarrays(int[] nums, int k) {
        int M = max(nums);

        int r = 0;
        int count = 0;

        long result = 0;

        for(int l=0; l < nums.length; l++) {
            while (r < nums.length && count < k) {
                if (nums[r] == M) count++;
                r++;
            }

            System.out.printf("l=%d r=%d count=%d\n", l, r, count);
            if (count == k) {
                result += (nums.length - r + 1);
            }

            if (nums[l] == M) {
                count--;
            }
        }

        return result;
    }
}
