package dev.priyanshu.leetcode.xor;

import dev.priyanshu.annotation.CornerCase;
import dev.priyanshu.annotation.Idea;
import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 961, name = "n-repeated-element-in-size-2n-array")
public interface NRepeatedElementInSize2NArray {
  int repeatedNTimes(int[] nums);
}

@Idea(
    "Instead of maintaining the count, "
        + "notice that there exist a window of 3, "
        + "that always contain the majority")
@CornerCase(scenario = "[1, 2, 3, 1]", solution = "n = 4, first and last elements are equal")
class MajorityInGroupOf3 implements NRepeatedElementInSize2NArray {
  private int findMajority(int a, int b, int c) {
    if (a == b || a == c) {
      return a;
    } else if (b == c) {
      return b;
    }
    return -1;
  }

  public int repeatedNTimes(int[] nums) {
    int n = nums.length;
    for (int i = 0; i < n - 2; i++) {
      int m = findMajority(nums[i], nums[i + 1], nums[i + 2]);
      if (m != -1) {
        return m;
      }
    }

    return findMajority(nums[n - 1], nums[0], nums[2]);
  }
}
