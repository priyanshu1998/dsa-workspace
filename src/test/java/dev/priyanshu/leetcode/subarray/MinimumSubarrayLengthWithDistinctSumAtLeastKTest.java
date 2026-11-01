package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumSubarrayLengthWithDistinctSumAtLeastKTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2,2,3,1}, 4, 2),
                Arguments.of(new int[]{3,2,3,4}, 5, 2),
                Arguments.of(new int[]{5,5,4}, 5, 1),
                Arguments.of(new int[]{63,47,9,23,47,29,47,23,36,47,9}, 121, 4)
        );
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.MethodSource("testCases")
    void testMinLength(int[] nums, int k, int expected){
        var solver = new MinimumSubarrayLengthWithDistinctSumAtLeastKImpl();
        int actual = solver.minLength(nums, k);
        assertEquals(expected, actual);
    }
}