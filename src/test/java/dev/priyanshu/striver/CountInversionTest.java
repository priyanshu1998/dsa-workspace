package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountInversionTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2, 3, 7, 1, 3, 5}, 5),
                Arguments.of(new int[]{-10, -5, 6, 11, 15, 17}, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new CountInversionImpl();
        Assertions.assertEquals(expected, solver.numberOfInversion(nums));
    }
}