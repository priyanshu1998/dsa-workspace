package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestSubarrayWithSumKTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{10, 5, 2, 7, 1, 9}, 15, 4),
                Arguments.of(new int[]{-3, 2, 1}, 6, 0),
                Arguments.of(new int[]{-753,-169,-35,-252,-235,-959,533,-426,311,110,-359}, -2296, 8)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver =  new LongestSubarrayWithSumKImpl();
        Assertions.assertEquals(expected, solver.longestSubarray(nums, k));
    }
}