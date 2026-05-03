package dev.priyanshu.structy.squre_root;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SquareRootTest {
  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(16, 4),
        Arguments.of(81, 9),
        Arguments.of(9, 3),
        Arguments.of(1, 1),
        Arguments.of(64, 8),
        Arguments.of(67, 8),
        Arguments.of(196, 14),
        Arguments.of(204, 14));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(long num, int expected) {
    var solver = new SquareRootImpl();
    Assertions.assertEquals(expected, solver.sqRoot(num));
  }
}
