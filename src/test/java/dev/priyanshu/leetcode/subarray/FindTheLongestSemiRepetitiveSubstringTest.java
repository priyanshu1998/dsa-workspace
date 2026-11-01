package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindTheLongestSemiRepetitiveSubstringTest {
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("52233", 4),
                Arguments.of("5494", 4),
                Arguments.of("1111111", 2)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, int expected){
        var solver = new FindTheLongestSemiRepetitiveSubstringImpl();
        Assertions.assertEquals(expected, solver.longestSemiRepetitiveSubstring(s));
    }
}