package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BinarySubarraysWithSumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,0,1,0,1}, 2, 4),
                Arguments.of(new int[]{0,0,0,0,0}, 0, 15)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[]nums, int goal, int expected){
        var solver = new BinarySubarraysWithSumImpl();
        Assertions.assertEquals(expected, solver.numSubarraysWithSum(nums, goal));
    }
}