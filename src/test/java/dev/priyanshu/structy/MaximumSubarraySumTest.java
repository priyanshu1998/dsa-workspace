package dev.priyanshu.structy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumSubarraySumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[] {1, 4, 1, 10, 25, 3, 5, 0, 26}, 4, 43L)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void maxSubarraySum(int[] nums, int k, long excepted){
        var solver = new OptimizedSolution();
        Assertions.assertEquals(excepted, solver.maximumSubarraySum(nums, k));
    }
}