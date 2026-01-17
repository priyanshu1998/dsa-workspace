package dev.priyanshu.leetcode.combination;

import dev.priyanshu.annotation.Leetcode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Leetcode(id = 18, name = "4sum")
public interface FourSum {

  List<List<Integer>> fourSum(int[] nums, long target);
}

record Quadruplet(int nums_a, int nums_b, int nums_c, int nums_d) {
  long sum() {
    return ((long) nums_a) + nums_b + nums_c + nums_d;
  }
}

class FourSumImpl implements FourSum {

  @Override
  public List<List<Integer>> fourSum(int[] nums, long target) {
    Arrays.sort(nums);
    Set<Quadruplet> unique = new HashSet<>();
    int n = nums.length;
    for (int a = 0; a < n; a++) {
      for (int b = a + 1; b < n; b++) {
        int c = b + 1;
        int d = n - 1;

        while (c < d) {
          if (nums[a] + nums[b] == target - nums[c] - nums[d]) {
            unique.add(new Quadruplet(nums[a], nums[b], nums[c], nums[d]));
            c++;
            d--;
          } else if (nums[c] + nums[d] < target - nums[a] - nums[b]) {
            c++;
          } else { // nums[c] + nums[d] > target - nums[a] - nums[b]
            d--;
          }
        }
      }
    }

    return unique.stream()
        .map(q -> List.of(q.nums_a(), q.nums_b(), q.nums_c(), q.nums_d()))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
