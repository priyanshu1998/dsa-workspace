package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlternatingGroups2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{0,1,0,1,0}, 3, 3),
                Arguments.of(new int[]{0,1,0,0,1,0,1}, 6,2),
                Arguments.of(new int[]{1,1,0,1}, 4, 0)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] colors, int k, int expected){
        var solver = new AlternatingGroups2Impl();
        Assertions.assertEquals(expected, solver.numberOfAlternatingGroups(colors, k));
    }
}