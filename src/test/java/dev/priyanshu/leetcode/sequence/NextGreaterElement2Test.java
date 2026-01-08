package dev.priyanshu.leetcode.sequence;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NextGreaterElement2Test {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 1}, new int[] {2, -1, 2}),
        Arguments.of(new int[] {1, 2, 3, 4, 3}, new int[] {2, 3, 4, -1, 4}));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void nextGreaterElements(int[] nums, int[] expected) {
    var solver = new NextGreaterElement2Impl();
    var actual = solver.nextGreaterElements(nums);
    //        System.out.println(Arrays.toString(actual));
    Assertions.assertArrayEquals(expected, actual);
  }
}
