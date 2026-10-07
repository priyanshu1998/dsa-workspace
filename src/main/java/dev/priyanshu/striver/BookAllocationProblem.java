package dev.priyanshu.striver;

public interface BookAllocationProblem {
    int findPages(int[] nums, int m);
}

class BookAllocationProblemImpl implements BookAllocationProblem {

    private int max(int[] nums){
        int m = nums[0];
        for(int num: nums){
            m = Math.max(m, num);
        }
        return m;
    }

    private int sum(int[] nums){
        int tot = 0;
        for(int num: nums){
            tot += num;
        }
        return tot;
    }

    private boolean isAMaximumPageCount(int[] nums, int m, int maxPages) {
        int students = 1;
        int runningSum = 0;

        for (int num : nums) {
            if (runningSum + num > maxPages) {
                students++;
                runningSum = num;
            } else {
                runningSum += num;
            }
        }

        return students <= m;
    }

    @Override
    public int findPages(int[] nums, int m) {
        if (m > nums.length) {
            return -1;
        }

        int l = max(nums);
        int r = sum(nums);

        while(l<r){
            int mid = l + (r-l)/2;
            if(isAMaximumPageCount(nums, m, mid)){
                r = mid;
            }else{
                l = mid + 1;
            }
        }

        return r;
    }
}
