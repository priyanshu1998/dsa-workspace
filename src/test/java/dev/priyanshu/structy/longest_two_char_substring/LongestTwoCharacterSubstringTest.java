package dev.priyanshu.structy.longest_two_char_substring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestTwoCharacterSubstringTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("xyzyyx", 4),
        Arguments.of("qrss", 3),
        Arguments.of("ababba", 6),
        Arguments.of("ttttttt", 0),
        Arguments.of("abacbbcbxadd", 5));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, int expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.longestTwoCharSubstring(s));
  }
}
