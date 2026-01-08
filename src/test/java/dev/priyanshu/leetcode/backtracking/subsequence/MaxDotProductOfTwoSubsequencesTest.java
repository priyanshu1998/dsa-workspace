package dev.priyanshu.leetcode.backtracking.subsequence;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaxDotProductOfTwoSubsequencesTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {2, 1, -2, 5}, new int[] {3, 0, -6}, 18),
        Arguments.of(new int[] {3, -2}, new int[] {2, -6, 7}, 21),
        Arguments.of(new int[] {-1, -1}, new int[] {1, 1}, -1),
        Arguments.of(new int[] {3, -1, 0}, new int[] {4, 5, 3}, 15),
        Arguments.of(
            new int[] {
              13, -7, 12, -15, -7, 8, 3, -7, -5, 13, -15, -8, 5, 7, -1, 3, -11, -12, 2, -12
            },
            new int[] {-1, 13, -4, -2, -13, 2, -4, 6, -9, 13, -8, -3, -9},
            972));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maxDotProduct(int[] a, int[] b, int expected) {
    var solver = new MaxDotProductOfTwoSubsequencesImpl();
    Assertions.assertEquals(expected, solver.maxDotProduct(a, b));
  }
}
