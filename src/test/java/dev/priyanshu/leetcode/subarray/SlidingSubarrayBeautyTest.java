package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlidingSubarrayBeautyTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,-1,-3,-2,3}, 3, 2, new int[]{-1,-2,-2}),
                Arguments.of(new int[]{-1,-2,-3,-4,-5}, 2,2,new int[]{-1,-2,-3,-4}),
                Arguments.of(new int[]{-3,1,2,-3,0,-3}, 2, 1, new int[]{-3,0,-3,-3,-3})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int x, int[] expected){
        var solver = new SlidingSubarrayBeautyImpl();
        Assertions.assertArrayEquals(expected, solver.getSubarrayBeauty(nums, k, x));
    }
}