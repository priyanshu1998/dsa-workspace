package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwoSumTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2,7,11,15}, 9, new int[]{0, 1}),
                Arguments.of(new int[]{3,2,4}, 6, new int[]{1, 2}),
                Arguments.of(new int[]{3, 3}, 6, new int[]{0, 1})
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int target, int[] expected){
        var solver = new TwoSumImpl();
        Assertions.assertArrayEquals(expected, solver.twoSum(nums, target));
    }
}