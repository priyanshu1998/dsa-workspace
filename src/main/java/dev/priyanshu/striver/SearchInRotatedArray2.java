package dev.priyanshu.striver;

public interface SearchInRotatedArray2 {
    boolean search(int[] nums, int x);
}

class SearchInRotatedArray2Impl implements SearchInRotatedArray2 {
    private boolean binarySearch(int[] nums, int x, int l, int r){
        while(l <= r){
            int mid = (l+r)/2;

            if(nums[mid] == x){
                return true;
            }else if(nums[mid] < x){
                l = mid + 1;
            }else {
                r = mid - 1;
            }
        }
        return false;
    }

    private boolean rotatedSearch(int[] nums, int x, int l, int r){
        // key insight removing a sorted half does not change the relative nums[i] ? nums[j] for all i,j of the remaining half
        // Hence T(n)
        //          = T(n/2) + O(1), n>2
        //          = O(1) , n<=2

        if(nums[l] == x || nums[r] == x) return true;

        while(r-l>2 && nums[l] == nums[r]){
            l++;
            r--;
        }

        if(r==l) return nums[l] == x;
        if(r-l==1) return nums[l] == x || nums[r] == x;

        int mid = (l+r)/2;
        if(nums[l] <= x && x <= nums[mid]) return binarySearch(nums, x, l, mid); // x is present in first half which is sorted
        else if(nums[l] <= nums[mid]) return rotatedSearch(nums, x, mid+1, r); // though first half is sorted it's not present there (look in other half)
        else if(nums[mid] <= x && x <= nums[r]) return binarySearch(nums, x, mid, r); // x is present in second half and is sorted
        else if(nums[mid] <= nums[r]) return rotatedSearch(nums, x, l, mid-1); // thought second half is sorted it's not present there (look in other half)
        else return false;
    }

    @Override
    public boolean search(int[] nums, int x) {
        return rotatedSearch(nums, x, 0, nums.length-1);
    }
}

