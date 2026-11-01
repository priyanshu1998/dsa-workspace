package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountOfSubstringsContainingEveryVowelAndKConsonants2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("aeioqq", 1, 0),
                Arguments.of("aeiou", 0, 1),
                Arguments.of("ieaouqqieaouqq", 1, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, int k, int expected){
        var solver = new CountOfSubstringsContainingEveryVowelAndKConsonants2Impl();
        Assertions.assertEquals(expected, solver.countOfSubstrings(s, k));
    }
}