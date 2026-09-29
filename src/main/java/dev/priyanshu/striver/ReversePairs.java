package dev.priyanshu.striver;

import dev.priyanshu.annotation.Striver;

public interface ReversePairs {
    int reversePairs(int[] nums);
}

@Striver(groups = "Striver's 180",
        category = "Divide and Conquer",
        path = "/practice/dsa/reverse-pairs")
class ReversePairsImpl implements ReversePairs {

    private int conquer(int[] nums, int l, int mid, int r){
        int []aux = new int[r-l];

        int inv = 0;

        // O(n)
        for (int i = l, j = mid; i < mid; i++) {
            while (j < r &&  nums[i] > 2L * nums[j]) {
                j++;
            }
            inv += j - mid;
        }

        int i = l;
        int j = mid;
        int k = 0;

        // Sorting element in `aux` array ================
        while(i<mid && j<r){
            if(nums[i] <= nums[j]){
                aux[k] = nums[i];
                i++;
                k++;

            }else{
                aux[k] = nums[j];
                j++;
                k++;
            }
        }

        System.arraycopy(nums, i, aux, k, mid-i);
        System.arraycopy(nums, j, aux, k, r-j);
        // ===================================================

        // Replace nums[l:r] with aux
        System.arraycopy(aux, 0, nums, l, r - l);

        return inv;
    }

    private int divideAndConquer(int[] nums, int l, int r){
        if(r-l==1){
            return 0;
        }

        int mid = (l + r) / 2;
        int countL = divideAndConquer(nums, l, mid);
        int countR = divideAndConquer(nums, mid, r);
        int countMerged = conquer(nums, l, mid, r);

        return countL + countMerged + countR;
    }
    @Override
    public int reversePairs(int[] nums) {
        return divideAndConquer(nums, 0, nums.length);
    }
}
