package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumDiscardsToBalanceInventoryTest {

    Stream<Arguments> testCases(){
        return Stream.of(
//                Arguments.of(new int[]{1,2,1,3,1}, 4, 2, 0),
//                Arguments.of(new int[]{1,2,3,3,3,4}, 3, 2, 1),
                Arguments.of(new int[]{8,8,8,1,7,4,3,7,5,2}, 7, 1, 3),
                Arguments.of(new int[]{7,3,9,9,7,3,5,9,7,2,6,10,9,7,9,1,3,6,2,4,6,2,6,8,4,8,2,7,5,6}, 10, 1, 13)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] arrivals, int w, int m, int expected){
        var solver = new MinimumDiscardsToBalanceInventoryImpl();
        Assertions.assertEquals(expected, solver.minArrivalsToDiscard(arrivals, w, m));
    }
}