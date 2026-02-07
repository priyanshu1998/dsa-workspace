package dev.priyanshu.leetcode.search.linear;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DivideAnArrayIntoSubarraysWithMinimumCost1Test {

  public static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 3, 12}, 6), Arguments.of(new int[] {10, 3, 1, 1}, 12));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] arr, int expected) {
    var solver = new DivideAnArrayIntoSubarraysWithMinimumCost1Impl();
    Assertions.assertEquals(expected, solver.minimumCost(arr));
  }
}
