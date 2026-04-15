package dev.priyanshu.codeforces;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SegmentsWithSmallSetTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(7, new int[]{2, 6, 4, 3, 6, 8, 3}, 3, 20)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int[] nums, int k, long expected){
        var solver = new SegmentsWithSmallSetImpl();
        assertEquals(expected, solver.countOfGoodSegments(n, nums, k));
    }
}