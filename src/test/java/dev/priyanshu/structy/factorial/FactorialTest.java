package dev.priyanshu.structy.factorial;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FactorialTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(3, 6),
        Arguments.of(6, 720),
        Arguments.of(18, 6402373705728000L),
        Arguments.of(1, 1),
        Arguments.of(13, 6227020800L),
        Arguments.of(0, 1));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int n, long expected) {
    var solver = new FactorialTailRecursionImpl();
    assertEquals(expected, solver.factorial(n));
  }
}
