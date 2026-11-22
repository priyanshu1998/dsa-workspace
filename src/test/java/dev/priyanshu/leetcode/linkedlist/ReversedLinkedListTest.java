package dev.priyanshu.leetcode.linkedlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.linkedList;
import static dev.priyanshu.leetcode.linkedlist.LinkedList.listToArr;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReversedLinkedListTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,4,5}, new int[]{5, 4, 3, 2, 1}),
                Arguments.of(new int[]{1, 2}, new int[]{2, 1}),
                Arguments.of(new int[]{}, new int[]{})
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int[] expected) {
        var list = linkedList(nums);

        var solver = new ReversedLinkedListImpl();
        var reverseList = solver.reverseList(list);

        if (list == null) {
            Assertions.assertNull(reverseList);
        } else {
            Assertions.assertArrayEquals(expected, listToArr(reverseList));
        }
    }
}