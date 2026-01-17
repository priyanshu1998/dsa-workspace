package dev.priyanshu.leetcode.sequence.monotonic;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximalRectangleTest {
  private char[][] sample1() {
    return new char[][] {
      "10100".toCharArray(), "10111".toCharArray(), "11111".toCharArray(), "10010".toCharArray()
    };
  }

  Stream<Arguments> testcases() {
    return Stream.of(
        Arguments.of(sample1(), 6),
        Arguments.of(new char[][] {{'0'}}, 0),
        Arguments.of(new char[][] {{'1'}}, 1),
        Arguments.of(new char[][] {"10".toCharArray()}, 1));
  }

  @ParameterizedTest
  @MethodSource("testcases")
  void test(char[][] matrix, int expected) {
    var solver = new MaximalRectangleImpl();
    Assertions.assertEquals(expected, solver.maximalRectangle(matrix));
  }
}
