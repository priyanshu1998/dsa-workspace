package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumAverageSubarrayTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,12,-5,-6,50,3}, 4, 12.75000),
                Arguments.of(new int[]{5}, 1, 5.0000)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, double expected){
        var solver = new MaximumAverageSubarrayImpl();
        Assertions.assertEquals(expected, solver.findMaxAverage(nums, k));
    }
}