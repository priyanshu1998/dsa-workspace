package dev.priyanshu.structy.count_subarray_product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubarrayProductTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {2, 4, 3, 10}, 31, 8),
        Arguments.of(new int[] {1, 2, 3, 4, 5, 6}, 20, 10),
        Arguments.of(new int[] {2, 1, 1, 2, 3, 4, 5, 6}, 8, 17),
        Arguments.of(
            new int[] {1, 2, 3, 1, 10, 1, 1, 1, 1, 2, 1, 15, 2, 1, 1, 1, 1, 1, 5, 1}, 20, 79),
        Arguments.of(new int[] {5, 10, 15}, 5, 0),
        Arguments.of(new int[] {3, 3, 3}, 1, 0),
        Arguments.of(new int[] {4}, 1, 0));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, long target, long expected) {
    var solver = new OptimizedSolution();
    assertEquals(expected, solver.countSubarrayProduct(nums, target));
  }
}
