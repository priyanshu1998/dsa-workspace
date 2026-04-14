package dev.priyanshu.structy.has_substring_anagram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HasSubstringAnagramTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("greyhounds", "hoy", true),
        Arguments.of("gruyheonds", "hoy", false),
        Arguments.of("breakdowns", "snow", true),
        Arguments.of("dermatoglyphics", "red", true),
        Arguments.of("southernly", "thorny", false),
        Arguments.of("southernly", "nerlysouth", true));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, String anagram, boolean expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.hasSubstringAnagram(s, anagram));
  }

  @Test
  void singleTest() {
    var solver = new NaiveSolution();
    assertFalse(solver.hasSubstringAnagram("gruyheonds", "hoy"));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void testMostOptimized(String s, String anagram, boolean expected) {
    var solver = new MostOptimizedSolution();
    assertEquals(expected, solver.hasSubstringAnagram(s, anagram));
  }
}
