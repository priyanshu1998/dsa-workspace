package dev.priyanshu.cses.sliding_window;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlidingWindowSumTest {

    public static final String INPUT_TXT = "/dev/priyanshu/cses/sliding_window/SlidingWindowSum/input.txt";

    Stream<Arguments> testCases(){
        return Stream.of(
//                Arguments.of(8, 5, 3, 7, 1, 11, 12),
                Arguments.of(3, 2, 1, 1, 1, 4, 6)

        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int k, int x, int a, int b, int c, int expected){
        var solver = new SlidingWindowSumImpl();
        Assertions.assertEquals(expected, solver.windowSums(n, k, x, a, b, c));
    }

    @Test
    void invokerTest(){
        try(var inputStream = getClass().getResourceAsStream(INPUT_TXT)) {
            SlidingWindowSum.invoker(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
