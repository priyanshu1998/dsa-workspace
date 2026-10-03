package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchInRotatedArray2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,1,2,2,2}, 1, true),
                Arguments.of(new int[]{3,1,2,2,2}, 2, true),
                Arguments.of(new int[]{1,1,1,0,1}, 0, true),
                Arguments.of(new int[]{0,1,1,2,0,0}, 2, true),
                Arguments.of(new int[]{4,5,6,7,0,1,2}, 0, true),
                Arguments.of(new int[]{1,3}, 3, true),
                Arguments.of(new int[]{1,1,3}, 0, false),
                Arguments.of(new int[]{1,1,3}, 3, true),
                Arguments.of(new int[]{1}, 1, true),
                Arguments.of(new int[]{1,1}, 1, true),
                Arguments.of(new int[]{1,2,1}, 1, true),
                Arguments.of(new int[]{1,3,5}, 1, true),
                Arguments.of(new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1}, 2, true),
                Arguments.of(new int[]{1,0,1,1,1}, 0, true)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int target, boolean expected){
        var solver = new SearchInRotatedArray2Impl();
        Assertions.assertEquals(expected, solver.search(nums, target));
    }
}