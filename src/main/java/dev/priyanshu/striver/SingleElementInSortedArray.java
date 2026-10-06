package dev.priyanshu.striver;

public interface SingleElementInSortedArray {
    int singleNonDuplicate(int[] nums);
}

class SingleElementInSortedArrayImpl implements SingleElementInSortedArray{

    @Override
    public int singleNonDuplicate(int[] nums) {
        int l = 0;
        int r = nums.length-1;

        while(l<r){
            int mid = (l+r)/2;

            if(mid%2==1)mid--;

            if(nums[mid] == nums[mid+1]){
                l = mid+2;
            }else{
                r = mid;
            }
        }

        return nums[l];
    }
}
