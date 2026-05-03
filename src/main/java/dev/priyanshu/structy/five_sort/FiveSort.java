package dev.priyanshu.structy.five_sort;

public interface FiveSort {
  void sort(int[] nums);
}

class FiveSortImpl implements FiveSort {

  @Override
  public void sort(int[] nums) {
    int i = 0; // read head
    int j = 0; // write head

    while (i < nums.length) {
      if (nums[i] == 5) {
        i++;
        continue;
      }
      nums[j] = nums[i];
      i++;
      j++;
    }

    while (j < nums.length) {
      nums[j] = 5;
      j++;
    }
  }
}

class FiveSortImpl2 implements FiveSort {

  @Override
  public void sort(int[] nums) {
    int i = 0; // non-five end
    int j = nums.length - 1; // five end

    while (i < j) {
      // find first non-five index from right;
      while (nums[j] == 5) {
        j--;
        if (j == -1) return; // exit if does not exist(already sorted)
      }

      // find first five index from left
      while (nums[i] != 5) {
        i++;
        if (i == nums.length) return; // exit if does not exist (already sorted)
      }

      var t = nums[i];
      nums[i] = nums[j];
      nums[j] = t;

      i++;
      j--;
    }
  }
}
