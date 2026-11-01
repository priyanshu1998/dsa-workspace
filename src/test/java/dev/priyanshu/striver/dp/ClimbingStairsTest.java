package dev.priyanshu.striver.dp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClimbingStairsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(2, 2),
                Arguments.of(3, 3),
                Arguments.of(1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int expected){
        var solver = new ClimbingStairsImpl();

        Assertions.assertEquals(expected, solver.climbStairs(n));
    }
}