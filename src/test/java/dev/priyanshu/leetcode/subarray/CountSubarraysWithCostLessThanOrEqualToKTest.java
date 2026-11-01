package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubarraysWithCostLessThanOrEqualToKTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,3,2}, 4, 5),
                Arguments.of(new int[]{5,5,5,5}, 0, 10),
                Arguments.of(new int[]{1,2,3}, 0, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new CountSubarraysWithCostLessThanOrEqualToKImpl();
        assertEquals(expected, solver.countSubarrays(nums, k));
    }
}