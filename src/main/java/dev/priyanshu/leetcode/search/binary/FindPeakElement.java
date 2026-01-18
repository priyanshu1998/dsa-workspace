package dev.priyanshu.leetcode.search.binary;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 162, name = "find-peak-element")
public interface FindPeakElement {
  int findPeakElement(int[] nums);
}

class FindPeakElementImpl implements FindPeakElement {

  @Override
  public int findPeakElement(int[] nums) {
    int l = 0;
    int r = nums.length - 1;

    while (l < r) {
      int mid = l + (r - l) / 2;

      if (nums[mid] < nums[mid + 1]) {
        // ascending slope → peak is to the right
        l = mid + 1;
      } else {
        // descending slope → peak is at mid or to the left
        r = mid;
      }
    }

    return l; // l == r == peak index
  }
}
