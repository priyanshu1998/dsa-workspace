package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

import java.util.HashMap;
import java.util.Map;

@Leetcode(id = 2260, name = "minimum-consecutive-cards-to-pick-up")
public interface MinimumConsecutiveCardsToPickUp {
    int minimumCardPickup(int[] cards);
}

class MinimumConsecutiveCardsToPickUpImpl implements MinimumConsecutiveCardsToPickUp {

    private void add(Map<Integer, Integer> freq, int card){
        freq.put(card, freq.getOrDefault(card,0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int card){
        int v = freq.get(card);
        if(v == 1){
            freq.remove(card);
        }else{
            freq.put(card, v-1);
        }
    }

    @Override
    public int minimumCardPickup(int[] cards) {
        int l = 0;

        int ans = cards.length+1;
        var freq = new HashMap<Integer, Integer>();

        for(int r=0; r < cards.length; r++){
            // add leading element
            add(freq, cards[r]);

            // remove trailing elements
            while(freq.get(cards[r]) > 1){
                remove(freq, cards[l]);
                l++;
            }

            // update ans
            if(l>0 && cards[l-1] == cards[r]) ans = Math.min(ans, r-(l-1)+1);
        }
        return ans == cards.length+1 ? -1 : ans;
    }
}
