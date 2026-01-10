package dev.priyanshu.leetcode.backtracking.tree;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SmallestSubtreeWithAllTheDeepestNodesTest {

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(
            TreeNode.build(new Integer[] {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4}),
            new int[] {2, 7, 4}),
        Arguments.of(TreeNode.build(new Integer[] {1}), new int[] {1}),
        Arguments.of(TreeNode.build(new Integer[] {0, 1, 3, null, 2}), new int[] {2}));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void subtreeWithAllDeepest(TreeNode node, int[] expected) {
    var solver = new SmallestSubtreeWithAllTheDeepestNodesImpl();
    Assertions.assertArrayEquals(expected, solver.subtreeWithAllDeepest(node).preorderTraversal());
  }
}
