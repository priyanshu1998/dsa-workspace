package dev.priyanshu.cses.sliding_window;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlidingWindowMinimumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(8, 5, 3, 7, 1, 11, 3),
                Arguments.of(100, 20, 3, 7, 1, 997, 74),
                Arguments.of(8, 1, 3, 7, 1, 11, 13),
                Arguments.of(3, 2, 1, 1, 1, 4, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int k, int x, int a, int b, int c, int expected){
        var solver = new SlidingWindowMinimumImpl();
        Assertions.assertEquals(expected, solver.windowMinimums(n, k, x, a, b, c));
    }
}