package dev.priyanshu.leetcode.backtracking;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumAsciiDeleteSumForTwoStringsTest {

  Stream<Arguments> testCases() {
    return Stream.of(Arguments.of("sea", "eat", 231), Arguments.of("delete", "leet", 403));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s1, String s2, int expected) {
    var solver = new MinimumAsciiDeleteSumForTwoStringsImpl();
    Assertions.assertEquals(expected, solver.minimumDeleteSum(s1, s2));
  }
}
