package dev.priyanshu.structy.sum_numbers_recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SumNumbersRecursiveTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {5, 2, 9, 10}, 26),
        Arguments.of(new int[] {1, -1, 1, -1, 1, -1, 1}, 1),
        Arguments.of(new int[] {}, 0),
        Arguments.of(new int[] {700, 70, 7}, 777),
        Arguments.of(new int[] {-10, -9, -8, -7, -6, -5, -4, -3, -2, -1}, -55),
        Arguments.of(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 0),
        Arguments.of(
            new int[] {123456789, 12345678, 1234567, 123456, 12345, 1234, 123, 12, 1, 0},
            137174205));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, long expected) {
    var solver = new SumNumbersTailRecursiveImpl();
    assertEquals(expected, solver.sumNumbersRecursive(nums));
  }
}
