package dev.priyanshu.structy.reverse_string_recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReverseStringRecursiveTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("hello", "olleh"),
        Arguments.of("abcdefg", "gfedcba"),
        Arguments.of("stopwatch", "hctawpots"),
        Arguments.of("", ""));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, String expected) {
    var solver = new ReverseStringTailRecursion();
    assertEquals(expected, solver.reverseString(s));
  }
}
