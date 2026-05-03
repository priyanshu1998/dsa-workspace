package dev.priyanshu.structy.is_palindrome;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IsPalindromeTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("pop", true),
        Arguments.of("kayak", true),
        Arguments.of("pops", false),
        Arguments.of("boot", false),
        Arguments.of("rotator", true),
        Arguments.of("abcbca", false),
        Arguments.of("", true));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, boolean expected) {
    var solver = new IsPalindromeImpl();
    Assertions.assertEquals(expected, solver.isPalindrome(s));
  }
}
