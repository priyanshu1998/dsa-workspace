package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;

public interface AddTwoNumbers {
    ListNode addTwoNumbers(ListNode l1, ListNode l2);
}

class AddTwoNumbersImpl implements AddTwoNumbers {

    private int val(ListNode curr){
        return (curr != null)? curr.val : 0;
    }

    private ListNode next(ListNode curr){
        return (curr != null)? curr.next : null;
    }

    @Override
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        var headPtr = new ListNode();
        var curr = headPtr;
        int carry = 0;

        while(l1 != null || l2 != null || carry != 0){
            int val = (val(l1) + val(l2) + carry) % 10;
            carry = (val(l1) + val(l2) + carry) / 10;

            curr.next = new ListNode(val);
            curr = curr.next;

            l1 = next(l1);
            l2 = next(l2);
        }

        return headPtr.next;
    }
}
