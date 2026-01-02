package dev.priyanshu.leetcode;

import static dev.priyanshu.leetcode.enums.Difficulty.EASY;

import dev.priyanshu.leetcode.annotation.Leetcode;

@Leetcode(id = 961, name = "n-repeated-element-in-size-2n-array", difficulty = EASY)
public class NRepeatedElementInSize2NArray {
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
