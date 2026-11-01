package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountPrimeGapBalancedSubarraysTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3}, 1, 2),
                Arguments.of(new int[]{2,3,5,7}, 3, 4)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new CountPrimeGapBalancedSubarraysImpl();
        assertEquals(expected, solver.primeSubarray(nums, k));
    }
}