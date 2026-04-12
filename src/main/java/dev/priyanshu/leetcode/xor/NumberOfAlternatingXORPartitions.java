package dev.priyanshu.leetcode.xor;

import java.util.HashMap;
import java.util.Map;

public interface NumberOfAlternatingXORPartitions {
  int alternatingXOR(int[] nums, int target1, int target2);
}

class NumberOfAlternatingXORPartitionsOptimizedImpl implements NumberOfAlternatingXORPartitions {

  private static final long MOD = 1000_000_007;

  @Override
  public int alternatingXOR(int[] nums, int target1, int target2) {

    Map<Integer, Long> count0 = new HashMap<>(); // previous segment XOR was target1
    Map<Integer, Long> count1 = new HashMap<>(); // previous segment XOR was target2

    int px = 0;
    count0.put(0, 1L);

    for (int num : nums) {
      px ^= num;

      long ways1 = count0.getOrDefault(px ^ target1, 0L);
      // ways in which we get XOR of last segment = target1, by taking XOR with b i.e. px ^ target1
      long ways0 = count1.getOrDefault(px ^ target2, 0L);
      // ways in which we get XOR of last segment = target2, by taking XOR with b i.e. px ^ target2

      if (ways1 > 0)
        count1.put(
            px, (count1.getOrDefault(px, 0L) + ways1) % MOD); // update freq, existing count + ways1
      if (ways0 > 0)
        count0.put(
            px, (count0.getOrDefault(px, 0L) + ways0) % MOD); // update freq, existing count + ways0
    }

    //    count0.put(0, count0.get(0)-1);

    return Math.toIntExact(
        ((count1.getOrDefault(px ^ target2, 0L) % MOD + count0.getOrDefault(px ^ target1, 0L) % MOD)
                + ((target2 == 0) ? -1 : 0))
            % MOD);
  }
}
