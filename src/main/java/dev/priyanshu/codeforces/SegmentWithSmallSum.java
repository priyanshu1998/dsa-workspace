package dev.priyanshu.codeforces;

/**
 * Defines the contract for finding the longest "good" contiguous segment (subarray) whose element
 * sum does <em>not exceed</em> a given target.
 *
 * <p>A segment {@code [l, r]} is considered <em>good</em> if:
 *
 * <pre>
 *     nums[l] + nums[l+1] + ... + nums[r] &lt;= target
 * </pre>
 *
 * <p><b>Constraints assumed by the implementation:</b>
 *
 * <ul>
 *   <li>All elements in {@code nums} are non-negative.
 *   <li>{@code 1 <= n <= nums.length}
 *   <li>{@code target >= 0}
 * </ul>
 */
public interface SegmentWithSmallSum {

  /**
   * Returns the length of the longest contiguous subarray whose sum is at most {@code target}.
   *
   * @param n the number of elements to consider (typically {@code nums.length})
   * @param nums the input array of non-negative long integers
   * @param target the maximum allowable sum for a "good" segment
   * @return the length of the longest good segment, or {@code 0} if no element satisfies the
   *     constraint individually
   */
  long longestGoodSegment(int n, long[] nums, long target);
}

/**
 * Sliding-window (two-pointer) implementation of {@link SegmentWithSmallSum}.
 *
 * <h2>Algorithm</h2>
 *
 * <p>Because all elements are non-negative, the window sum grows monotonically as the right pointer
 * advances and shrinks monotonically as the left pointer advances. This allows a single
 * left-to-right scan:
 *
 * <ol>
 *   <li><b>Expand</b> — move {@code r} one step to the right and add {@code nums[r]} to the running
 *       sum.
 *   <li><b>Shrink</b> — while the sum exceeds {@code target}, remove {@code nums[l]} from the sum
 *       and advance {@code l}.
 *   <li><b>Record</b> — after shrinking, the window {@code [l, r]} is the longest valid window
 *       ending at {@code r}; update {@code maxLength}.
 * </ol>
 *
 * <h2>Complexity</h2>
 *
 * <ul>
 *   <li><b>Time:</b> O(n) — each element is added and removed at most once.
 *   <li><b>Space:</b> O(1) — only a constant number of scalar variables.
 * </ul>
 */
class SegmentWithSmallSumImpl implements SegmentWithSmallSum {

  /**
   * {@inheritDoc}
   *
   * <p><b>Example:</b>
   *
   * <pre>
   *   nums   = [1, 2, 3, 4, 5]
   *   target = 6
   *
   *   Window trace:
   *   r=0 → sum=1, window=[0,0] len=1
   *   r=1 → sum=3, window=[0,1] len=2
   *   r=2 → sum=6, window=[0,2] len=3
   *   r=3 → sum=10 &gt; 6 → shrink: l=1 sum=9, l=2 sum=7, l=3 sum=4
   *          window=[3,3] len=1
   *   r=4 → sum=9  &gt; 6 → shrink: l=4 sum=5, window=[4,4] len=1
   *
   *   maxLength = 3  (segment [1, 2, 3])
   * </pre>
   *
   * @param n the logical array length (unused internally; {@code nums.length} is used)
   * @param nums non-negative input values
   * @param target the inclusive upper bound on the subarray sum
   * @return length of the longest subarray with sum &lt;= {@code target}
   */
  @Override
  public long longestGoodSegment(int n, long[] nums, long target) {
    long sum = 0;
    int maxLength = 0;

    int l = 0;
    int r = 0;

    while (r < nums.length) {
      // Expand: include nums[r] in the current window
      sum += nums[r];

      // Shrink: restore the "sum <= target" invariant
      while (sum > target) {
        sum -= nums[l];
        l++;
      }

      // The window [l, r] is now the longest valid window ending at r
      maxLength = Math.max(maxLength, r - l + 1);
      r++;
    }

    return maxLength;
  }
}
