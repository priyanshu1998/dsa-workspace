package dev.priyanshu.leetcode.sequence;

import dev.priyanshu.annotation.Leetcode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

@Leetcode(id = 739, name = "daily-temperatures")
public interface DailyTemperatures {
  int[] dailyTemperatures(int[] temperatures);
}

class DailyTemperaturesImpl implements DailyTemperatures {

  int[] a;
  Stack<Integer> stack = new Stack<>();

  void setA(int[] a) {
    this.a = a;
  }

  int nextMax(int i) {
    int v = a[i];
    try {
      while (v >= a[stack.peek()]) {
        stack.pop();
      }
    } catch (EmptyStackException ignored) {
      return 0;
    }

    return stack.peek();
  }

  @Override
  public int[] dailyTemperatures(int[] temperatures) {
    setA(temperatures);
    List<Integer> result = new ArrayList<>();
    for (int i = temperatures.length - 1; i >= 0; i--) {
      int v = nextMax(i);
      result.add((v != 0) ? v - i : 0);
      stack.push(i);
    }

    Collections.reverse(result);
    return result.stream().mapToInt(Integer::intValue).toArray();
  }
}
