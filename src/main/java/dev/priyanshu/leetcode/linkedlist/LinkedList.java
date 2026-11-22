package dev.priyanshu.leetcode.linkedlist;

import java.util.ArrayList;

public class LinkedList {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
            val = 0;
            next = null;
        }

        ListNode(int data1) {
            val = data1;
            next = null;
        }

        ListNode(int data1, ListNode next1) {
            val = data1;
            next = next1;
        }
    }

    private static ListNode lastNode(ListNode head) {
        ListNode curr = head;
        while (curr.next != null) {
            curr = curr.next;
        }
        return curr;
    }

    public static ListNode linkedList(int[] arr) {
        if (arr.length == 0) return null;

        var head = new ListNode(arr[0]);
        var curr = head;
        for (int i = 1; i < arr.length; i++) {
            curr.next = new ListNode(arr[i]);
            curr = curr.next;
        }
        return head;
    }

    public static ListNode getNode(ListNode head, int pos) {
        if (pos == -1) return null;
        var curr = head;
        for (int i = 0; i < pos; i++) {
            curr = curr.next;
        }
        return curr;
    }

    public static ListNode linkedListContainingLoop(int[] arr, int pos) {
        var head = linkedList(arr);
        assert head != null;

        lastNode(head).next = getNode(head, pos);
        return head;
    }

    public static int[] listToArr(ListNode head) {
        var list = new ArrayList<>();

        var curr = head;
        while (curr != null) {
            list.add(curr.val);
            curr = curr.next;
        }

        return list.stream()
                .mapToInt(v -> (int) v)
                .toArray();
    }
}
