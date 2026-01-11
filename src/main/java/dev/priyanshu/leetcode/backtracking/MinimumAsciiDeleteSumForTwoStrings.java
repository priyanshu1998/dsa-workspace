package dev.priyanshu.leetcode.backtracking;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 712, name = "minimum-ascii-delete-sum-for-two-strings")
public interface MinimumAsciiDeleteSumForTwoStrings {
  int minimumDeleteSum(String s1, String s2);
}

class MinimumAsciiDeleteSumForTwoStringsImpl implements MinimumAsciiDeleteSumForTwoStrings {

  String s1;
  String s2;

  int[][] dp = new int[1002][1002];

  MinimumAsciiDeleteSumForTwoStringsImpl() {
    for (int i = 0; i <= 1000; i++) {
      for (int j = 0; j < 1000; j++) {
        dp[i][j] = -1;
      }
    }
  }

  void setS1(String s1) {
    this.s1 = s1;
  }

  void setS2(String s2) {
    this.s2 = s2;
  }

  private int stringAsciiSum(String s) {
    int sum = 0;
    for (char c : s.toCharArray()) {
      sum += c;
    }
    return sum;
  }

  int solveState(int i, int j) {
    if (dp[i][j] != -1) return dp[i][j];
    if (i == s1.length()) return dp[i][j] = stringAsciiSum(s2.substring(j));
    if (j == s2.length()) return dp[i][j] = stringAsciiSum(s1.substring(i));

    if (s1.charAt(i) == s2.charAt(j)) {
      return solveState(i + 1, j + 1);
    }

    int v1 = s1.charAt(i) + solveState(i + 1, j);
    int v2 = s2.charAt(j) + solveState(i, j + 1);

    return dp[i][j] = Math.min(v1, v2);
  }

  @Override
  public int minimumDeleteSum(String s1, String s2) {
    setS1(s1);
    setS2(s2);

    return solveState(0, 0);
  }
}
