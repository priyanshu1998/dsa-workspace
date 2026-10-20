package dev.priyanshu.leetcode.subarray;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public interface SlidingWindowMaximum {
    int[] maxSlidingWindow(int[] nums, int k);
}

class SlidingWindowMaximumImpl implements SlidingWindowMaximum {

    private void buildInitialWindow(int []nums, int k, Deque<Integer> deque){
        for(int i=0; i<k; i++){
            addNewWindowEnd(nums, deque, i);
        }
    }

    private void removeWindowStart(int[] nums, Deque<Integer> deque, int l){
        while(!deque.isEmpty() && deque.getFirst() <= l) {
            deque.removeFirst();
        }
    }

    private void addNewWindowEnd(int[] nums, Deque<Integer> deque, int r){
        while(!deque.isEmpty() && nums[deque.getLast()] < nums[r]){
            deque.removeLast();
        }
        deque.addLast(r);
    }

    private int windowMax(int[] nums, Deque<Integer> deque, int l, int r){
        var front = deque.getFirst();
        while(deque.getFirst() < l){
            deque.removeFirst();
            front = deque.getFirst();
        }

        return nums[front];
    }

    @Override
    public int[] maxSlidingWindow(int[] nums, int k) {
        var answer = new ArrayList<Integer>();

        Deque<Integer> deque = new ArrayDeque<>();
        buildInitialWindow(nums, k, deque);

        answer.add(windowMax(nums, deque, 0, k));

        int l = 0;
        int r = k;

        while(r < nums.length){
            removeWindowStart(nums, deque, l); l++;
            addNewWindowEnd(nums, deque, r); r++;
            answer.add(windowMax(nums, deque, l, r));
        }

        return answer.stream().mapToInt(Integer::intValue).toArray();
    }
}
