package dev.priyanshu.leetcode.backtracking;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BestTimeToBuyAndSellStock2Test {

  Stream<Arguments> testCases() {
    return Stream.of(Arguments.of(new int[]{7,1,5,3,6,4}, 7),
            Arguments.of(new int[]{1,2,3,4,5}, 4),
            Arguments.of(new int[]{7,6,4,3,1}, 0));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void maxProfit(int[] prices, int expected) {
    var solver = new BestTimeToBuyAndSellStock2Impl();
    Assertions.assertEquals(expected, solver.maxProfit(prices));
  }
}
