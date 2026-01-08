package dev.priyanshu.leetcode.backtracking.tree;

import java.util.ArrayDeque;
import java.util.Queue;

public class TreeNode {
  int val;
  TreeNode left;
  TreeNode right;

  TreeNode() {}

  TreeNode(int val) {
    this.val = val;
  }

  TreeNode(int val, TreeNode left, TreeNode right) {
    this.val = val;
    this.left = left;
    this.right = right;
  }

  public static TreeNode build(Integer[] arr) {
    if (arr == null || arr.length == 0 || arr[0] == null) return null;

    Queue<TreeNode> q = new ArrayDeque<>();
    TreeNode root = new TreeNode(arr[0]);
    q.offer(root);

    int i = 1;
    while (!q.isEmpty() && i < arr.length) {
      TreeNode cur = q.poll();

      if (i < arr.length && arr[i] != null) {
        cur.left = new TreeNode(arr[i]);
        q.offer(cur.left);
      }
      i++;

      if (i < arr.length && arr[i] != null) {
        cur.right = new TreeNode(arr[i]);
        q.offer(cur.right);
      }
      i++;
    }
    return root;
  }
}
