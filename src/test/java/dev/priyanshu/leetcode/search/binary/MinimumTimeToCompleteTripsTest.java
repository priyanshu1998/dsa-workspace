package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumTimeToCompleteTripsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3}, 5, 3),
                Arguments.of(new int[]{2}, 1, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] time, int totalTrips, int expected){
        var solver = new MinimumTimeToCompleteTripsImpl();
        Assertions.assertEquals(expected, solver.minimumTime(time, totalTrips));
    }
}