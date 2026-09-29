package dev.priyanshu.striver;

import dev.priyanshu.annotation.Striver;

@Striver(groups = "Striver's 180",
        category = "Divide and Conquer",
        path = "/practice/dsa/count-inversions")
public interface CountInversion {
    long numberOfInversion(int[] nums);
}

class CountInversionImpl implements CountInversion {

    private long conquer(int[]nums, int l, int mid, int r){
        int []aux = new int[r-l];

        int i = l;
        int j = mid;
        int k = 0;

        int inv = 0;

        // Sorting element in `aux` array ================
        while(i < mid && j < r){
            if(nums[i] <= nums[j]){
                aux[k] = nums[i];
                k++;
                i++;
            }else{
                inv+=(mid-i); //nums[i:mid] > nums[j]
                aux[k] = nums[j];
                k++;
                j++;
            }
        }

        System.arraycopy(nums, i, aux, k, mid-i);
        System.arraycopy(nums, j, aux, k, r-j);
        // ===================================================


        // Replace nums[l:r] with aux
        System.arraycopy(aux, 0, nums, l, r - l);

        return inv;
    }


    private long divideAndConquer(int[] nums, int l, int r){
        if(r-l==1){
            return 0;
        }

        int mid = (l+r)/2;
        long countL = divideAndConquer(nums, l, mid);
        long countR = divideAndConquer(nums, mid, r);
        long countCombine = conquer(nums, l, mid, r);
        return countL + countR + countCombine;
    }

    @Override
    public long numberOfInversion(int[] nums) {
        return divideAndConquer(nums, 0, nums.length);
    }
}
