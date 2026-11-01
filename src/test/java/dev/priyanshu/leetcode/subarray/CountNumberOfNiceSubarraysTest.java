package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountNumberOfNiceSubarraysTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,1,2,1,1}, 3, 2),
                Arguments.of(new int[]{2,4,6}, 1, 0),
                Arguments.of(new int[]{2,2,2,1,2,2,1,2,2,2}, 2, 16)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new CountNumberOfNiceSubarraysImpl();
        Assertions.assertEquals(expected, solver.numberOfSubarrays(nums, k));
    }
}