package dev.priyanshu.leetcode.subarray;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumGoodSubarraySumTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 3, 4, 5, 6}, 1, 11),
        Arguments.of(new int[] {-1, 3, 2, 4, 5}, 3, 11),
        Arguments.of(new int[] {-1, -2, -3, -4}, 2, -6),
        Arguments.of(new int[] {1, 5}, 2, 0));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maximumSubarraySum(int[] nums, int k, long expected) {
    var solver = new MaximumGoodSubarraySumImpl();
    var actual = solver.maximumSubarraySum(nums, k);

    Assertions.assertEquals(expected, actual);
  }
}
