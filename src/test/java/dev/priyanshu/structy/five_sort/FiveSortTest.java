package dev.priyanshu.structy.five_sort;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FiveSortTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {5, 0}, new int[] {0, 5}),
        Arguments.of(new int[] {12, 5, 1, 5, 12, 7}, new int[] {12, 7, 1, 12, 5, 5}),
        Arguments.of(
            new int[] {5, 2, 5, 6, 5, 1, 10, 2, 5, 5}, new int[] {2, 2, 10, 6, 1, 5, 5, 5, 5, 5}),
        Arguments.of(new int[] {5, 5, 5, 1, 1, 1, 4}, new int[] {4, 1, 1, 1, 5, 5, 5}),
        Arguments.of(new int[] {5, 5, 6, 5, 5, 5, 5}, new int[] {6, 5, 5, 5, 5, 5, 5}),
        Arguments.of(
            new int[] {5, 1, 2, 5, 5, 3, 2, 5, 1, 5, 5, 5, 4, 5},
            new int[] {4, 1, 2, 1, 2, 3, 5, 5, 5, 5, 5, 5, 5, 5}));
  }

  private int countOf5(int[] nums) {
    int count = 0;
    for (int i = 0; i < nums.length; i++) {
      count += 1;
    }

    return count;
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void testCases(int[] nums) {
    int count = countOf5(nums);
    var solver = new FiveSortImpl2();
    solver.sort(nums);

    int i = nums.length - 1;
    while (count != 0) {
      if (nums[i] != 5) {
        Assertions.fail();
      }
      count--;
    }
  }
}
