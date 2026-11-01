package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

import java.util.HashMap;
import java.util.Map;

@Leetcode(id=904, name="fruit-into-baskets")
public interface FruitIntoBaskets {
    int totalFruit(int[] fruits);
}

class FruitIntoBasketsImpl implements FruitIntoBaskets {

    private void add(Map<Integer, Integer> freq, int fruit){
        freq.put(fruit, freq.getOrDefault(fruit, 0)+1);
    }


    private int mapTot(Map<Integer, Integer> freq){
        int tot = 0;
        for(var key: freq.keySet()){
            tot += freq.get(key);
        }

        return tot;
    }

    private void remove(Map<Integer, Integer> freq, int fruit){
        int v = freq.get(fruit);

        if(v == 1){
            freq.remove(fruit);
        }
        else{
            freq.put(fruit, v-1);
        }
    }

    @Override
    public int totalFruit(int[] fruits) {
        Map<Integer, Integer> freq = new HashMap<>();

        int l = 0;
        int tot = 0;

        for(int r=0; r<fruits.length; r++){
            // add leading element
            add(freq, fruits[r]);

            // remove trailing elements while constraint is violated
            while(freq.size() > 2){
                remove(freq, fruits[l]);
                l++;
            }

            // update ans
            tot = Math.max(tot,mapTot(freq));
        }

        return tot;
    }
}
