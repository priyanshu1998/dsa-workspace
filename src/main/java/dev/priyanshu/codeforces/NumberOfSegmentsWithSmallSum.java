package dev.priyanshu.codeforces;

/**
 * Counts the number of contiguous segments (subarrays) whose element sum is <em>at most</em> {@code
 * target}.
 *
 * <p>A segment {@code [l, r]} is called <strong>good</strong> if:
 *
 * <pre>
 *     nums[l] + nums[l+1] + ... + nums[r] &lt;= target
 * </pre>
 *
 * <p><b>Constraints assumed by the implementation:</b>
 *
 * <ul>
 *   <li>All elements in {@code nums} are <em>non-negative</em>. This is the key precondition that
 *       makes the sliding-window approach valid.
 *   <li>{@code n == nums.length}
 * </ul>
 */
public interface NumberOfSegmentsWithSmallSum {

  /**
   * Returns the total count of contiguous subarrays whose sum is ≤ {@code target}.
   *
   * @param n the length of the array (must equal {@code nums.length})
   * @param nums array of <em>non-negative</em> long integers
   * @param target the maximum allowed sum for a segment to be considered good
   * @return the number of good segments (subarrays with sum ≤ {@code target})
   */
  long countOfGoodSegments(int n, long[] nums, long target);
}

/**
 * Sliding-window implementation of {@link NumberOfSegmentsWithSmallSum}.
 *
 * <h2>Algorithm</h2>
 *
 * <p>Because all elements are non-negative, the window sum is <em>monotonically non-decreasing</em>
 * as we expand to the right. This lets us maintain a two-pointer window {@code [l, r]} such that
 * the invariant {@code sum(nums[l..r]) <= target} always holds after the inner shrink loop.
 *
 * <p>For every right boundary {@code r}:
 *
 * <ol>
 *   <li>Expand: add {@code nums[r]} to the running total.
 *   <li>Shrink: while the total exceeds {@code target}, remove {@code nums[l]} and advance {@code
 *       l}.
 *   <li>Count: all {@code r - l + 1} subarrays ending at {@code r} (with left endpoints {@code l,
 *       l+1, ..., r}) are good → add {@code r - l + 1} to the counter.
 * </ol>
 *
 * <h2>Complexity</h2>
 *
 * <ul>
 *   <li><b>Time :</b> O(n) — each element is added and removed at most once.
 *   <li><b>Space :</b> O(1) — only scalar variables are used.
 * </ul>
 */
class NumberOfSegmentsWithSmallSumImpl implements NumberOfSegmentsWithSmallSum {

  /**
   * {@inheritDoc}
   *
   * <p>Uses a variable-width sliding window. After the inner shrink loop the window {@code [l, r]}
   * is the <em>widest</em> valid window ending at {@code r}, so {@code r - l + 1} is exactly the
   * count of good subarrays whose right endpoint is {@code r}.
   */
  @Override
  public long countOfGoodSegments(int n, long[] nums, long target) {
    long count = 0; // accumulates the total number of good segments
    long tot = 0; // running sum of the current window [l, r]

    int l = 0; // left boundary of the sliding window (inclusive)
    int r = 0; // right boundary of the sliding window (inclusive)

    while (r < nums.length) {
      // ── EXPAND ──────────────────────────────────────────────────────
      // Include nums[r] in the current window.
      tot += nums[r];

      // ── SHRINK ──────────────────────────────────────────────────────
      // If the window sum exceeds the target, remove elements from the
      // left until the invariant (tot <= target) is restored.
      // This is safe because all elements are non-negative: removing the
      // leftmost element can only decrease (or keep equal) the sum.
      while (tot > target) {
        tot -= nums[l];
        l++;
      }

      // ── COUNT ───────────────────────────────────────────────────────
      // [l, r] is the widest valid window ending at r.
      // Every subarray [l, r], [l+1, r], ..., [r, r] is good,
      // contributing (r - l + 1) valid segments for this right boundary.
      count += r - l + 1;

      r++;
    }

    return count;
  }
}
