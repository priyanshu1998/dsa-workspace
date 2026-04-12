package dev.priyanshu.leetcode.search.linear;

import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Container {

  private int k;
  private int st1Size;
  private int st2Size;
  // st1 saves the k smallest values, st2 saves the other values
  private TreeMap<Integer, Integer> st1;
  private TreeMap<Integer, Integer> st2;
  // sm represents the sum of the first k smallest elements
  private long sm;

  public Container(int k) {
    this.k = k;
    this.st1 = new TreeMap<>();
    this.st2 = new TreeMap<>();
    this.sm = 0;
    this.st1Size = 0;
    this.st2Size = 0;
  }

  private void removeOne(TreeMap<Integer, Integer> map, int key) {
    int count = map.get(key);
    if (count == 1) {
      map.remove(key);
    } else {
      map.put(key, count - 1);
    }
  }

  private void addOne(TreeMap<Integer, Integer> map, int key) {
    map.put(key, map.getOrDefault(key, 0) + 1);
  }

  private void adjust() {
    while (st1Size < k && !st2.isEmpty()) {
      int x = st2.firstKey();
      addOne(st1, x);
      st1Size++;
      sm += x;
      removeOne(st2, x);
      st2Size--;
    }
    while (st1Size > k) {
      int x = st1.lastKey();
      addOne(st2, x);
      st2Size++;
      removeOne(st1, x);
      st1Size--;
      sm -= x;
    }
  }

  // insert element x
  public void add(int x) {
    if (!st2.isEmpty() && x >= st2.firstKey()) {
      addOne(st2, x);
      st2Size++;
    } else {
      addOne(st1, x);
      st1Size++;
      sm += x;
    }
    adjust();
  }

  // delete element x
  public void erase(int x) {
    if (st1.containsKey(x)) {
      removeOne(st1, x);
      st1Size--;
      sm -= x;
    } else if (st2.containsKey(x)) {
      removeOne(st2, x);
      st2Size--;
    }
    adjust();
  }

  // sum of the first k smallest elements
  public long sum() {
    return sm;
  }
}

class Solution {

  public long minimumCost(int[] nums, int k, int dist) {
    int n = nums.length;
    Container cnt = new Container(k - 2);
    for (int i = 1; i < k - 1; i++) {
      cnt.add(nums[i]);
    }

    long ans = cnt.sum() + nums[k - 1];
    for (int i = k; i < n; i++) {
      int j = i - dist - 1;
      if (j > 0) {
        cnt.erase(nums[j]);
      }
      cnt.add(nums[i - 1]);
      ans = Math.min(ans, cnt.sum() + nums[i]);
    }

    return ans + nums[0];
  }
}

class DivideAnArrayIntoSubarraysWithMinimumCost2Test {
  public static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(new int[] {10, 1, 2, 2, 2, 1}, 4, 3, 15),
        Arguments.of(new int[] {1, 2, 3, 2, 6, 4, 9, 2, 8}, 5, 4, 12),
        Arguments.of(new int[] {1, 3, 2, 6, 4, 2}, 3, 3, 5));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int[] arr, int k, int dist, int expected) {
    var solver = new DivideAnArrayIntoSubarraysWithMinimumCost2Impl();
    Assertions.assertEquals(expected, solver.minimumCost(arr, k, dist));
  }
}
