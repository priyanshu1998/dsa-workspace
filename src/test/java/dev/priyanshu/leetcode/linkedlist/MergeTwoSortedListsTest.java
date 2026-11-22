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
class MergeTwoSortedListsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,4}, new int[]{1,3,4}, new int[]{1,1,2,3,4,4}),
                Arguments.of(new int[]{}, new int[]{}, new int[]{}),
                Arguments.of(new int[]{}, new int[]{0}, new int[]{0})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] arr1, int[] arr2, int[] expected){
        var list1 = linkedList(arr1);
        var list2 = linkedList(arr2);

        var solver = new MergeTwoSortedListsImpl();

        var result = solver.mergeTwoLists(list1, list2);

        if(result == null){
            Assertions.assertAll(
                    ()->Assertions.assertNull(list1),
                    ()->Assertions.assertNull(list2)
            );
        }else{
            Assertions.assertArrayEquals(expected, listToArr(result));
        }
    }
}