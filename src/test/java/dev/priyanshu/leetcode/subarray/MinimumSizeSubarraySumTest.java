package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumSizeSubarraySumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2,3,1,2,4,3}, 7, 2),
                Arguments.of(new int[]{1,4,4}, 4, 1),
                Arguments.of(new int[]{1,1,1,1,1,1,1,1}, 11, 0)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int target, int expected){
        var solver = new MinimumSizeSubarraySumImpl();
        Assertions.assertEquals(expected, solver.minSubArrayLen(target, nums));
    }
}