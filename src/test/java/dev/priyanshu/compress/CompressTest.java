package dev.priyanshu.compress;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompressTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("ccaaatsss", "2c3at3s"),
        Arguments.of("ssssbbz", "4s2bz"),
        Arguments.of("ppoppppp", "2po5p"),
        Arguments.of("nnneeeeeeeeeeeezz", "3n12e2z"),
        Arguments.of(
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
            "127y"));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String s, String expected) {
    var solver = new CompressImpl();
    Assertions.assertEquals(expected, solver.compress(s));
  }
}
