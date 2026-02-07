package dev.priyanshu.leetcode.search.linear;

import dev.priyanshu.annotation.Leetcode;
import java.util.Comparator;
import java.util.stream.IntStream;

@Leetcode(id = 3010, name = "divide-an-array-into-subarrays-with-minimum-cost-i")
public interface DivideAnArrayIntoSubarraysWithMinimumCost1 {
  int minimumCost(int[] nums);
}

class DivideAnArrayIntoSubarraysWithMinimumCost1Impl
    implements DivideAnArrayIntoSubarraysWithMinimumCost1 {

  @Override
  public int minimumCost(int[] nums) {

    // max index
    var p =
        IntStream.range(1, nums.length)
            .boxed()
            .min(Comparator.comparingInt(i -> nums[i]))
            .orElse(-1);

    // second max index
    var q =
        IntStream.range(1, nums.length)
            .filter(i -> i != p)
            .boxed()
            .min(Comparator.comparingInt(i -> nums[i]))
            .orElse(-1);

    return nums[0] + nums[p] + nums[q];
  }
}
