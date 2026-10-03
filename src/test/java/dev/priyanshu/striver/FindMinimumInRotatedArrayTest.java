package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindMinimumInRotatedArrayTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,4,5,1,2}, 1),
                Arguments.of(new int[]{4,5,6,7,0,1,2}, 0),
                Arguments.of(new int[]{11,13,15,17}, 11),
                Arguments.of(new int[]{3,1,2}, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new FindMinimumInRotatedArrayImpl();
        Assertions.assertEquals(expected, solver.findMinimum(nums));
    }
}