package dev.priyanshu.leetcode.linkedlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static dev.priyanshu.leetcode.linkedlist.CopyListWithRandomPointer.Node;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CopyListWithRandomPointerTest {

    private record NodeData(int v, int i) {
        private static NodeData of(int v, int i){
            return new NodeData(v, i);
        }
    }

    // Parses a LeetCode-style string like "[[7,null],[13,0],[11,4],[10,2],[1,0]]"
    // into a List<NodeData>, treating "null" as -1.
    private static List<NodeData> parseNodeData(String input) {
        var cleaned = input.trim();
        cleaned = cleaned.substring(1, cleaned.length() - 1); // strip outer [ ]
        var result = new ArrayList<NodeData>();

        if (cleaned.isBlank()) {
            return result;
        }

        var pairs = cleaned.split("(?<=\\]),");
        for (var pair : pairs) {
            var trimmed = pair.trim();
            trimmed = trimmed.substring(1, trimmed.length() - 1); // strip inner [ ]
            var parts = trimmed.split(",");
            int v = Integer.parseInt(parts[0].trim());
            int idx = parts[1].trim().equals("null") ? -1 : Integer.parseInt(parts[1].trim());
            result.add(NodeData.of(v, idx));
        }

        return result;
    }


    Stream<Arguments> testCases(){

        return Stream.of(
                Arguments.of("[[7,null],[13,0],[11,4],[10,2],[1,0]]"),
                Arguments.of("[[1,1],[2,1]]"),
                Arguments.of("[[3,null],[3,0],[3,null]]"));
    }

    private Node constructList(List<NodeData> list){
        var linkedListArray= new ArrayList<Node>();

        var head = new Node(list.getFirst().v);
        linkedListArray.add(head);
        var prev = head;

        // construct nodes
        for(int i=1; i<list.size(); i++){
            var curr = new Node(list.get(i).v());
            linkedListArray.add(curr);
            prev.next = curr;
            prev = curr;
        }

        // link
        for(int j = 0; j<linkedListArray.size(); j++){
            var curr = linkedListArray.get(j);
            var i = list.get(j).i();

            curr.random = i != -1 ? linkedListArray.get(i) : null;
        }

        return head;
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String input){
        var head = constructList(parseNodeData(input));

        var solver = new CopyListWithRandomPointerImpl();
        var newHead = solver.copyRandomList(head);

        while(newHead != null){
            Assertions.assertEquals(head.val, newHead.val);
            if(head.next == null){
                Assertions.assertNull(newHead.next);
            }else{
                Assertions.assertNotEquals(head.next, newHead.next);
            }

            if(head.random == null){
                Assertions.assertNull(newHead.random);
            }else{
                Assertions.assertNotEquals(head.random, newHead.random);
            }

            head = head.next;
            newHead = newHead.next;
        }

    }

}