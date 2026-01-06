package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;
import java.util.HashSet;
import java.util.Set;

@Leetcode(id = 2461, name = "maximum-sum-of-distinct-subarrays-with-length-k")
public interface MaximumSumOfDistinctSubarraysWithLengthK {
  long maximumSubarraySum(int[] a, int k);
}

class MaximumSumOfDistinctSubarraysWithLengthKImpl
    implements MaximumSumOfDistinctSubarraysWithLengthK {

  Set<Integer> flag;
  int[] a;
  int sum;
  int l;
  int r;

  MaximumSumOfDistinctSubarraysWithLengthKImpl() {
    flag = new HashSet<>();
    sum = 0;
    l = 0;
    r = 0;
  }

  void setArr(int[] arr) {
    this.a = arr;
  }

  void incrementL() {
    sum -= a[l];
    flag.remove(a[l]);
    l++;
  }

  void incrementR() {
    sum += a[r];
    flag.add(a[r]);
    r++;
  }

  @Override
  public long maximumSubarraySum(int[] a, int k) {
    int sol = 0;
    int n = a.length;
    setArr(a);

    while (l - k < n && r < n) {
      while (flag.contains(a[r]) || r - l >= k) {
        incrementL();
      }
      incrementR();

      if (r - l == k) {
        sol = Math.max(sol, sum);
      }
    }

    return sol;
  }
}
