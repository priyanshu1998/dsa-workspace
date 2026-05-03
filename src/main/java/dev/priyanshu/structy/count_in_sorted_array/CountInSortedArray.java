package dev.priyanshu.structy.count_in_sorted_array;

public interface CountInSortedArray {
  int count(int[] nums, int target);
}

class CountInSortedArrayImpl implements CountInSortedArray {
  private enum FindType {
    FIND_FIRST,
    FIND_LAST;

    private record Window(int l, int r) {}

    Window updateWindow(int l, int r) {
      int mid = (l + r) / 2;

      switch (this) {
        case FIND_FIRST -> {
          return new Window(l, mid - 1);
        }
        case FIND_LAST -> {
          return new Window(mid + 1, r);
        }
        default -> throw new IllegalStateException();
      }
    }
  }

  private int index(int[] nums, int target, FindType type) {
    int l = 0;
    int r = nums.length - 1;
    int ret = -1;

    while (l <= r) {
      int mid = (l + r) / 2;

      if (nums[mid] == target) {
        var window = type.updateWindow(l, r);
        ret = mid;
        l = window.l();
        r = window.r();
      } else if (nums[mid] < target) {
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }

    return ret;
  }

  @Override
  public int count(int[] nums, int target) {
    int first = index(nums, target, FindType.FIND_FIRST);
    int last = index(nums, target, FindType.FIND_LAST);

    return first != -1 ? (last - first + 1) : 0;
  }
}
