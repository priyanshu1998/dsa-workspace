package dev.priyanshu.structy.count_substring_anagrams;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubstringAnagramsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("tacoctacabcatt", "cat", 4),
                Arguments.of("qtqt", "qt", 3),
                Arguments.of("gattactat", "att", 3),
                Arguments.of("gattactat", "tag", 1),
                Arguments.of("rreeadreaerrand", "reade", 4),
                Arguments.of("versorevairlg", "serve", 0)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, String anagram, int expected){
        var solver = new OptimizedSolution();
        assertEquals(expected, solver.countSubstringAnagrams(s, anagram));
    }
}