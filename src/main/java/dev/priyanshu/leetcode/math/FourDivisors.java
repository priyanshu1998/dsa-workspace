package dev.priyanshu.leetcode.math;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 1390, name = "four-divisors")
public interface FourDivisors {
  int sumFourDivisors(int[] nums);
}

class FourDivisorsImpl implements FourDivisors {
  @Override
  public int sumFourDivisors(int[] nums) {
    int ans = 0;
    for (int num : nums) {
      int i = 1;
      int cnt = 0;
      int temp = 0;

      for (i = 1; i * i < num; i++) {
        if (num % i == 0) {
          temp += i;
          temp += (num / i);
          cnt++;
        }
      }

      if (cnt == 2 && i * i != num) {
        ans += temp;
      }
    }

    return ans;
  }
}
