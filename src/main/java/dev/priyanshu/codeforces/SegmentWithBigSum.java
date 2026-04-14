package dev.priyanshu.codeforces;

/**
 * Contract for finding the shortest contiguous subarray whose element sum is
 * greater than or equal to a given target value.
 *
 * <p>Problem context (Codeforces): given an integer array {@code nums} of
 * length {@code n}, find the minimum length of a contiguous segment (subarray)
 * such that the sum of its elements is {@code >= target}.  Return {@code -1}
 * if no such segment exists.
 */
public interface SegmentWithBigSum {

    /**
     * Returns the length of the shortest contiguous subarray whose sum is
     * {@code >= target}, or {@code -1} if no such subarray exists.
     *
     * @param n      the length of the input array (same as {@code nums.length})
     * @param nums   the input array of non-negative long values
     * @param target the minimum required sum for a "good" segment
     * @return the minimum length of a good segment, or {@code -1} if none exists
     */
    long shortestGoodSegment(int n, long[] nums, long target);
}

/**
 * Sliding-window (two-pointer) implementation of {@link SegmentWithBigSum}.
 *
 * <h2>Approach</h2>
 * <p>A variable-width sliding window is maintained with a left pointer {@code l}
 * and a right pointer {@code r}.  For every position of {@code l}:
 * <ol>
 *   <li>Advance {@code r} (expanding the window) until the running sum
 *       {@code tot >= target} <em>or</em> the array is exhausted.</li>
 *   <li>If the window is "good" ({@code tot >= target}), record its length
 *       {@code r - l} as a candidate minimum.</li>
 *   <li>Remove {@code nums[l]} from the running sum and advance {@code l}
 *       (shrinking from the left) to search for a potentially shorter window
 *       starting at the next position.</li>
 * </ol>
 *
 * <h2>Complexity</h2>
 * <ul>
 *   <li><b>Time:</b> O(n) — each element is added and removed from the window
 *       at most once.</li>
 *   <li><b>Space:</b> O(1) — only a constant number of auxiliary variables.</li>
 * </ul>
 *
 * <h2>Known bug in return statement</h2>
 * <p>The sentinel value for "not found" is initialised to
 * {@code nums.length + 1}.  The final guard should therefore be:
 * <pre>{@code
 *   return minLength > nums.length ? -1L : minLength;
 * }</pre>
 * The current implementation has the branches swapped, causing it to return
 * {@code -1} when a valid segment <em>is</em> found and to return the sentinel
 * value when no segment exists.
 */
class SegmentWithBigSumImpl implements SegmentWithBigSum {

    /**
     * {@inheritDoc}
     *
     * <p>Uses a variable-width sliding window.  {@code minLength} is
     * initialised to {@code nums.length + 1} as a sentinel for "not found";
     * any real answer is {@code <= nums.length}.
     *
     * <p><b>⚠ Bug:</b> the ternary in the return statement is inverted.
     * Replace {@code minLength > nums.length ? minLength : -1L} with
     * {@code minLength > nums.length ? -1L : minLength} for correct behaviour.
     *
     * @param n      ignored (use {@code nums.length} instead)
     * @param nums   input array
     * @param target minimum required segment sum
     * @return minimum good-segment length, or {@code -1} if none exists
     *         (currently returns wrong value due to the inverted ternary)
     */
    @Override
    public long shortestGoodSegment(int n, long[] nums, long target) {
        // Sentinel: any real answer is strictly less than nums.length + 1
        long minLength = nums.length + 1;
        int l = 0;
        int r = 0;
        long tot = 0;

        while (l < nums.length) {

            // Expand the window rightward until the sum satisfies the target
            // or the right boundary reaches the end of the array.
            while (tot < target && r < nums.length) {
                tot += nums[r];
                r++;
            }

            // If the current window is "good", update the running minimum.
            if (tot >= target) {
                minLength = Math.min(minLength, r - l);
            }

            // Shrink the window from the left to look for a shorter good segment.
            tot -= nums[l];
            l++;
        }

        return minLength < nums.length ? minLength : -1L;
    }
}
