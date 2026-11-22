package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface RotateList {
    ListNode rotateRight(ListNode head, int k);
}

class RotateListImpl implements RotateList {

    private static int length(ListNode head) {
        var curr = head;
        int cnt = 0;
        while(curr != null) {
            cnt++;
            curr = curr.next;
        }
        return cnt;
    }

    private static ListNode tail(ListNode head){
        var curr = head;

        while(curr.next != null){
            curr = curr.next;
        }

        return curr;
    }

    private ListNode newHead(ListNode head, int k){
        var tail = tail(head);
        tail.next = head;

        var curr = head;
        var predecessor = tail;

        for(int i=0; i<k; i++){
            curr = curr.next;
            predecessor = predecessor.next;
        }

        predecessor.next = null;
        return curr;
    }

    @Override
    public ListNode rotateRight(ListNode head, int k) {
        if(head == null) return null;

        int n = length(head);
        k = k % n;

        return newHead(head, n-k);
    }
}
