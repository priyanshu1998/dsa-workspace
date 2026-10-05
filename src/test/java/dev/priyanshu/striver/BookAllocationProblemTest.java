package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookAllocationProblemTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{12, 34, 67, 90}, 2, 113),
                Arguments.of(new int[]{25, 46, 28, 49, 24}, 4, 71)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int m, int expected){
        var solver = new BookAllocationProblemImpl();
        Assertions.assertEquals(expected, solver.findPages(nums, m));
    }
}