package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface ReverseNodesInKGroup {
    ListNode reverseKGroup(ListNode head, int k);
}

class ReverseNodesInKGroupImpl implements ReverseNodesInKGroup {

    private ListNode reverseListHelper(ListNode current, ListNode successor) {
        if(successor == null){
            return current;
        }

        var newHead = reverseListHelper(current.next, successor.next);
        successor.next = current;
        return newHead;
    }

    public ListNode reverseList(ListNode head, ListNode nextHead) {
        if(head == null) return null;
        var newHead = reverseListHelper(head, head.next);
        head.next = nextHead;

        return newHead;
    }

    private ListNode kthSuccessor(ListNode node, int k){
        var curr = node;
        for(int i=0; i<k; i++){
            if(curr == null) return null;
            curr = curr.next;
        }

        return curr;
    }


    @Override
    public ListNode reverseKGroup(ListNode head, int i) {
        if(head == null || i == 1) return head;
        var tail = kthSuccessor(head, i-1);

        // Not enough nodes for a complete group, no reversing
        if (tail == null) return head;

        var nextHead = tail.next;
        tail.next = null;

        var newNextHead = reverseKGroup(nextHead, i); // last k Group is first reversed
        return reverseList(head, newNextHead); // actual reversal, last k group can contain lesser than k elements
    }
}