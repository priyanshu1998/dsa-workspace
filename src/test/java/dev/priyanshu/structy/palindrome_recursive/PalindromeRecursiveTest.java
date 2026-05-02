package dev.priyanshu.structy.palindrome_recursive;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PalindromeRecursiveTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of("pop", true),
                Arguments.of("kayak", true),
                Arguments.of("pops", false),
                Arguments.of("boot", false),
                Arguments.of("rotator", true),
                Arguments.of("abcbca", false),
                Arguments.of("", true)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String s, boolean expected){
        var solver = new PalindromeTailRecursiveImpl();
        assertEquals(expected, solver.isPalindrome(s));
    }
}