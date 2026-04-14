package dev.priyanshu.structy.maximum_subarray_sum;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumSubarraySumTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 4, 1, 10, 25, 3, 5, 0, 26}, 4, 43L),
        Arguments.of(new int[] {4, 2, 1, -9, 8, 4, 3}, 3, 15L),
        Arguments.of(new int[] {2, 1, 5, -4, 6}, 3, 8L),
        Arguments.of(new int[] {1, 4, 1, 10, 25, 3, 1, 0, 20}, 4, 40L),
        Arguments.of(new int[] {20, 50, 10, 60, 80, 70}, 1, 80L),
        Arguments.of(new int[] {-4, -18, -2, -5, -9}, 2, -7L));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maxSubarraySum(int[] nums, int k, long excepted) {
    var solver = new OptimizedSolution();
    Assertions.assertEquals(excepted, solver.maximumSubarraySum(nums, k));
  }
}
