package dev.priyanshu.structy.count_substring_at_most_k_distinct;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubstringAtMostKDistinctTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("gattc", 3, 14),
        Arguments.of("gattc", 2, 11),
        Arguments.of("abacd", 3, 13),
        Arguments.of("racetracks", 4, 34),
        Arguments.of("racetracks", 3, 27),
        Arguments.of("racetracks", 2, 19),
        Arguments.of("ab", 1, 2),
        Arguments.of("ab", 2, 3));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, int k, long expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.countSubstringAtMostKDistinct(s, k));
  }
}
