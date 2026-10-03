package dev.priyanshu.striver;

public interface FindMinimumInRotatedArray {
    int findMinimum(int[] nums);
}

class FindMinimumInRotatedArrayImpl implements FindMinimumInRotatedArray{

    @Override
    public int findMinimum(int[] nums) {
        int l = 0;
        int r = nums.length - 1;

        while(l < r){
            int mid = (l+r)/2;

            if(nums[mid]<=nums[r]){
                r = mid;
            }else{
                l = mid+1;
            }
        }
        return nums[l];
    }
}
