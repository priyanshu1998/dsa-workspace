package dev.priyanshu.striver;

import java.util.Arrays;

public interface AggressiveCows {
    int aggressiveCows(int[] nums, int k);
}

class AggressiveCowsImpl implements AggressiveCows {

    private boolean isAMinimumDistance(int[] nums, int k, int dist){
        int i = 0;
        int j = 1;
        int count = 1;
        while(j < nums.length){
            if(nums[j] - nums[i] >= dist){
                i = j;
                j = j+1;

                count++;
            }else{
                j++;
            }
        }

        return count >= k;
    }

    @Override
    public int aggressiveCows(int[] nums, int k) {
        Arrays.sort(nums);

        int l = 0;
        int r = 1_000_000_000;

        while(l<r){
            int mid = l + (r-l+1)/2;
            System.out.printf("%d %d %d\n", l, mid, r);

            if(isAMinimumDistance(nums, k, mid)){
                l = mid;
            }else{
                r = mid-1;
            }
        }

        return l;
    }
}

