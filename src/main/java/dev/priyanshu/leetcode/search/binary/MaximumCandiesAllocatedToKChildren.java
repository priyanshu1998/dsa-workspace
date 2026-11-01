package dev.priyanshu.leetcode.search.binary;

public interface MaximumCandiesAllocatedToKChildren {
    int maximumCandies(int[] candies, long k);
}

class MaximumCandiesAllocatedToKChildrenImpl implements MaximumCandiesAllocatedToKChildren {

    private int min(int[] candies){
        int m = candies[0];

        for(int candy: candies){
            m = Math.min(candy, m);
        }
        return m;
    }


    private boolean check(int[] candies, long k, int mid){
        long count = 0;

        for(int candy: candies){
            count += candy / mid;
        }

        return count >= k;
    }

    @Override
    public int maximumCandies(int[] candies, long k) {
        int l = 0;
        int r = min(candies);

        while(l < r){
            System.out.printf("l=%d, r=%d\n", l, r);

            int mid = l + (r-l)/2 + 1;

            if(check(candies, k, mid)){
                l = mid;
            }else{
                r = mid-1;
            }
        }

        return l;
    }
}
