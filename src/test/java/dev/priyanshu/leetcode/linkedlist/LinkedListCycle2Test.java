package dev.priyanshu.leetcode.linkedlist;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.getNode;
import static dev.priyanshu.leetcode.linkedlist.LinkedList.linkedListContainingLoop;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinkedListCycle2Test {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,2,0,-4}, 1),
                Arguments.of(new int[]{1,2}, 0),
                Arguments.of(new int[]{1}, -1)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void testLinkedListCycle2(int[] arr, int pos){
        var head = linkedListContainingLoop(arr, pos);
        var solver = new LinkedListCycle2Impl();

        var loopStart = getNode(head, pos);
        assertEquals(loopStart, solver.detectCycle(head));
    }
}