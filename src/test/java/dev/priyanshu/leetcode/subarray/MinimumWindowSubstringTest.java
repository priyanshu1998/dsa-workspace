package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumWindowSubstringTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("ADOBECODEBANC", "ABC", "BANC"),
                Arguments.of("a", "a", "a"),
                Arguments.of("a", "aa", "")
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testCases(String s, String t, String expected){
        var solver = new MinimumWindowSubstringImpl();

        Assertions.assertEquals(expected, solver.minWindow(s, t));
    }
}