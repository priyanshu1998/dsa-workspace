package dev.priyanshu.leetcode.backtracking.subsequence;

import dev.priyanshu.annotation.Leetcode;
import java.util.Arrays;

@Leetcode(id = 1458, name = "max-dot-product-of-two-subsequences")
public interface MaxDotProductOfTwoSubsequences {
  int maxDotProduct(int[] a, int[] b);
}

class MaxDotProductOfTwoSubsequencesImpl implements MaxDotProductOfTwoSubsequences {

  int[] a;
  int[] b;

  int[][] dp;

  MaxDotProductOfTwoSubsequencesImpl() {
    dp = new int[1000][1000];

    for (int i = 0; i < 1000; i++) {
      for (int j = 0; j < 1000; j++) {
        dp[i][j] = -1;
      }
    }
  }

  void setA(int[] a) {
    this.a = a;
  }

  void setB(int[] b) {
    this.b = b;
  }

  int solveState(int i, int j) {
    if (i == a.length || j == b.length) {
      return 0;
    }

    if (dp[i][j] != -1) return dp[i][j];

    int v1 = a[i] * b[j] + solveState(i + 1, j + 1);
    int v2 = solveState(i, j + 1);
    int v3 = solveState(i + 1, j);

    return dp[i][j] = Math.max(v1, Math.max(v2, v3));
  }

  boolean isCornerCase1() {
    return Arrays.stream(a).allMatch(ai -> ai <= 0) && Arrays.stream(b).allMatch(bi -> bi >= 0);
  }

  boolean isCornerCase2() {
    return Arrays.stream(a).allMatch(ai -> ai >= 0) && Arrays.stream(b).allMatch(bi -> bi <= 0);
  }

  int solveCornerCase() {
    if (isCornerCase2()) {
      var t = a;
      a = b;
      b = t;
    }

    int max = -1000;
    for (int ai : a) {
      max = Math.max(max, ai);
    }

    int min = 1000;
    for (int bi : b) {
      min = Math.min(min, bi);
    }

    return max * min;
  }

  @Override
  public int maxDotProduct(int[] a, int[] b) {
    setA(a);
    setB(b);

    if (isCornerCase1() || isCornerCase2()) {
      return solveCornerCase();
    }

    return solveState(0, 0);
  }
}
