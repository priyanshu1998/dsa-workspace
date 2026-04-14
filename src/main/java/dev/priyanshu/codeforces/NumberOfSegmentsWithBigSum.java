package dev.priyanshu.codeforces;

/**
 * Interface for counting the number of "good" contiguous segments in an array.
 * A segment is considered "good" if the sum of its elements is at least equal to a target value.
 *
 * This problem is typically solved using a two-pointer sliding window technique,
 * which efficiently counts all good segments in O(n) time complexity.
 */
public interface NumberOfSegmentsWithBigSum {
    /**
     * Counts the number of contiguous segments (subarrays) where the sum of elements
     * is at least equal to the target value.
     *
     * @param n the expected length of the array (used in calculation, note: algorithm uses nums.length)
     * @param nums the array of integers to analyze
     * @param target the minimum sum threshold; a segment is "good" if its sum >= target
     * @return the total count of good segments in the array
     *
     * Time Complexity: O(n) where n is the length of the nums array
     * Space Complexity: O(1) - only uses a constant amount of extra space
     */
    long countOfGoodSegments(int n, long[] nums, long target);
}

/**
 * Implementation of NumberOfSegmentsWithBigSum using a two-pointer sliding window algorithm.
 *
 * Algorithm Overview:
 * - Uses two pointers (left and right) to maintain a window of elements
 * - Expands the window by moving right pointer until sum >= target
 * - When a valid segment is found, counts all segments starting at left and ending at right or beyond
 * - Shrinks the window from the left to check other potential segments
 *
 * Key Insight:
 * If a segment [l, r] has sum >= target, then all segments [l, r'], where r' >= r,
 * also have sum >= target (assuming all array elements are positive).
 * Therefore, when we find a valid segment at position l with right pointer at r,
 * we can immediately count (n - r + 1) valid segments.
 */
class NumberOfSegmentsWithBigSumImpl implements NumberOfSegmentsWithBigSum {

    /**
     * Counts good segments using the two-pointer sliding window technique.
     *
     * Algorithm Steps:
     * 1. Initialize two pointers (l, r) and sum (tot) to track the current window
     * 2. For each left pointer position:
     *    a. Expand the window (move right) until the sum is at least the target or r reaches the end
     *    b. If sum >= target, count all valid segments: n - r + 1
     *    c. Move left pointer forward and remove the leftmost element from the sum
     * 3. Return the total count of good segments
     *
     * Example: nums = [1, 2, 3, 4], target = 5
     * - l=0: r expands to include [1,2,3] (sum=6), count += 4-3+1 = 2 segments: [0,2], [0,3]
     * - l=1: r stays at 3, sum = 2+3+4 = 9 >= 5, count += 4-3+1 = 2 segments: [1,2], [1,3]
     * - l=2: r is at 3, sum = 3+4 = 7 >= 5, count += 4-3+1 = 2 segments: [2,2], [2,3]
     * - l=3: r is at 3, sum = 4 < 5, no more valid segments
     * - Total: 6 segments
     *
     * @param n the expected array length (NOTE: implementation uses nums.length)
     * @param nums the array of integers; elements should typically be positive
     * @param target the minimum sum threshold for good segments
     * @return the total number of contiguous segments with sum >= target
     */
    @Override
    public long countOfGoodSegments(int n, long[] nums, long target) {
        long count = 0;  // Counter for valid segments
        int l = 0;       // Left pointer of the window
        int r = 0;       // Right pointer of the window

        long tot = 0;    // Current sum of elements in the window [l, r)

        // Outer loop: iterate through each potential starting position
        while (l < nums.length){
            // Inner loop: expand the window until sum >= target or we reach the end
            while(tot < target && r < nums.length){
                tot += nums[r];
                r++;
            }

            // If the current window has sum >= target, count all valid segments
            // All segments [l, r'], where r' >= r, have sum >= target
            if (tot >= target){
                count += n - r + 1;
            }

            // Shrink the window from the left: remove the leftmost element and move left pointer
            tot -= nums[l];
            l++;
        }

        return count;
    }
}

