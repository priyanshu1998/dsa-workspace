package dev.priyanshu.structy.sum_of_lengths;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SumOfLengthsTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new String[] {"goat", "cat", "purple"}, 13),
        Arguments.of(new String[] {"bike", "at", "pencils", "phone"}, 18),
        Arguments.of(new String[] {}, 0),
        Arguments.of(new String[] {"", " ", "  ", "   ", "    ", "     "}, 15),
        Arguments.of(new String[] {"0", "313", "1234567890"}, 14));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(String[] strings, int expected) {
    var solver = new SumOfLengthTailRecursion();
    assertEquals(expected, solver.getLength(strings));
  }
}
