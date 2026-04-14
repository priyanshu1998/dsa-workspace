package dev.priyanshu.structy.longest_unique_substring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestUniqueSubstringTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("abcabcqbb", 4),
        Arguments.of("forgeonwards", 9),
        Arguments.of("tmmzuxt", 5),
        Arguments.of("racecarisgreatness", 8),
        Arguments.of("zzzzzz", 1),
        Arguments.of("zqzzzz", 2));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, int expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.longestUniqueSubstring(s));
  }
}
