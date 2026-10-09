package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumNumberOfDaysToMakeMBouquetsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,10,3,10,2}, 3, 1, 3),
                Arguments.of(new int[]{1,10,3,10,2}, 3, 2, -1),
                Arguments.of(new int[]{7,7,7,7,12,7,7}, 2, 3, 12)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int []bloomDay, int m, int k, int expected){
        var solver = new MinimumNumberOfDaysToMakeMBouquetsImpl();
        Assertions.assertEquals(expected, solver.minDays(bloomDay, m, k));
    }

}