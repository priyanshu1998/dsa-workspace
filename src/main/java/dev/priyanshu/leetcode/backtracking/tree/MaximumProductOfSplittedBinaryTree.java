package dev.priyanshu.leetcode.backtracking.tree;

import java.util.function.Supplier;

public interface MaximumProductOfSplittedBinaryTree {
  int maxProduct(TreeNode root);
}

class MaximumProductOfSplittedBinaryTreeImpl implements MaximumProductOfSplittedBinaryTree {

  private static final long M = 1000_000_007;

  private Supplier<Integer> sum(TreeNode node) {
    return new Supplier<Integer>() {
      Integer sum = null;

      @Override
      public Integer get() {
        if (sum != null) return sum;
        return this.sum = sum(node);
      }

      int sum(TreeNode node) {
        if (node == null) return 0;

        return node.val + sum(node.left) + sum(node.right);
      }
    };
  }

  Supplier<Integer> treeSum;

  record Result(int product, int subTreeSum) {}

  private Result dfs(TreeNode root) {
    if (root == null) return new Result(0, 0);

    var leftResult = dfs(root.left);
    var rightResult = dfs(root.right);

    int sum = root.val + leftResult.subTreeSum + rightResult.subTreeSum;
    int product =
        Math.max((treeSum.get() - sum) * sum, Math.max(leftResult.product, rightResult.product));
    return new Result(product, sum);
  }

  @Override
  public int maxProduct(TreeNode root) {
    treeSum = sum(root);
    return Math.toIntExact(dfs(root).product % M);
  }
}
