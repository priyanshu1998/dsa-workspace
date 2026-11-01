package dev.priyanshu.leetcode.subarray;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface MinimumDiscardsToBalanceInventory {
    int minArrivalsToDiscard(int[] arrivals, int w, int m);
}

class MinimumDiscardsToBalanceInventoryImpl implements MinimumDiscardsToBalanceInventory {

    private boolean canAccept(Map<Integer, Integer> freq, int key, int m){
        return freq.getOrDefault(key, 0) < m;
    }

    private void add(Map<Integer, Integer> freq, int key){
        freq.put(key, freq.getOrDefault(key, 0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int key){
        int v = freq.get(key);

        if(v == 1){
            freq.remove(key);
        }else{
            freq.put(key, v-1);
        }
    }


    @Override
    public int minArrivalsToDiscard(int[] arrivals, int w, int m) {

        Map<Integer, Integer> freq = new HashMap<>(arrivals.length);
        Set<Integer> skips = new HashSet<>();
        int discarded = 0;


        for(int i=0; i<w; i++){
            if(canAccept(freq, arrivals[i], m)){
                add(freq, arrivals[i]);
            }else{
                skips.add(i);
                discarded++;
            }
        }


        for(int i=w; i<arrivals.length; i++){
            if(!skips.contains(i-w)) {
                remove(freq, arrivals[i - w]);
            }
            if(canAccept(freq, arrivals[i], m)){
                add(freq, arrivals[i]);
            }else{
                skips.add(i);
                discarded++;
            }
        }

        return discarded;
    }
}
