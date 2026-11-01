package dev.priyanshu.leetcode.search.binary;

public interface MedianOfTwoSortedArrays {
    int findMedianSortedArrays(int[] nums1, int[] nums2);
}

class MedianOfTwoSortedArraysImpl implements MedianOfTwoSortedArrays {



    @Override
    public int findMedianSortedArrays(int[] nums1, int[] nums2) {
        int l = 0;
        int r = nums1.length-1;

        while(l < r){
            int mid = (l+r)/2;

            int b1 = mid;
            int b2 = mid+1;

            int b3 = nums1.length/2 + nums2.length/2;

        }
        return -1;
    }
}
