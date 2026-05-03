package dev.priyanshu.structy.find_leftmost_index;

public interface FindLeftmostIndex {
  int index(int[] nums, int target);
}

class FindLeftmostIndexImpl implements FindLeftmostIndex {

  @Override
  public int index(int[] nums, int target) {
    int l = 0;
    int r = nums.length - 1;

    int ret = -1;
    while (l <= r) {
      int mid = (l + r) / 2;
      if (nums[mid] == target) {
        ret = mid;
        r = mid - 1;
      } else if (nums[mid] < target) {
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }

    return ret;
  }
}
