package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LongestConsecutiveSequenceTest {

    Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(new int[]{100, 4, 200, 1, 3, 2}, 4),
                Arguments.of(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}, 9),
                Arguments.of(new int[]{1, 9, 3, 10, 4, 20, 2}, 4));
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new LongestConsecutiveSequenceImpl();
        Assertions.assertEquals(expected, solver.longestConsecutive(nums));
    }
}