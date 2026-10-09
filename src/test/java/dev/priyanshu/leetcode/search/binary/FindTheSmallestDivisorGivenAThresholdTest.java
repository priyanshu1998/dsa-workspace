package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindTheSmallestDivisorGivenAThresholdTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,5,9}, 6, 5),
                Arguments.of(new int[]{44,22,33,11,1}, 5, 44),
                Arguments.of(new int[]{21212,10101,12121}, 1000000, 1)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int []nums, int threshold, int expected){
        var solver = new FindTheSmallestDivisorGivenAThresholdImpl();
        Assertions.assertEquals(expected, solver.smallestDivisor(nums, threshold));
    }
}