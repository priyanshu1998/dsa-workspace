package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SplitArrayLargestSumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{7,2,5,10,8}, 2, 18),
                Arguments.of(new int[]{1, 2,3,4,5}, 2, 9)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new SplitArrayLargestSumImpl();
        Assertions.assertEquals(expected, solver.splitArray(nums, k));
    }



}