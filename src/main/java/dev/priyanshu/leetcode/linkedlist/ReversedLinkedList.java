package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface ReversedLinkedList {
    ListNode reverseList(ListNode head);
}

class ReversedLinkedListImpl implements ReversedLinkedList {


    private ListNode reverseList(ListNode current, ListNode successor) {
        if(successor == null){
            return current;
        }

        var newHead = reverseList(current.next, successor.next);
        successor.next = current;
        return newHead;
    }

    @Override
    public ListNode reverseList(ListNode head) {
        if(head == null) return null;
        var newHead = reverseList(head, head.next);
        head.next = null;

        return newHead;
    }
}
