package dev.priyanshu.leetcode.linkedlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.linkedList;
import static dev.priyanshu.leetcode.linkedlist.LinkedList.listToArr;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddTwoNumbersTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2,4,3}, new int[]{5,6,4}, new int[]{7,0,8}),
                Arguments.of(new int[]{0}, new int[]{0}, new int[]{0}),
                Arguments.of(new int[]{9,9,9,9,9,9,9}, new int[]{9,9,9,9}, new int[]{8,9,9,9,0,0,0,1})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums1, int[] nums2, int[] expected){
        var list1 = linkedList(nums1);
        var list2 = linkedList(nums2);

        var solver = new AddTwoNumbersImpl();

        var result = solver.addTwoNumbers(list1, list2);
        Assertions.assertArrayEquals(expected, listToArr(result));
    }
}