package dev.priyanshu.leetcode.search.binary;

import java.util.function.Consumer;

public interface SellDiminishingValuedColoredBalls {
    int maxProfit(int[] inventory, int orders);
}

class SellDiminishingValuedColoredBallsImpl implements SellDiminishingValuedColoredBalls {

    private int maximum(int[] inventory){
        int max = inventory[0];

        for(int i=0; i<inventory.length; i++){
            max = Math.max(max, inventory[i]);
        }

        return max;
    }

    private boolean check(int[] inventory, int orders, int mid){
        Consumer<Long> display = (v)->System.out.printf("check; mid=%d, count=%d\n", mid, v);
        long count = 0;

        for(int i=0; i<inventory.length; i++){
            if(inventory[i] > mid){
                count += inventory[i] - mid;
            }
        }

//        display.accept(count);
        return count <= orders;
    }

    private int answer(int[] inventory, int order, int r){
        long ret = 0;
        int count = 0;
        int M = 1000_000_007;
        for(int i=0; i<inventory.length; i++){
            if(inventory[i] > r){
                count += inventory[i]-r;
                int a = Math.toIntExact(((long) inventory[i] * (inventory[i]+1) / 2) % M);
                int b = Math.toIntExact(((long) r * (r+1) / 2) % M);
                ret = (Math.toIntExact(ret + a-b)%M + M)%M;
            }
        }

        ret = ((ret + ((long) (order - count) *r)%M))%M;
        return Math.toIntExact(ret);
    }

    @Override
    public int maxProfit(int[] inventory, int orders) {
        int l = 0;
        int r = maximum(inventory);

        while(l < r){
            int mid = l + (r-l)/2;

            if(check(inventory, orders, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return answer(inventory, orders, r);
    }
}
