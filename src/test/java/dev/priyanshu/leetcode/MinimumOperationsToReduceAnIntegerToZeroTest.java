package dev.priyanshu.leetcode;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumOperationsToReduceAnIntegerToZeroTest {

  private Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(54, 3),
        Arguments.of(1, 1),
        Arguments.of(84, 3),
        Arguments.of(870, 5),
        Arguments.of(57410, 4),
        Arguments.of(8254, 3),
        Arguments.of(97123, 7),
        Arguments.of(7862, 5));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void minimumOperationsToReduceAnIntegerToZero(int n, int expected) {
    var solver = new MinimumOperationsToReduceAnIntegerToZeroImpl();
    Assertions.assertEquals(expected, solver.minOperations(n));
  }
}
