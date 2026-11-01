package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumCandiesAllocatedToKChildrenTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{5,8,6},3,5),
                Arguments.of(new int[]{2, 5}, 11, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] candies, long k, int expected){
        var solver = new MaximumCandiesAllocatedToKChildrenImpl();
        Assertions.assertEquals(expected, solver.maximumCandies(candies, k));
    }
}