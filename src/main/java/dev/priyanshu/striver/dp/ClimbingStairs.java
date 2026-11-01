package dev.priyanshu.striver.dp;

import java.util.HashMap;
import java.util.Map;

public interface ClimbingStairs {
     int climbStairs(int n);
}


class ClimbingStairsImpl implements ClimbingStairs {

    Map<Integer, Long> dp = new HashMap<>();

    private long climbStairsHelper(int n) {
        if(n == 0 || n == 1) return 1;
        if (dp.containsKey(n)) {
            return dp.get(n);
        }
        dp.put(n-1, climbStairsHelper(n - 1));
        dp.put(n-2, climbStairsHelper(n - 2));

        return dp.get(n-1) + dp.get(n-2);
    }

    @Override
    public int climbStairs(int n) {
        return Math.toIntExact(climbStairsHelper(n));
    }
}
