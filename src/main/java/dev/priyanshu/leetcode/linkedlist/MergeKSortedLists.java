package dev.priyanshu.leetcode.linkedlist;

import static dev.priyanshu.leetcode.linkedlist.LinkedList.ListNode;


public interface MergeKSortedLists {
    ListNode mergeKLists(ListNode[] lists);
}

class MergeKSortedListsImpl implements MergeKSortedLists {

    private int val(ListNode list){
        if(list == null) return Integer.MAX_VALUE;
        return list.val;
    }

    private void percolateDown(ListNode[] list, int lastIdx, int i){
        int smallest = i;
        int l = 2*i+1;
        int r = 2*i+2;

        // If the left child is smaller than the current smallest
        if (l <= lastIdx && val(list[l]) < val(list[smallest])) {
            smallest = l;
        }

        // If the right child is smaller than the current smallest
        if (r <= lastIdx && val(list[r]) < val(list[smallest])) {
            smallest = r;
        }

        // If the smallest is not the root
        if (smallest != i) {
            var temp = list[i];
            list[i] = list[smallest];
            list[smallest] = temp;

            // Recursively heapify the affected subtree
            percolateDown(list, lastIdx, smallest);
        }
    }

    private void percolateUp(ListNode[] list, int i){
        if(i == 0) return;

        int parent = (i - 1) / 2;

        // Heap property already satisfied
        if (val(list[parent]) <= val(list[i])) {
            return;
        }

        var temp = list[i];
        list[i] = list[parent];
        list[parent] = temp;

        percolateUp(list, parent);
    }



    private ListNode pop(ListNode[] list, int lastIdx){
        var minList = list[0];

        if (lastIdx == 0) {
            list[0] = null;
            return minList;
        }

        list[0] = list[lastIdx];
        list[lastIdx] = null;

        percolateDown(list, lastIdx-1, 0);
        return minList;
    }


    private void push(ListNode[] list, int lastIdx, ListNode newList){
        list[lastIdx] = newList;
        percolateUp(list, lastIdx);
    }


    private void heapify(ListNode[] lists){
        int n = lists.length;

        for(int i=n/2-1; i>=0; i--){
            percolateDown(lists, lists.length-1, i);
        }
    }

    @Override
    public ListNode mergeKLists(ListNode[] lists) {
        // remove null lists
        lists = java.util.Arrays.stream(lists)
                .filter(java.util.Objects::nonNull)
                .toArray(ListNode[]::new);
        int lastIdx = lists.length-1;

        heapify(lists);


        ListNode head = null;
        ListNode prev = null;

        while(lastIdx >= 0){
            var minList = pop(lists, lastIdx);
            // push minList.val to new list

            var temp = new ListNode(minList.val);
            if(prev != null){
                prev.next = temp;
                prev = temp;
            }else{
                head = temp;
                prev = temp;
            }

            if(minList.next == null){
                lastIdx--;
            }else{
                push(lists, lastIdx, minList.next);
            }
        }

        return head;
    }
}