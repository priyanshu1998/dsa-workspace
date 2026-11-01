package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistinctPointsReachableAfterSubstringRemovalTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("LUL", 1, 2),
                Arguments.of("UDLR", 4, 1),
                Arguments.of("UU", 1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, int k, int expected){
        var solver = new DistinctPointsReachableAfterSubstringRemovalImpl();
        Assertions.assertEquals(expected, solver.distinctPoints(s, k));
    }
}