package dev.priyanshu.leetcode.search.binary;

public interface MinimumSumOfSquaredDifference {
    long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2);
}

class MinimumSumOfSquaredDifferenceImpl implements MinimumSumOfSquaredDifference {

    private int maximum(int[] a){
        int m = a[0];
        for(int ai: a){
            m = Math.max(m, ai);
        }

        return m;
    }

    private boolean check(int[] diff, long k, int mid){
        long count = 0;
        for(int i=0; i<diff.length; i++){
            if(diff[i]>mid){
                count += diff[i]-mid;
            }
        }

        return count <= k;
    }


    private long answer(int[] diff, int r, long k){
        // Flatten every value above r down to r; this uses exactly the
        // minimal number of operations needed to reach the threshold r.
        long remaining = k;
        int countAtR = 0;
        for(int i=0; i<diff.length; i++){
            if(diff[i] > r){
                remaining -= diff[i] - r;
                diff[i] = r;
            }
            if(diff[i] == r){
                countAtR++;
            }
        }

        // Any leftover operations are spread one-per-element across the values
        // now sitting at r (r is the minimal feasible threshold, so remaining
        // is guaranteed to be < countAtR), minimizing the resulting sum of squares.
        if(r > 0){
            for(int i=0; i<diff.length && remaining > 0; i++){
                if(diff[i] == r){
                    diff[i]--;
                    remaining--;
                }
            }
        }

        long ans = 0;
        for(int i=0; i<diff.length; i++){
            ans += (long)diff[i] * diff[i];
        }

        return ans;
    }

    @Override
    public long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2) {
        int[] diff = new int[nums1.length];

        for(int i=0; i<nums1.length; i++){
            diff[i] = Math.abs(nums1[i] - nums2[i]);
        }

        int l = 0;
        int r = maximum(diff);


        long k = (long) k1 + k2;

        while(l < r){
            int mid = l + (r-l)/2;

            if(check(diff, k, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return answer(diff, r, k);
    }
}