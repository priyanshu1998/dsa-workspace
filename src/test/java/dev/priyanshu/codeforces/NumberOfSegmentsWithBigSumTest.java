package dev.priyanshu.codeforces;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NumberOfSegmentsWithBigSumTest {
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(7, new long[]{2, 6, 4, 3, 6, 8, 9}, 20, 9)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, long[] nums, long target, long expected){
        var solver = new NumberOfSegmentsWithBigSumImpl();
        assertEquals(expected, solver.countOfGoodSegments(n, nums, target));
    }
}
