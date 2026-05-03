package dev.priyanshu.structy.is_subsequence;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IsSubsequenceTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("bde", "abcdef", true),
        Arguments.of("bda", "abcdef", false),
        Arguments.of("ser", "super", true),
        Arguments.of("serr", "super", false),
        Arguments.of("ama", "camera", true),
        Arguments.of("unfun", "unfortunate", true),
        Arguments.of("riverbed", "river", false),
        Arguments.of("river", "riverbed", true));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, String t, boolean expected) {
    var solver = new IsSubsequenceImpl();
    Assertions.assertEquals(expected, solver.isSubsequence(s, t));
  }
}
