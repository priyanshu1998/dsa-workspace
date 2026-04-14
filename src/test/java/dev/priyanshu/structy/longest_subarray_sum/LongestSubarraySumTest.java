package dev.priyanshu.structy.longest_subarray_sum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestSubarraySumTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 1, 5, 2, 3, 10, 1, 9, 4, 3, 3, 7}, 10, 4),
        Arguments.of(new int[] {7, 2, 4, 2, 1}, 5, -1),
        Arguments.of(new int[] {4, 2, 2, 2, 1, 1}, 6, 4),
        Arguments.of(new int[] {1, 5, 2, 4, 9, 2}, 11, 3),
        Arguments.of(new int[] {10, 4, 8, 4}, 10, 1),
        Arguments.of(new int[] {10, 4, 8, 0, 4}, 8, 2),
        Arguments.of(new int[] {2, 4, 1, 1, 2}, 10, 5));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, long target, int expected) {
    var solver = new OptimizedSolution();
    var actual = solver.longestSubarraySum(nums, target);
    assertEquals(expected, actual);
  }
}
