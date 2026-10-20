package dev.priyanshu.cses.sliding_window;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlidingWindowXorTest {
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(8, 5, 3, 7, 1, 11, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int k, int x, int a, int b, int c, int expected){
        var solver = new SlidingWindowXorImpl();
        Assertions.assertEquals(expected, solver.windowXors(n, k, x, a, b, c));
    }
}