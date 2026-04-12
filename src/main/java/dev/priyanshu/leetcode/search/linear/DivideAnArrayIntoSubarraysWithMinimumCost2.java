package dev.priyanshu.leetcode.search.linear;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

interface DivideAnArrayIntoSubarraysWithMinimumCost2 {
  long minimumCost(int[] nums, int k, int dist);
}

class DivideAnArrayIntoSubarraysWithMinimumCost2Impl
    implements DivideAnArrayIntoSubarraysWithMinimumCost2 {
  record IndexedElement(int v, int i) {}

  PriorityQueue<IndexedElement> pq =
      new PriorityQueue<>(Comparator.comparingInt(IndexedElement::v));

  void init(int[] arr, int l, int r) {
    for (int i = l; i < r + 1; i++) {
      pq.add(new IndexedElement(arr[i], i));
    }
  }

  void push(IndexedElement item) {
    pq.add(item);
  }

  void push(int i, int v) {
    pq.add(new IndexedElement(v, i));
  }

  IndexedElement pop(int l, int r) {
    while (pq.peek().i() < l || pq.peek().i() > r) {
      pq.remove();
    }
    return pq.poll();
  }

  @Override
  public long minimumCost(int[] nums, int k, int dist) {
    int l = 1;
    int r = 1 + dist;
    init(nums, l, r);

    long M = 1000_000_000_000_000L;

    while (r < nums.length) {
      var temp = new ArrayList<>();

      long windowTot = 0;
      for (int i = 0; i < k - 1; i++) {
        var item = pop(l, r);
        windowTot += item.v();
        temp.add(item);
      }
      temp.forEach(item -> push((IndexedElement) item));

      M = Math.min(M, nums[0] + windowTot);

      l++;
      r++;
      if (r != nums.length) push(r, nums[r]);
    }

    return M;
  }
}
