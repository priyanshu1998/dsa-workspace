package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumOperationsToReduceXToZeroTest {
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,1,4,2,3}, 5, 2),
                Arguments.of(new int[]{5,6,7,8,9}, 4, -1),
                Arguments.of(new int[]{3,2,20,1,1,3}, 10, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int x, int expected){
        var solver = new MinimumOperationsToReduceXToZeroImpl();
        Assertions.assertEquals(expected, solver.minOperations(nums, x));
    }
}