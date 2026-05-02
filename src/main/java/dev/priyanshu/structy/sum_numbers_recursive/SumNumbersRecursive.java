package dev.priyanshu.structy.sum_numbers_recursive;

import dev.priyanshu.annotation.Structy;
import java.util.Arrays;

@Structy(tag = {"recursion"})
public interface SumNumbersRecursive {
  long sumNumbersRecursive(int[] nums);
}

class SumNumbersRecursiveImpl implements SumNumbersRecursive {

  @Override
  public long sumNumbersRecursive(int[] nums) {
    if (nums.length == 0) return 0;

    return nums[0] + sumNumbersRecursive(Arrays.copyOfRange(nums, 1, nums.length));
  }
}

class SumNumbersTailRecursiveImpl implements SumNumbersRecursive {
  private long sumNumbersRecursive(int[] nums, int i, long tailSum) {
    if (nums.length == i) {
      return tailSum;
    }

    return sumNumbersRecursive(nums, i + 1, tailSum + nums[i]);
  }

  @Override
  public long sumNumbersRecursive(int[] nums) {
    return sumNumbersRecursive(nums, 0, 0);
  }
}
