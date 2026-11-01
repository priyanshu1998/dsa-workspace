package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContainsDuplicate2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,1}, 3, true),
                Arguments.of(new int[]{1,0,1,1}, 1, true),
                Arguments.of(new int[]{1,2,3,1,2,3}, 2, false)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, boolean expected){
        var solver = new ContainsDuplicate2Impl();
        Assertions.assertEquals(expected, solver.containsNearbyDuplicate(nums, k));
    }
}