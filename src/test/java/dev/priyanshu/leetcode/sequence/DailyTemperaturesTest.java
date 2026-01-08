package dev.priyanshu.leetcode.sequence;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DailyTemperaturesTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(
            new int[] {73, 74, 75, 71, 69, 72, 76, 73}, new int[] {1, 1, 4, 2, 1, 1, 0, 0}),
        Arguments.of(new int[] {30, 40, 50, 60}, new int[] {1, 1, 1, 0}),
        Arguments.of(new int[] {30, 60, 90}, new int[] {1, 1, 0}));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] temperatures, int[] expected) {
    var solver = new DailyTemperaturesImpl();
    Assertions.assertArrayEquals(expected, solver.dailyTemperatures(temperatures));
  }
}
