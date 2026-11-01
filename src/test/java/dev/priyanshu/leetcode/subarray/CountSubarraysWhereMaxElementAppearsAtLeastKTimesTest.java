package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountSubarraysWhereMaxElementAppearsAtLeastKTimesTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,3,2,3,3}, 2, 6),
                Arguments.of(new int[]{1,4,2,1}, 3, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, long expected){
        var solver = new CountSubarraysWhereMaxElementAppearsAtLeastKTimesImpl();
        Assertions.assertEquals(expected, solver.countSubarrays(nums, k));
    }
}