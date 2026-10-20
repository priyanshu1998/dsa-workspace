package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumLimitOfBallsInABagTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{9}, 2, 3),
                Arguments.of(new int[]{2,4,8,2}, 4, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int maxOperations, int expected){
        var solver = new MinimumLimitOfBallsInABagImpl();
        Assertions.assertEquals(expected, solver.minimumSize(nums, maxOperations));
    }
}