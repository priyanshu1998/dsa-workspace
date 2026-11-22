package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface LinkedListCycle2 {
    ListNode detectCycle(ListNode head);
}

class LinkedListCycle2Impl implements LinkedListCycle2 {
    @Override
    public ListNode detectCycle(ListNode head) {
        var slow = head;
        var fast = head;

        while (fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;

            if(slow == fast){
                break;
            }
        }

        if (fast == null || fast.next == null) return null;

        while (head != slow){
            head = head.next;
            slow = slow.next;
        }
        return head;
    }
}


