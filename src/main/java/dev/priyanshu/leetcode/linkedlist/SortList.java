package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface SortList {
    ListNode sortList(ListNode head);
}

class SortListImpl implements SortList {

    private ListNode mergeHelper(ListNode curr1, ListNode curr2, int i, int j, int n1, int n2){
        if(i == n1) return curr2;
        if(j == n2) return curr1;

        if(curr1.val <= curr2.val){
            curr1.next = mergeHelper(curr1.next, curr2, i+1, j, n1, n2);
            return curr1;
        }else{
            curr2.next = mergeHelper(curr1, curr2.next, i, j+1, n1, n2);
            return curr2;
        }
    }

    private ListNode merge(ListNode list1, ListNode list2, int l, int mid, int r){
        return mergeHelper(list1, list2, l, mid, mid, r);
    }

    private ListNode divide(ListNode head, int l, int r){
        if(r-l == 1) {
            head.next = null;
            return head;
        }
        int mid = (r+l)/2;

        var secondHalf = successor(head, l, mid);
        var sortedList1 = divide(head, l, mid);
        var sortedList2 = divide(secondHalf, mid, r);


        return merge(sortedList1, sortedList2, l, mid, r);
    }

    private ListNode successor(ListNode head, int l, int mid){
        for(int i=l; i<mid; i++){
            head = head.next;
        }
        return head;
    }

    private int length(ListNode head){
        int count = 0;
        while(head != null){
            head = head.next;
            count++;
        }
        return count;
    }

    @Override
    public ListNode sortList(ListNode head) {
        if(head == null) return null;
        return divide(head, 0, length(head));
    }
}

class SortListIBottomUpImpl implements SortList {

    private ListNode split(ListNode head, int size){
        for(int i=1; head != null && i < size; i++){
            head = head.next;
        }
        if(head == null) return null;
        var next = head.next;
        head.next = null;
        return next;
    }

    private ListNode merge(ListNode list1, ListNode list2, ListNode tail){
        while(list1 != null && list2 != null){
            if(list1.val <= list2.val){
                tail.next = list1;
                list1 = list1.next;
            }else{
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        tail.next = (list1 != null) ? list1 : list2;
        while(tail.next != null) tail = tail.next;
        return tail;
    }

    private int length(ListNode head){
        int count = 0;
        while(head != null){
            head = head.next;
            count++;
        }
        return count;
    }

    @Override
    public ListNode sortList(ListNode head) {
        if(head == null || head.next == null) return head;

        int n = length(head);
        var dummy = new ListNode();
        dummy.next = head;

        for(int size = 1; size < n; size *= 2){
            var prevTail = dummy;
            var curr = dummy.next;

            while(curr != null){
                var left = curr;
                var right = split(left, size);
                curr = split(right, size);
                prevTail = merge(left, right, prevTail);
            }
        }

        return dummy.next;
    }
}
