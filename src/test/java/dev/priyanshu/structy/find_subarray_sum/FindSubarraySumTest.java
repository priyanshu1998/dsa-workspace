package dev.priyanshu.structy.find_subarray_sum;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindSubarraySumTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 3, 7, 5}, 12),
        Arguments.of(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 15),
        Arguments.of(new int[] {3, 1, 4, 9, 2, 1, 7}, 10),
        Arguments.of(new int[] {3, 1, 4, 9, 2, 1, 7}, 11));
  }

  private boolean check(int[] nums, long target, int l, int r) {
    long sum = 0;
    for (int i = l; i <= r; i++) {
      sum += nums[i];
    }

    return sum == target;
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, long target) {
    var solver = new WindowOptimized();
    var actual = solver.findSubarraySum(nums, target);
    assertTrue(check(nums, target, actual[0], actual[1]));
  }

  @Test
  void singleTest() {
    int[] nums = new int[] {3, 1, 4, 9, 2, 1, 7};
    int target = 10;
    var solver = new WindowOptimized();

    var actual = solver.findSubarraySum(nums, target);
    assertTrue(check(nums, target, actual[0], actual[1]));
  }
}
