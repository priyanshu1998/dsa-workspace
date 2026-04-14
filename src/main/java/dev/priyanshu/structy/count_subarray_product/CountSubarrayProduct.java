package dev.priyanshu.structy.count_subarray_product;

public interface CountSubarrayProduct {
  long countSubarrayProduct(int[] nums, long target);
}

class NaiveSolutions implements CountSubarrayProduct {

  private boolean check(int[] nums, int l, int r, long target) {
    long product = 1;
    for (int i = l; i < r; i++) {
      product *= nums[i];
      if (product >= target) {
        return false;
      }
    }

    return true;
  }

  @Override
  public long countSubarrayProduct(int[] nums, long target) {
    long count = 0;
    for (int i = 0; i < nums.length; i++) {
      for (int j = i + 1; j <= nums.length; j++) {
        if (check(nums, i, j, target)) {
          count++;
        }
      }
    }
    return count;
  }
}

class OptimizedSolution implements CountSubarrayProduct {

  @Override
  public long countSubarrayProduct(int[] nums, long target) {
    long product = 1;
    int count = 0;

    int l = 0;
    int r = 0;

    while (r < nums.length) {
      product *= nums[r];
      while (l < nums.length && product >= target) {

        product /= nums[l];
        l++;
      }

      count += r - l + 1;
      r++;
    }

    return count;
  }
}
