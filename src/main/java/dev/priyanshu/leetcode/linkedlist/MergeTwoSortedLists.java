package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;


public interface MergeTwoSortedLists {
    ListNode mergeTwoLists(ListNode list1, ListNode list2);
}

class MergeTwoSortedListsImpl implements MergeTwoSortedLists {

    private ListNode mergeTwoListsHelper(ListNode curr1, ListNode curr2){
        if(curr1 == null) return curr2;
        if(curr2 == null) return curr1;

        if(curr1.val <= curr2.val){
            curr1.next = mergeTwoListsHelper(curr1.next, curr2);
            return curr1;
        }else{
            curr2.next = mergeTwoListsHelper(curr1, curr2.next);
            return curr2;
        }
    }

    @Override
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        return mergeTwoListsHelper(list1, list2);
    }
}
