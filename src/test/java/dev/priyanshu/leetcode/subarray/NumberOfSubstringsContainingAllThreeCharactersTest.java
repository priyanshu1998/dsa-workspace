package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NumberOfSubstringsContainingAllThreeCharactersTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("abcabc", 10),
                Arguments.of("aaacb", 3),
                Arguments.of("abc", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, int expected){
        var solver = new NumberOfSubstringsContainingAllThreeCharactersImpl();
        Assertions.assertEquals(expected, solver.numberOfSubstrings(s));
    }
}