# Find Subarray with Target Sum

## Problem Statement

Given an array of **positive integers** `nums` and a `target` value, find the **contiguous subarray** whose elements sum exactly to `target`. Return the **start and end indices** `[l, r]` (inclusive) of that subarray, or `[-1, -1]` if no such subarray exists.

**Example:**
```
nums = [1, 4, 2, 9, 5, 3], target = 11
→ subarray [4, 2, 9] at indices [1, 3] sums to 11 ✓

nums = [1, 2, 3], target = 10
→ no subarray sums to 10 → [-1, -1]
```

> **Important constraint:** All elements in `nums` are **positive**. This is what makes the sliding window approach valid.

---

## Solutions

### 1. Naive Solution — O(n³) Time, O(1) Space

The most straightforward approach: try every possible subarray `[i, j]` and compute its sum from scratch by iterating through all elements between `i` and `j`.

```java
class NaiveSolution implements FindSubarraySum {

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = 1; j <= nums.length; j++) {
                long sum = 0;
                for (int k = i; k < j; k++) {  // inner sum loop — O(n) per window
                    sum += nums[k];
                }
                if (sum == target) {
                    return FindSubarraySum.getIndexes(new Window(i, j - 1));
                } else if (sum > target) {
                    break;              // early exit: further extending this window only grows the sum
                }
            }
        }
        return new int[]{-1, -1};
    }
}
```

**How it works:**
- The outer two loops enumerate every `(start, end)` pair — there are `O(n²)` of them.
- The innermost loop sums the elements in range `[i, j)` each time — O(n) per pair.
- An early `break` exits the inner loop when the sum already exceeds the target (valid only because all values are positive).

**Complexity:**
- Time: **O(n³)** — triple nested loops in the worst case
- Space: **O(1)** — only a handful of variables

**Drawback:** The innermost loop recomputes the entire subarray sum from scratch for every `(i, j)` pair, even though consecutive windows share almost all their elements.

---

### 2. Sum-Optimized Solution (Prefix Sum) — O(n²) Time, O(n) Space

**Key Insight:** Pre-compute a cumulative prefix sum array so that any subarray sum `[l, r]` can be answered in **O(1)** instead of O(n).

```
prefixSum[i]  =  nums[0] + nums[1] + … + nums[i]

sum of [l, r] =  prefixSum[r] - prefixSum[l-1]    (when l > 0)
              =  prefixSum[r]                       (when l == 0)
```

```java
class SumOptimized implements FindSubarraySum {

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        long[] prefixSum = new long[nums.length];
        prefixSum[0] = nums[0];

        for (int i = 1; i < nums.length; i++) {
            prefixSum[i] = prefixSum[i - 1] + nums[i];   // one-time O(n) build
        }

        for (int i = 0; i < nums.length; i++) {
            for (int j = 1; j <= nums.length; j++) {
                long sum = prefixSum[j - 1] - ((i != 0) ? prefixSum[i - 1] : 0);

                if (sum == target) {
                    return FindSubarraySum.getIndexes(new Window(i, j - 1));
                } else if (sum > target) {
                    break;              // still valid: all values positive → sums only grow
                }
            }
        }

        return new int[]{-1, -1};
    }
}
```

**How it works:**
1. **Build phase (O(n)):** Walk the array once to fill `prefixSum`.
2. **Query phase (O(n²)):** The two outer loops still enumerate all `(start, end)` pairs, but each sum is computed in O(1) using the formula above — eliminating the innermost loop entirely.

**Complexity:**
- Time: **O(n²)** — double loop, O(1) per sum query
- Space: **O(n)** — the prefix sum array

**Improvement over Naive:** Removes the O(n) inner sum loop, cutting time from O(n³) to O(n²). The trade-off is O(n) extra space for the prefix array.

**Remaining drawback:** We're still iterating over every starting index `i`. Most of this is unnecessary — can we eliminate the outer loop too?

---

### 3. Window-Optimized Solution (Sliding Window) — O(n) Time, O(1) Space

**Key Insight:** Because all elements are **positive**, the subarray sum grows monotonically as we extend right and shrinks monotonically as we shrink from the left. We can use a **variable-size sliding window** that expands and contracts based on a comparison with the target — no need to test every starting index.

```
           l               r
           ↓               ↓
  … [ a | b | c | d | e ] f …
         └───────────────┘
              window sum

  sum > target  →  shrink from left  (l++)
  sum == target →  found it!
  sum < target  →  extend to right   (r++)
```

```java
class WindowOptimized implements FindSubarraySum {

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        long sum = 0;
        int l = 0;
        int r = 0;

        while (r < nums.length) {
            sum += nums[r];                     // extend window to the right

            while (sum > target) {
                sum -= nums[l++];               // shrink window from the left
            }

            if (sum == target) {
                return FindSubarraySum.getIndexes(new Window(l, r));
            }

            r++;
        }

        return new int[]{-1, -1};
    }
}
```

**How it works:**
1. Start with an empty window at `l = r = 0`.
2. **Expand:** Add `nums[r]` to `sum`, then move `r` forward.
3. **Shrink:** While `sum > target`, subtract `nums[l]` and move `l` forward. Because all values are positive, this always brings the sum back down toward the target.
4. **Check:** If `sum == target`, the window `[l, r]` is the answer.
5. Each element is added at most once (via `r`) and removed at most once (via `l`), so the total work is **O(n)**.

**Complexity:**
- Time: **O(n)** — each pointer traverses the array at most once
- Space: **O(1)** — only two pointers and a running sum

**Why can't this work for arrays with negative numbers?**
Shrinking from the left is only safe when removing an element is guaranteed to reduce the sum. A negative element would do the opposite, breaking the monotonicity assumption that the whole strategy relies on.

---

## Optimization Journey

```
Naive (O(n³)) ──────────────────────────────────────────────────────────────►
    For every (start, end) pair, re-sum all elements between them.
    Wasted work: the same elements are added over and over across overlapping windows.
    Time: O(n³)   Space: O(1)

        ▼  Observation: any subarray sum = difference of two prefix sums (O(1) lookup)

Prefix Sum (O(n²)) ──────────────────────────────────────────────────────────►
    Pre-build cumulative sums once. Eliminate the innermost summation loop.
    Any window sum is now a single subtraction: prefixSum[r] - prefixSum[l-1].
    Time: O(n²)   Space: O(n)

        ▼  Observation: because values are positive, sums are monotone — we
           don't need to restart from every index; just slide a window

Sliding Window (O(n)) ────────────────────────────────────────────────────────►
    Maintain a live running sum. Extend right when below target,
    shrink left when above target. The answer window is found in one pass.
    Time: O(n)    Space: O(1)   ← Optimal
```

### Step-by-step derivation

Consider `nums = [2, 3, 1, 4, 5]`, `target = 8`.

| Step | l | r | sum | Action |
|------|---|---|-----|--------|
| 1 | 0 | 0 | 2 | sum < 8 → expand right |
| 2 | 0 | 1 | 5 | sum < 8 → expand right |
| 3 | 0 | 2 | 6 | sum < 8 → expand right |
| 4 | 0 | 3 | 10 | sum > 8 → shrink left (subtract nums[0]=2) |
| 5 | 1 | 3 | 8 | sum == 8 → **return [1, 3]** ✓ |

The window contracts and expands fluidly, visiting each index at most twice total (once as `r` sweeps in, once as `l` sweeps out) — this is the core reason the algorithm is O(n).

**Contrast with Naive:** At step 4, the naive approach would discard everything and restart the inner sum loop from `i=1`, summing `[3,1,4]` from scratch. The sliding window just subtracts one element — that single change eliminates the entire outer-over-inner restart overhead.

---

## Complexity Summary

| Solution | Time | Space | Strategy |
|----------|------|-------|----------|
| `NaiveSolution` | O(n³) | O(1) | Enumerate all windows, recompute sum each time |
| `SumOptimized` | O(n²) | O(n) | Enumerate all windows, O(1) sum via prefix array |
| `WindowOptimized` | **O(n)** | **O(1)** | Expand/shrink a live window guided by sum vs target |

> **Best solution:** `WindowOptimized` — linear time, constant space, and no auxiliary data structures.  
> **Prerequisite:** Only valid when all elements in `nums` are **positive** (ensures monotone sums).

