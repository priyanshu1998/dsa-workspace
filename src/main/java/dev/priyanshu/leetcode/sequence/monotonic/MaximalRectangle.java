package dev.priyanshu.leetcode.sequence.monotonic;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 85, name = "maximal-rectangle")
public interface MaximalRectangle {
  int maximalRectangle(char[][] matrix);
}

class MaximalRectangleImpl implements MaximalRectangle {

  @Override
  public int maximalRectangle(char[][] matrix) {
    int n = matrix.length;
    int m = matrix[0].length;

    int max = 0;
    var solver = new LargestRectangleInHistogramImpl();

    int[] heights = new int[m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (matrix[i][j] == '1') {
          heights[j]++;
        } else {
          heights[j] = 0;
        }
      }

      max = Math.max(max, solver.largestRectangleArea(heights));
      solver.stack.clear();
    }
    return max;
  }
}
