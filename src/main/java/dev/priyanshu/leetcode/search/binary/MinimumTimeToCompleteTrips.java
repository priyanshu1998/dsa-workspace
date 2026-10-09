package dev.priyanshu.leetcode.search.binary;

public interface MinimumTimeToCompleteTrips {
    long minimumTime(int[] time, int totalTrips);
}

class MinimumTimeToCompleteTripsImpl implements MinimumTimeToCompleteTrips {

    private int min(int[] time){
        int M = time[0];
        for(int t: time){
            M = Math.min(t, M);
        }
        return M;
    }

    private boolean check(int []time, int totalTrips, long mid){
        long count = 0;

        for(int i=0; i<time.length; i++){
            count += (mid/time[i]);
        }
        return count >= totalTrips;
    }


    @Override
    public long minimumTime(int[] time, int totalTrips) {
        long l = min(time);
        long r = 100_000_000_000_000L;

        while(l < r){
            var mid = l + (r-l)/2;
            if(check(time, totalTrips, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}