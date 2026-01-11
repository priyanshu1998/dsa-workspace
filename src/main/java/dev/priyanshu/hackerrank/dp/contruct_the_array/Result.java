package dev.priyanshu.hackerrank.dp.contruct_the_array;

public class Result {
  private static int n;
  private static int k;
  private static int x;

  static long[] dpTrue = new long[100001];
  static long[] dpFalse = new long[100001];

  static final long MOD = 1_000_000_007;

  public static long solveState(int j, int p) {
    if (j == n - 1) return 1; // only one value can be placed at arr[n-1]

    long ways = 0;
    for (int v = 1; v <= k; v++) {
      if (v == p) continue; // avoids arr[j-1] == arr[j]

      if (j == n - 2 && v == x) {
        continue; // avoids arr[n-2] = x
      }

      ways += solveState(j + 1, v);
    }

    return ways % MOD;
  }

  static long compressedSolveState(int j, boolean prevIsX) {
    if (j == n) return prevIsX ? 1 : 0; // when j == n, array creation is completed
    // , if last value is x then array is valid.

    if (!prevIsX) {
      if (dpFalse[j] != -1) return dpFalse[j];
      long v1 = compressedSolveState(j + 1, true); // place x, 1 choice
      long v2 =
          (k - 2)
              * compressedSolveState(
                  j + 1, false); // place all value apart from x and prev value, (k-2) choices
      return dpFalse[j] = (v1 + v2) % MOD; // total (k-1) choices
    } else {
      if (dpTrue[j] != -1) return dpTrue[j];
      return dpTrue[j] =
          ((k - 1) * compressedSolveState(j + 1, false))
              % MOD; // (k-1) choices, place value apart from the last placed i.e x
    }
  }

  public static long countArray(int n, int k, int x) {
    // Return the number of ways to fill in the array.
    Result.n = n;
    Result.k = k;
    Result.x = x;

    for (int i = 0; i < 100000; i++) {
      dpTrue[i] = -1;
      dpFalse[i] = -1;
    }
    return compressedSolveState(1, Result.x == 1); // j = 1, 0th position is already placed,
    // as we have arr[0]=1
  }
}
