package dev.priyanshu.structy.max_ones_with_single_flip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaxOnesWithSingleFlipTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("10110110", 5),
        Arguments.of("011101101111", 7),
        Arguments.of("111", 3),
        Arguments.of("01101", 4),
        Arguments.of("10110111011110111011111", 9));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, int expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.maxOnesWithSingleFlip(s));
  }
}
