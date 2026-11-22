package dev.priyanshu.leetcode.linkedlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.linkedList;
import static dev.priyanshu.leetcode.linkedlist.LinkedList.listToArr;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RotateListTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,4,5}, 2, new int[]{4,5,1,2,3}),
                Arguments.of(new int[]{0,1,2}, 4, new int[]{2,0,1}),
                Arguments.of(new int[]{}, 0, new int[]{}),
                Arguments.of(new int[]{1, 2}, 0, new int[]{1, 2})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int[] expected){
        var list = linkedList(nums);
        var solver = new RotateListImpl();

        var result = solver.rotateRight(list, k);
        Assertions.assertArrayEquals(expected, listToArr(result));
    }
}