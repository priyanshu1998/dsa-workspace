package dev.priyanshu.structy.subarray_target_sum_size_k;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubarrayTargetSumSizeKTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2, 3, 2, 2, 3, 1, 3, 8, 5, 0, 2, 4}, 7, 3, 5),
                Arguments.of(new int[]{2,3,2}, 7,3,1),
                Arguments.of(new int[]{1, 2, 2, 2, 2, 4, 6, 5, 1, 2, 0, 10, -2, 7}, 8,4,2)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, long target, int k, long expected){
        var solver = new OptimizedSolution();
        assertEquals(expected, solver.subarrayTargetSumSizeK(nums, target, k));
    }
}