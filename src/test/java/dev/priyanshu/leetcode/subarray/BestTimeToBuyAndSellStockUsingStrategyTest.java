package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BestTimeToBuyAndSellStockUsingStrategyTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{4,2,8}, new int[]{-1,0,1}, 2, 10),
                Arguments.of(new int[]{5,4,3}, new int[]{1,1,0}, 2, 9),
                Arguments.of(new int[]{5,14,16,9}, new int[]{-1,0,0,-1}, 2, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] prices, int[]strategy, int k, long expected){
        var solver = new BestTimeToBuyAndSellStockUsingStrategyImpl();
        Assertions.assertEquals(expected, solver.maxProfit(prices, strategy, k));
    }
}