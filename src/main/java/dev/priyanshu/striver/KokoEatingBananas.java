package dev.priyanshu.striver;

public interface KokoEatingBananas {
    int minEatingSpeed(int[] piles, int h);
}

class KokoEatingBananasImpl implements KokoEatingBananas {

    private boolean isEnoughSpeed(int[] piles, int k, int h){
        int iteration = 0;
        for(int pile: piles){
            iteration += (pile + k-1)/k;
        }

        return iteration <= h;
    }

    @Override
    public int minEatingSpeed(int[] piles, int h) {
        int l = 1;
        int r = 1000_000_000;

        while(l<r){
            int mid = l + (r-l)/2;

            if(isEnoughSpeed(piles, mid, h)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}
