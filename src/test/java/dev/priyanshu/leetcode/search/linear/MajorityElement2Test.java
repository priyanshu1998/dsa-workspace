package dev.priyanshu.leetcode.search.linear;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MajorityElement2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,2,3}, List.of(3)),
                Arguments.of(new int[]{1,2,3}, List.of()),
                Arguments.of(new int[]{2,2}, List.of(2)),
                Arguments.of(new int[]{6,5,5}, List.of(5)),
                Arguments.of(new int[]{0,3,4,0}, List.of(0))
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, List<Integer> expected){
        var solver = new MajorityElement2Impl();
        Assertions.assertEquals(expected, solver.majorityElement(nums));
    }

}