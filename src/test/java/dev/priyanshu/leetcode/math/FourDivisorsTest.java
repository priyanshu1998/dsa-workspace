package dev.priyanshu.leetcode.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FourDivisorsTest {

    Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(new int[] {21, 4, 7}, 32),
                Arguments.of(new int[] {21, 21}, 64),
                Arguments.of(new int[] {4, 25}, 0),  // p^2
                Arguments.of(new int[] {16}, 0) // p^4

        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void sumFourDivisors(int[] nums, int expected) {
        var solver = new FourDivisorsImpl();
        Assertions.assertEquals(expected, solver.sumFourDivisors(nums));
    }
}