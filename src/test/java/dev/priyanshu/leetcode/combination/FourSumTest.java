package dev.priyanshu.leetcode.combination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FourSumTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(
            new int[] {1, 0, -1, 0, -2, 2},
            0,
            List.of(List.of(-2, -1, 1, 2), List.of(-2, 0, 0, 2), List.of(-1, 0, 0, 1))),
        Arguments.of(new int[] {2, 2, 2, 2, 2}, 8, List.of(List.of(2, 2, 2, 2))),
        Arguments.of(
            new int[] {1000000000, 1000000000, 1000000000, 1000000000},
            4000000000L,
            List.of(List.of(1000000000, 1000000000, 1000000000, 1000000000))));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, long target, List<List<Integer>> expected) {
    var solver = new FourSumImpl();

    var result = solver.fourSum(nums, target);

    for (var each : result) {
      var quad = new Quadruplet(each.get(0), each.get(1), each.get(2), each.get(3));
      Assertions.assertEquals(target, quad.sum());
    }
  }
}

class GenericSumSolution {
  public List<List<Integer>> fourSum(int[] nums, int target) {
    Arrays.sort(nums);
    return kSum(nums, target, 0, 4);
  }

  public List<List<Integer>> kSum(int[] nums, long target, int start, int k) {
    List<List<Integer>> res = new ArrayList<>();

    // If we have run out of numbers to add, return res.
    if (start == nums.length) {
      return res;
    }

    // There are k remaining values to add to the sum. The
    // average of these values is at least target / k.
    long average_value = target / k;

    // We cannot obtain a sum of target if the smallest value
    // in nums is greater than target / k or if the largest
    // value in nums is smaller than target / k.
    if (nums[start] > average_value || average_value > nums[nums.length - 1]) {
      return res;
    }

    if (k == 2) {
      return twoSum(nums, target, start);
    }

    for (int i = start; i < nums.length; ++i) {
      if (i == start || nums[i - 1] != nums[i]) {
        for (List<Integer> subset : kSum(nums, target - nums[i], i + 1, k - 1)) {
          res.add(new ArrayList<>(Arrays.asList(nums[i])));
          res.get(res.size() - 1).addAll(subset);
        }
      }
    }

    return res;
  }

  public List<List<Integer>> twoSum(int[] nums, long target, int start) {
    List<List<Integer>> res = new ArrayList<>();
    int lo = start, hi = nums.length - 1;

    while (lo < hi) {
      int currSum = nums[lo] + nums[hi];
      if (currSum < target || (lo > start && nums[lo] == nums[lo - 1])) {
        ++lo;
      } else if (currSum > target || (hi < nums.length - 1 && nums[hi] == nums[hi + 1])) {
        --hi;
      } else {
        res.add(Arrays.asList(nums[lo++], nums[hi--]));
      }
    }

    return res;
  }
}
