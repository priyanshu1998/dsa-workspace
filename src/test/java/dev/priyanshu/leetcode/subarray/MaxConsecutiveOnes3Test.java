package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaxConsecutiveOnes3Test {
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,1,1,0,0,0,1,1,1,1,0}, 2, 6),
                Arguments.of(new int[]{0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1}, 3, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new MaxConsecutiveOnes3Impl();
        Assertions.assertEquals(expected, solver.longestOnes(nums, k));
    }
}