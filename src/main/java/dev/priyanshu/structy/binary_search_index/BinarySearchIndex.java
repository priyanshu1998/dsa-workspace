package dev.priyanshu.structy.binary_search_index;

import java.util.Arrays;

public interface BinarySearchIndex {
  int index(int[] nums, int target);
}

class BinarySearchIndexImpl implements BinarySearchIndex {

  @Override
  public int index(int[] nums, int target) {
    int l = 0;
    int r = nums.length - 1;

    while (l <= r) {
      int mid = (l + r) / 2;

      if (nums[mid] == target) {
        return mid;
      } else if (nums[mid] <= target) {
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }
    return l;
  }
}

class BinarySearchInbuiltImpl implements BinarySearchIndex {

  @Override
  public int index(int[] nums, int target) {
    int index = Arrays.binarySearch(nums, target);
    if (index < 0) {
      return -index - 1;
    }
    return index;
  }
}
