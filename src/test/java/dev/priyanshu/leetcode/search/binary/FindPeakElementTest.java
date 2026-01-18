package dev.priyanshu.leetcode.search.binary;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindPeakElementTest {

  Stream<int[]> testCases() {
    return Stream.of(
        new int[] {9, 7, 3, 7, 8},
        new int[] {1, 2, 1, 3, 5, 6, 4},
        new int[] {9, 7, 3, 7, 8},
        new int[] {1},
        new int[] {4, 5},
        new int[] {9, 8},
        new int[] {3, 4, 3, 2, 1});
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums) {
    var solver = new FindPeakElementImpl();
    var idx = solver.findPeakElement(nums);

    final int n = nums.length;

    if (nums.length == 1) Assertions.assertEquals(0, idx);
    else if (idx == 0) Assertions.assertTrue(nums[0] > nums[1]);
    else if (idx == nums.length - 1) Assertions.assertTrue(nums[n - 1] > nums[n - 2]);
    else {
      Assertions.assertTrue(nums[idx] > nums[idx - 1] && nums[idx] > nums[idx + 1]);
    }
  }
}
