package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestSubstringWithoutRepeatingCharactersTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("abcabcbb",3),
                Arguments.of("bbbbb", 1),
                Arguments.of("pwwkew", 3)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, int expected){
        var solver = new LongestSubstringWithoutRepeatingCharactersImpl();
        Assertions.assertEquals(expected, solver.lengthOfLongestSubstring(s));
    }
}