package dev.priyanshu.structy.maximum_subarray_product;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumSubarrayProductTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new long[] {4, 2, 1, -9, 8, 2, 3}, 3, 48),
        Arguments.of(new long[] {-9, 1, -8, 2, 3, 7}, 3, 72),
        Arguments.of(new long[] {7, 4, -5, -7, 8, -10, -1}, 2, 35),
        Arguments.of(new long[] {60, 20, 10, 90, 50}, 1, 90),
        Arguments.of(new long[] {1, 2, 3, 4}, 4, 24));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maximumSubArrayProduct(long[] nums, int k, long expected) {
    var solver = new Solution();
    var actual = solver.maximumSubarrayProduct(nums, k);

    Assertions.assertEquals(expected, actual);
  }
}
