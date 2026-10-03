package dev.priyanshu.leetcode.search.linear;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MajorityElementTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,2,3}, 3),
                Arguments.of(new int[]{2,2,1,1,1,2,2}, 2),
                Arguments.of(new int[]{1,1,2,2,3}, -1),
                Arguments.of(new int[]{1,2,3}, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new MajorityElementImpl();
        Assertions.assertEquals(expected, solver.majorityElement(nums));
    }
}