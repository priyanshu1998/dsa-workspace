package dev.priyanshu.striver;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReversePairsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{6, 4, 1, 2, 7}, 3),
                Arguments.of(new int[]{5, 4, 4, 3, 3}, 0));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new ReversePairsImpl();
        Assertions.assertEquals(expected, solver.reversePairs(nums));
    }

}