package dev.priyanshu.leetcode;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DecodeXORedPermutationTest {

  private Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {3, 1}, new int[] {1, 2, 3}),
        Arguments.of(new int[] {6, 5, 4, 6}, new int[] {2, 4, 1, 5, 3}));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void decodeXORedPermutation(int[] encoded, int[] expected) {
    var solver = new DecodeXORedPermutation();
    var actualArray = solver.decode(encoded);

    Assertions.assertArrayEquals(expected, actualArray);
  }
}
