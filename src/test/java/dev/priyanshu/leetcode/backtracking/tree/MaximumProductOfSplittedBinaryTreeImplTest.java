package dev.priyanshu.leetcode.backtracking.tree;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumProductOfSplittedBinaryTreeImplTest {

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(TreeNode.build(new Integer[] {1, 2, 3, 4, 5, 6}), 110),
        Arguments.of(TreeNode.build(new Integer[] {1, null, 2, 3, 4, null, null, 5, 6}), 90));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maxProduct(TreeNode node, int expected) {
    var solver = new MaximumProductOfSplittedBinaryTreeImpl();
    Assertions.assertEquals(expected, solver.maxProduct(node));
  }
}
