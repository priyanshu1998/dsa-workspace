package dev.priyanshu.leetcode.sequence.monotonic;

import dev.priyanshu.annotation.Leetcode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Leetcode(id = 503, name = "next-greater-element-ii")
public interface NextGreaterElement2 {
  int[] nextGreaterElements(int[] nums);
}

class NextGreaterElement2Impl implements NextGreaterElement2 {

  // Monotonically decreasing
  Stack<Integer> stack = new Stack<>();

  void push(int v) {
    try {
      while (v >= stack.peek()) {
        stack.pop();
      }
    } catch (Exception ex) {
      // do nothing
    } finally {
      stack.push(v);
    }
  }

  int nextMax(int v) {
    try {
      while (v >= stack.peek()) {
        stack.pop();
      }
    } catch (Exception ex) {
      return -1;
    }

    return stack.peek();
  }

  @Override
  public int[] nextGreaterElements(int[] nums) {
    List<Integer> output = new ArrayList<>();
    for (int i = nums.length - 1; i >= 0; i--) {
      push(nums[i]);
    }

    for (int i = nums.length - 1; i >= 0; i--) {
      int v = nextMax(nums[i]);
      output.add(v);
      stack.add(nums[i]);
    }

    Collections.reverse(output);
    return output.stream().mapToInt(Integer::intValue).toArray();
  }
}
