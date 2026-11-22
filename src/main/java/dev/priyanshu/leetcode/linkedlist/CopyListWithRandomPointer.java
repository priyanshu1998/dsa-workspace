package dev.priyanshu.leetcode.linkedlist;


import java.util.function.Supplier;

public interface CopyListWithRandomPointer {
    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }

        public Node(int val, Node next){
            this.val = val;
            this.next = next;
            this.random = null;
        }
    }

    Node copyRandomList(Node head);
}

class CopyListWithRandomPointerImpl implements CopyListWithRandomPointer {

    private Node next(Node curr){
        if(curr == null) return null;
        return curr.next;
    }


    private void initNodes(Node head){
        var curr = head;

        while(curr != null){
            var successor = next(curr);

            // add a temp node between curr and successor
            curr.next = new Node(curr.val, successor);

            curr = successor;
        }
    }

    private void enrichRandomPointer(Node head){
        var curr = head;

        while(curr != null){
            var temp = next(curr);
            var successor = next(temp);

            // point random to successor's random temp
            temp.random = next(curr.random);

            curr = successor;
        }
    }

    private Node finalizeNodes(Node head){
        var curr = head;
        var newHead = curr.next;

        while(curr != null){
            var currTemp = next(curr); // temp  // non-null
            var successor = next(currTemp); // successor of curr in the original list

            // point currTemp's next to successorTemp
            currTemp.next = next(successor);
            curr.next = successor;

            curr = successor;
        }

        return newHead;
    }

    @Override
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        initNodes(head);
        enrichRandomPointer(head);
        return finalizeNodes(head);
    }
}
