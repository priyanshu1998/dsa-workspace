package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubarraysWithKDistinctIntegersTest {

    Stream<Arguments> testCases(){
        return Stream.of(
//                Arguments.of(new int[]{1,2,1,2,2}, 2, 2, 2),
//                Arguments.of(new int[]{3,1,2,4}, 2, 1, 3),
                Arguments.of(new int[]{1,1,9966}, 1, 2, 1)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int m, int expected){
        var solver = new CountSubarraysWithKDistinctIntegersImpl();
        assertEquals(expected, solver.countSubarrays(nums, k, m));
    }
}