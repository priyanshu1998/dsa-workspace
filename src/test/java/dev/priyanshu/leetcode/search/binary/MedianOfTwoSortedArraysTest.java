package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedianOfTwoSortedArraysTest {

    Stream<Arguments> testCases(){
        return Stream.of(
//                Arguments.of(new int[]{1, 3}, new int[]{2}, 2.0),
                Arguments.of(new int[]{1, 2}, new int[]{3, 4}, 2.5)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums1, int[] nums2, double expected){
        var solver = new MedianOfTwoSortedArraysImpl();
        Assertions.assertEquals(expected, solver.findMedianSortedArrays(nums1, nums2));
    }
}