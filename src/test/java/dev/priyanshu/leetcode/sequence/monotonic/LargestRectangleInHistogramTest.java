package dev.priyanshu.leetcode.sequence.monotonic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LargestRectangleInHistogramTest {

    Stream<Arguments> testCases(){
       return Stream.of(Arguments.of(new int[] {2,1,5,6,2,3}, 10),
               Arguments.of(new int[]{2, 4}, 4),
               Arguments.of(new int[]{1}, 1),
               Arguments.of(new int[]{1, 1}, 2),
               Arguments.of(new int[]{2, 1, 2}, 3));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] heights, int expected){
        var solver = new LargestRectangleInHistogramImpl();
        Assertions.assertEquals(expected, solver.largestRectangleArea(heights));
    }

}