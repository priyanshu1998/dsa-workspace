package dev.priyanshu.leetcode.subarray;

import java.util.Deque;

public interface CountSubarraysWithCostLessThanOrEqualToK {
    long countSubarrays(int[] nums, long k);
}

class CountSubarraysWithCostLessThanOrEqualToKImpl implements CountSubarraysWithCostLessThanOrEqualToK {

    private void add(int[] nums, int i, Deque<Integer> minDeque, Deque<Integer> maxDeque) {
        while(!minDeque.isEmpty() && nums[minDeque.peekLast()] >= nums[i]) {
            minDeque.pollLast();
        }
        minDeque.addLast(i);

        while(!maxDeque.isEmpty() && nums[maxDeque.peekLast()] <= nums[i]) {
            maxDeque.pollLast();
        }
        maxDeque.addLast(i);
    }

    private void remove(int[] nums, int i, Deque<Integer> minDeque, Deque<Integer> maxDeque) {
        if (!minDeque.isEmpty() && minDeque.peekFirst() == i) {
            minDeque.pollFirst();
        }

        if (!maxDeque.isEmpty() && maxDeque.peekFirst() == i) {
            maxDeque.pollFirst();
        }
    }

    private long cost(int[] nums, Deque<Integer> maxDeque, Deque<Integer> minDeque, int l, int r) {
        assert !maxDeque.isEmpty();
        assert !minDeque.isEmpty();

        return (long) (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()]) * (r - l + 1);
    }

    @Override
    public long countSubarrays(int[] nums, long k) {
        Deque<Integer> minDeque = new java.util.ArrayDeque<>();
        Deque<Integer> maxDeque = new java.util.ArrayDeque<>();

        int l = 0;
        long result = 0;

        for (int r = 0; r < nums.length; r++) {
            add(nums, r, minDeque, maxDeque);

            while (cost(nums, maxDeque, minDeque, l, r) > k) {
                remove(nums, l, minDeque, maxDeque);
                l++;
            }

            result += r - l + 1;
        }
        return result;
    }
}
