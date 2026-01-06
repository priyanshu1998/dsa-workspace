package dev.priyanshu.leetcode;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 2571, name = "minimum-operations-to-reduce-an-integer-to-0")
public interface MinimumOperationsToReduceAnIntegerToZero {
  int minOperations(int n);
}

class MinimumOperationsToReduceAnIntegerToZeroImpl
    implements MinimumOperationsToReduceAnIntegerToZero {
  private int solveState(int num) {
    if (num == 0) return 0;
    if (num == 1) return 1;

    if ((num & 1) == 1) {
      int state1 = solveState((num + 1) >> 1);
      int state2 = solveState((num - 1) >> 1);

      return 1 + Math.min(state1, state2);
    }
    return solveState(num >> 1);
  }

  public int minOperations(int n) {
    return solveState(n);
  }
}
