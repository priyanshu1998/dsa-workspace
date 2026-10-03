package dev.priyanshu.leetcode.search.linear;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximumValueOfAnOrderedTriplet2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{12,6,1,2,7}, 77),
                Arguments.of(new int[]{1,10,3,4,19}, 133),
                Arguments.of(new int[]{1,2,3}, 0),
                Arguments.of(new int[]{2,3,1}, 0));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new MaximumValueOfAnOrderedTriplet2Impl();
        Assertions.assertEquals(expected, solver.maximumTripletValue(nums));
    }
}