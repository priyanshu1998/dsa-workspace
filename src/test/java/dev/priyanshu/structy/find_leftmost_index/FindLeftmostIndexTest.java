package dev.priyanshu.structy.find_leftmost_index;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindLeftmostIndexTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {1, 2, 3, 3, 3, 4, 5, 6, 6, 7, 8, 8, 8, 9}, 3, 2),
        Arguments.of(new int[] {1, 2, 3, 3, 3, 4, 5, 6, 6, 7, 8, 8, 8, 9}, 4, 5),
        Arguments.of(new int[] {1, 2, 3, 3, 3, 4, 5, 6, 6, 7, 8, 8, 8, 9}, 12, -1),
        Arguments.of(new int[] {2, 2, 5, 7, 8, 8, 10, 10, 10, 12, 15, 18, 20}, 10, 6),
        Arguments.of(new int[] {42}, 42, 0),
        Arguments.of(new int[] {}, 42, -1));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] nums, int target, int expected) {
    var solver = new FindLeftmostIndexImpl();
    assertEquals(expected, solver.index(nums, target));
  }
}
