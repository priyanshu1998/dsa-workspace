package dev.priyanshu.hackerrank.dp.contruct_the_array;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConstructTheArrayTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(4, 3, 2, 3), Arguments.of(5, 2, 2, 0), Arguments.of(761, 99, 1, 236568308));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void countArray(int n, int k, int x, long expected) {
    Assertions.assertEquals(expected, Result.countArray(n, k, x));
  }
}
