package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggressiveCowsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
            Arguments.of(new int[]{0, 3, 4, 7, 10, 9}, 4, 3),
            Arguments.of(new int[]{4, 2, 1, 3, 6}, 2, 5),
            Arguments.of(new int[]{10, 1, 2, 7, 5}, 3, 4)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new AggressiveCowsImpl();
        Assertions.assertEquals(expected, solver.aggressiveCows(nums, k));
    }
}