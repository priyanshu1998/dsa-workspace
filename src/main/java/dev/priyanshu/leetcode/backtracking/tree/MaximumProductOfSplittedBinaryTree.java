package dev.priyanshu.leetcode.backtracking.tree;

public interface MaximumProductOfSplittedBinaryTree {
  int maxProduct(TreeNode root);
}

class MaximumProductOfSplittedBinaryTreeImpl implements MaximumProductOfSplittedBinaryTree {

  private static final long M = 1000_000_007;
  private int sum = 0;

  private TreeNode sumTree(TreeNode root) {
    if (root == null) return null;
    var leftNode = sumTree(root.left);
    var rightNode = sumTree(root.right);

    int sum = root.val;
    sum += (leftNode != null) ? leftNode.val : 0;
    sum += (rightNode != null) ? rightNode.val : 0;

    return new TreeNode(sum, leftNode, rightNode);
  }

  private int product(TreeNode root) {
    if (root == null) return 0;
    int product = Math.toIntExact((long) (sum - root.val) * root.val);
    int childTreeMaxProduct = Math.max(product(root.left), product(root.right));
    //        System.out.printf("%s %s\n", root.val, product);
    return Math.max(product, childTreeMaxProduct);
  }

  @Override
  public int maxProduct(TreeNode root) {
    TreeNode sumTreeRoot = sumTree(root);
    sum = sumTreeRoot.val;
    return Math.toIntExact(product(sumTreeRoot) % M);
  }
}
