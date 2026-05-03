package dev.priyanshu.structy.binary_search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BinarySearchTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 6, 6),
        Arguments.of(new int[] {0, 6, 8, 12, 16, 19, 20, 24, 28}, 27, -1),
        Arguments.of(new int[] {0, 6, 8, 12, 16, 19, 20, 28}, 8, 2),
        Arguments.of(new int[] {0, 6, 8, 12, 16, 19, 20, 24, 28}, 28, 8),
        Arguments.of(new int[] {7, 9}, 7, 0),
        Arguments.of(new int[] {7, 9}, 9, 1),
        Arguments.of(new int[] {7, 9}, 12, -1),
        Arguments.of(new int[] {7}, 7, 0),
        Arguments.of(new int[] {}, 7, -1));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, int target, int expected) {
    var solver = new BinarySearchImpl();
    assertEquals(expected, solver.search(nums, target));
  }
}
