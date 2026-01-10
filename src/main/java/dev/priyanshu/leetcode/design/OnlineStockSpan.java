package dev.priyanshu.leetcode.design;

import dev.priyanshu.annotation.Leetcode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Leetcode(id = 901, name = "online-stock-span")
public class OnlineStockSpan {
  Stack<Integer> span;
  List<Integer> priceList;
  int nextIndex = 0;

  public OnlineStockSpan() {
    span = new Stack<>();
    priceList = new ArrayList<>();

    priceList.add(100001);
    span.push(0);
    nextIndex = 1;
  }

  public int next(int price) {
    priceList.add(price);

    // System.out.println(span);
    // System.out.println(priceList);
    while (price >= priceList.get(span.peek())) {
      span.pop();
    }

    int currIndex = nextIndex++;
    int diff = currIndex - span.peek();
    span.push(currIndex);
    return diff;
  }
}
