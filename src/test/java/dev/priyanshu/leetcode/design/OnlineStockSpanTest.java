package dev.priyanshu.leetcode.design;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OnlineStockSpanTest {

  @Test
  void test() {
    String[] cmdArr =
        new String[] {"StockSpanner", "next", "next", "next", "next", "next", "next", "next"};
    List<String[]> args =
        List.of(
            new String[] {},
            new String[] {"100"},
            new String[] {"80"},
            new String[] {"60"},
            new String[] {"70"},
            new String[] {"60"},
            new String[] {"75"},
            new String[] {"85"});

    var expected = new Integer[] {null, 1, 1, 1, 2, 1, 4, 6};
    var actual = new Integer[expected.length];

    OnlineStockSpan stockSpanner = null;

    for (int i = 0; i < cmdArr.length; i++) {
      String cmd = cmdArr[i];
      String[] arg = args.get(i);
      switch (cmd) {
        case "StockSpanner" -> {
          stockSpanner = new OnlineStockSpan();
          actual[i] = null;
        }
        case "next" -> {
          actual[i] = stockSpanner.next(Integer.parseInt(arg[0]));
        }
        default -> {
          // do nothing
        }
      }
    }

    Assertions.assertArrayEquals(expected, actual);
  }
}
