package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindThePowerOfKSizeSubarrays2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
//                Arguments.of(new int[]{1,2,3,4,3,2,5}, 3, new int[]{3,4,-1,-1,-1}),
//                Arguments.of(new int[]{2,2,2,2,2}, 4, new int[]{-1,-1}),
//                Arguments.of(new int[]{3,2,3,2,3,2}, 2, new int[]{-1,3,-1,3,-1}),
                Arguments.of(new int[]{1, 1}, 1, new int[]{1, 1})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int[] expected){
        var solver = new FindThePowerOfKSizeSubarrays2Impl();
        Assertions.assertArrayEquals(expected, solver.resultsArray(nums, k));
    }
}