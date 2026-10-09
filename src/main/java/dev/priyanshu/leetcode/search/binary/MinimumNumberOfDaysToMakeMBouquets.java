package dev.priyanshu.leetcode.search.binary;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MinimumNumberOfDaysToMakeMBouquets {
    int minDays(int[] bloomDay, int m, int k);
}

class MinimumNumberOfDaysToMakeMBouquetsImpl implements MinimumNumberOfDaysToMakeMBouquets {

    private int max(int[] bloomDay){
        int M = bloomDay[0];

        for(int i=0; i<bloomDay.length; i++){
            M = Math.max(M, bloomDay[i]);
        }

        return M;
    }

    private boolean check(int[] bloomDay, int m, int k, int mid){
        Consumer<Integer> display  = (v) -> System.out.printf("check; mid=%d, bouquetCount=%d\n", mid, v);
        BiConsumer<Integer, Integer> displayWindowSize = (l, r) -> System.out.printf("l=%d r=%d\n,", l, r);
        int bouquetCount = 0;

        int l = 0;
        int r = 0;

        while(r < bloomDay.length){
            if(bloomDay[r] <= mid){
//                displayWindowSize.accept(l, r);
                r++;

                if(r-l == k){
                    bouquetCount++;
                    l = r;
                }
            }else{
                r++;
                l = r;
            }
        }

//        display.accept(bouquetCount);
        return bouquetCount >= m;
    }

    @Override
    public int minDays(int[] bloomDay, int m, int k) {
        if((long) m * k > bloomDay.length) return -1;

        int l = 1;
        int r = max(bloomDay);

        while(l < r){
            int mid = l + (r-l)/2;

            if(check(bloomDay, m, k, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}
