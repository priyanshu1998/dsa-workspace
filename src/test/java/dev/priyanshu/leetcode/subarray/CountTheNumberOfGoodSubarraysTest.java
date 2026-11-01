package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountTheNumberOfGoodSubarraysTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,1,1,1,1}, 10, 1),
                Arguments.of(new int[]{3,1,4,3,2,2,4}, 2, 4),
                Arguments.of(new int[]{1,1,1,1}, 2, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new CountTheNumberOfGoodSubarraysImpl();
        Assertions.assertEquals(expected, solver.countGood(nums, k));
    }
}