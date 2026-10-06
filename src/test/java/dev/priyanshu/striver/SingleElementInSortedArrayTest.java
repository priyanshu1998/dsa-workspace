package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SingleElementInSortedArrayTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6}, 4),
                Arguments.of(new int[]{1, 1, 3, 5, 5}, 3),
                Arguments.of(new int[]{1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7}, 7)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int expected){
        var solver = new SingleElementInSortedArrayImpl();
        Assertions.assertEquals(expected, solver.singleNonDuplicate(nums));
    }
}