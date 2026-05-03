package dev.priyanshu.structy.binary_search;

import java.util.Arrays;

public interface BinarySearch {
  int search(int[] nums, int target);
}

class BinarySearchImpl implements BinarySearch {

  @Override
  public int search(int[] nums, int target) {
    int l = 0;
    int r = nums.length - 1;

    while (l <= r) {
      int mid = (l + r) / 2;

      if (nums[mid] == target) {
        return mid;
      } else if (nums[mid] < target) {
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }

    return -1;
  }
}

class InbuildBinarySearchImpl implements BinarySearch {

  @Override
  public int search(int[] nums, int target) {
    int index = Arrays.binarySearch(nums, target);
    if (index < 0) {
      return -1;
    }

    return index;
  }
}
