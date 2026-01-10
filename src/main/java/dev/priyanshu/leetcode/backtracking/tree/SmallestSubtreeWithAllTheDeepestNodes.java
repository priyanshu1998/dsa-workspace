package dev.priyanshu.leetcode.backtracking.tree;

import dev.priyanshu.annotation.Leetcode;

@Leetcode(id = 865, name = "smallest-subtree-with-all-the-deepest-nodes")
public interface SmallestSubtreeWithAllTheDeepestNodes {
  TreeNode subtreeWithAllDeepest(TreeNode root);
}

class SmallestSubtreeWithAllTheDeepestNodesImpl implements SmallestSubtreeWithAllTheDeepestNodes {

  record Result(TreeNode node, int depth) {}

  private Result dfs(TreeNode root) {
    if (root == null) return new Result(null, 0);

    Result left = dfs(root.left);
    Result right = dfs(root.right);

    if (left.depth() > right.depth()) return new Result(left.node(), left.depth() + 1);
    if (right.depth() > left.depth()) return new Result(right.node(), right.depth() + 1);

    return new Result(root, left.depth() + 1);
  }

  @Override
  public TreeNode subtreeWithAllDeepest(TreeNode root) {
    return dfs(root).node;
  }
}
