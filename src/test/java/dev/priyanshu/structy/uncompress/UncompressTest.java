package dev.priyanshu.structy.uncompress;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UncompressTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("2c3a1t", "ccaaat"),
        Arguments.of("4s2b", "ssssbb"),
        Arguments.of("2p1o5p", "ppoppppp"),
        Arguments.of("3n12e2z", "nnneeeeeeeeeeeezz"),
        Arguments.of(
            "127y",
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, String expected) {
    var solver = new UncompressImpl();
    Assertions.assertEquals(expected, solver.uncompress(s));
  }
}
