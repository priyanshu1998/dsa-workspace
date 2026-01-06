package dev.priyanshu.leetcode.combination;

import dev.priyanshu.annotation.Leetcode;
import java.util.HashMap;
import java.util.Map;

@Leetcode(id = 1411, name = "number-of-ways-to-paint-n-3-grid")
public interface NumberOfWaysToPaintNx3Grid {
  int numOfWays(int n);
}

class NumberOfWaysToPaintNx3GridImpl implements NumberOfWaysToPaintNx3Grid {
  private static final int M = 1_000_000_007;

  private final Map<Integer, Integer> dpABA = new HashMap<>();
  private final Map<Integer, Integer> dpABC = new HashMap<>();

  private int solveStateABA(int n) {
    if (n == 0) return 1;

    Integer cached = dpABA.get(n);
    if (cached != null) return cached;

    int val = (int) ((3L * solveStateABA(n - 1) + 2L * solveStateABC(n - 1)) % M);

    dpABA.put(n, val);
    return val;
  }

  private int solveStateABC(int n) {
    if (n == 0) return 1;

    Integer cached = dpABC.get(n);
    if (cached != null) return cached;

    int val = (int) ((2L * solveStateABA(n - 1) + 2L * solveStateABC(n - 1)) % M);

    dpABC.put(n, val);
    return val;
  }

  @Override
  public int numOfWays(int n) {
    return (int) ((6L * solveStateABA(n - 1) + 6L * solveStateABC(n - 1)) % M);
  }
}
