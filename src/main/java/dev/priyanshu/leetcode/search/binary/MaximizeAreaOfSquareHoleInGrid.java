package dev.priyanshu.leetcode.search.binary;

import dev.priyanshu.annotation.Leetcode;
import java.util.Arrays;

@Leetcode(id = 2943, value = "maximize-area-of-square-hole-in-grid")
public interface MaximizeAreaOfSquareHoleInGrid {
  int maximizeSquareHoleArea(int n, int m, int[] hBars, int[] vBars);
}

class MaximizeAreaOfSquareHoleInGridImpl implements MaximizeAreaOfSquareHoleInGrid {

  private boolean removeAllPossible(int y, int x, int l, int[] hBars, int[] vBars, int n, int m) {
    int hBar = hBars[y] - 1; // matches first HBar
    int vBar = vBars[x] - 1; // matches last HBar

    final int lastHBarIdx = y + l - 2;
    var matchLastHBar = (lastHBarIdx < hBars.length) && (hBars[lastHBarIdx] == hBar + l - 1);

    final int lastVBarIdx = x + l - 2;
    var matchLastVBar = (lastVBarIdx < vBars.length) && (vBars[x + l - 2] == vBar + l - 1);

    return matchLastHBar && matchLastVBar;
  }

  private boolean check(int k, int[] hBars, int[] vBars, int n, int m) {
    for (int i = 0; i < hBars.length; i++) { // iterate x-axis aligned bars
      for (int j = 0; j < vBars.length; j++) { // iterate y-axis aligned bars
        if (removeAllPossible(i, j, k, hBars, vBars, n, m)) {
          return true;
        }
      }
    }
    return false;
  }

  private int binarySearch(int n, int m, int[] hBars, int[] vBars) {
    Arrays.sort(hBars);
    Arrays.sort(vBars);

    int l = 2;
    int r = Math.min(101, Math.min(n + 2, m + 2));

    int size = 1;
    while (l <= r) {
      int mid = (l + r) / 2;

      if (check(mid, hBars, vBars, n, m)) { // greater needed
        size = Math.max(size, mid);
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }
    return size * size;
  }

  private int maxSubArrayLength(int[] a) {
    int n = a.length;
    int apLength = 1;
    int size = 1;

    int i = 1;
    while (i < n) {
      if (a[i] - a[i - 1] == 1) {
        apLength += 1;
        size = Math.max(size, apLength);
      } else {
        apLength = 1;
      }
      i++;
    }
    return size;
  }

  @Override
  public int maximizeSquareHoleArea(int n, int m, int[] hBars, int[] vBars) {
    //        return binarySearch(n, m, hBars, vBars);

    Arrays.sort(hBars);
    Arrays.sort(vBars);

    var hLength = maxSubArrayLength(hBars);
    var vLength = maxSubArrayLength(vBars);

    var size = Math.min(hLength, vLength);
    return (size + 1) * (size + 1);
  }
}
