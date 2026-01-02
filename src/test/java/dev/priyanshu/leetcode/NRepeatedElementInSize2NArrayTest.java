package dev.priyanshu.leetcode;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NRepeatedElementInSize2NArrayTest {

  private Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 3, 3}, 3),
        Arguments.of(new int[] {2, 1, 2, 5, 3, 2}, 2),
        Arguments.of(new int[] {5, 1, 5, 2, 5, 3, 5, 4}, 5));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void nRepeatedElementInSize2NArray(int[] nums, int expected) {
    var solution = new MajorityInGroupOf3();
    var actual = solution.repeatedNTimes(nums);

    Assertions.assertEquals(expected, actual);
  }
}
