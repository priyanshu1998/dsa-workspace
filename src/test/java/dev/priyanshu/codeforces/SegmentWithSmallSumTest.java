package dev.priyanshu.codeforces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SegmentWithSmallSumTest {

  Stream<Arguments> testCases() {
    return Stream.of(Arguments.of(7, new long[] {2, 6, 4, 3, 6, 8, 9}, 20, 4));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int n, long[] nums, long target, long expected) {
    SegmentWithSmallSum solver = new SegmentWithSmallSumImpl();
    assertEquals(expected, solver.longestGoodSegment(n, nums, target));
  }
}
