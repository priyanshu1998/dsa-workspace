package dev.priyanshu.leetcode.search.binary;

import java.util.Arrays;
import java.util.function.Consumer;

public interface MagneticForceBetweenTwoBalls {
    int maxDistance(int[] position, int m);
}

class MagneticForceBetweenTwoBallsImpl implements MagneticForceBetweenTwoBalls {

    private boolean check(int []position, int m, int mid){
        Consumer<Integer> display = (v) -> System.out.printf("check(mid=%d)=%d\n", mid, v);
        int i = 0;
        int j = 1;
        int count = 1;

        while(j < position.length){
            if(position[j] - position[i] >= mid){
                count++;
                i = j;
            }
            j++;
        }

//        display.accept(count);
        return count >= m;
    }

    @Override
    public int maxDistance(int[] position, int m) {
        Arrays.sort(position);

        int l = 1;
        int r = position[position.length-1] - position[0];

        while(l<r){
            int mid = l + (r-l)/2 + 1;

            if(check(position, m, mid)){
                l = mid;
            }else{
                r = mid-1;
            }
        }

        return l;
    }
}
