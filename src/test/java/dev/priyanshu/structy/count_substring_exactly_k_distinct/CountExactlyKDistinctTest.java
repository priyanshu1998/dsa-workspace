package dev.priyanshu.structy.count_substring_exactly_k_distinct;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountExactlyKDistinctTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("gattc", 3, 3),
        Arguments.of("abacd", 3, 3),
        Arguments.of("racetracks", 4, 7),
        Arguments.of("pqpqs", 2, 7),
        Arguments.of("aabacbebebe", 3, 12),
        Arguments.of("serenenethers", 4, 17));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, int k, long expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.countSubstringExactlyKDistinct(s, k));
  }
}
