package dev.priyanshu.leetcode.combination;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NumberOfWaysToPaintNx3GridTest {

  Stream<Arguments> testCases() {
    return Stream.of(Arguments.of(1, 12), Arguments.of(2, 54), Arguments.of(5000, 30228214));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void numOfWays(int n, int expected) {
    var solver = new NumberOfWaysToPaintNx3GridImpl();
    int actual = solver.numOfWays(n);

    Assertions.assertEquals(expected, actual);
  }
}
