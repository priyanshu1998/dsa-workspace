package dev.priyanshu.leetcode.subarray;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumSumOfDistinctSubarraysWithLengthKTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 5, 4, 2, 9, 9, 9}, 3, 15),
        Arguments.of(new int[] {4, 4, 4}, 3, 0));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maximumSubarraySum(int[] a, int k, long expected) {
    var solver = new MaximumSumOfDistinctSubarraysWithLengthKImpl();
    Assertions.assertEquals(expected, solver.maximumSubarraySum(a, k));
  }
}
