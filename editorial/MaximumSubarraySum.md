# Maximum Subarray Sum of Length K

## Problem Statement

Given an integer array `nums` and an integer `k`, find the **maximum sum** among all contiguous subarrays of exactly length `k`.

**Example:**
```
nums = [2, 1, 5, 1, 3, 2], k = 3
Subarrays of length 3: [2,1,5]=8, [1,5,1]=7, [5,1,3]=9, [1,3,2]=6
Maximum sum = 9
```

---

## Solutions

### 1. Naive Solution — O(n·k) Time, O(1) Space

The most straightforward approach: iterate over every valid starting index and compute the subarray sum from scratch each time.

```java
class NaiveSolution implements MaximumSubarraySum {
    private long subarraySum(int[] nums, int start, int k) {
        long sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[start + i];
        }
        return sum;
    }

    @Override
    public long maximumSubarraySum(int[] nums, int k) {
        long M = 0;
        for (int i = 0; i <= nums.length - k; i++) {
            M = Math.max(M, subarraySum(nums, i, k));
        }
        return M;
    }
}
```

**How it works:**
- There are `n - k + 1` valid starting positions.
- For each starting index `i`, sum `k` elements: `nums[i], nums[i+1], ..., nums[i+k-1]`.
- Track the running maximum.

**Complexity:**
Time: O(n·k)
Space: O(1)

**Drawback:** For every new window, all `k` elements are summed again — even though most of them overlap with the previous window. This is the redundant work we eliminate in later solutions.

---

### 2. Runtime Optimized Solution (Prefix Sum) — O(n) Time, O(n) Space

**Key Insight:** Pre-compute a prefix sum array so any subarray sum can be answered in O(1).

```
prefixSum[i] = nums[0] + nums[1] + ... + nums[i]
subarray sum [l, r] = prefixSum[r] - prefixSum[l-1]   (l > 0)
                    = prefixSum[r]                      (l == 0)
```

```java
class RuntimeOptimizedSolution implements MaximumSubarraySum {
    private long subarraySum(long[] prefixSum, int start, int k) {
        int end = start + k - 1;
        return prefixSum[end] - (start > 0 ? prefixSum[start - 1] : 0L);
    }

    @Override
    public long maximumSubarraySum(int[] nums, int k) {
        long[] prefixSum = new long[nums.length];
        prefixSum[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            prefixSum[i] = prefixSum[i - 1] + nums[i];
        }

        long M = 0;
        for (int i = 0; i <= nums.length - k; i++) {
            M = Math.max(M, subarraySum(prefixSum, i, k));
        }
        return M;
    }
}
```

**How it works:**
1. Build the prefix sum in one O(n) pass.
2. Answer each of the `n - k + 1` window queries in O(1) using the prefix array.

**Complexity:**
Time: O(n)
Space: O(n) — the prefix sum array

**Improvement over Naive:** Time drops from O(n·k) → O(n), but we pay O(n) extra space for the prefix array.

---

### 3. Optimized Solution (Sliding Window) — O(n) Time, O(1) Space

**Key Insight:** When the window slides one step to the right, only **one element is added** (new right) and **one element is removed** (old left). There is no need to recompute or store anything extra.

```
window  [l ......... r-1]  →  [l+1 ......... r]
new sum = old sum - nums[l] + nums[r]
```

```java
class OptimizedSolution implements MaximumSubarraySum {
    @Override
    public long maximumSubarraySum(int[] nums, int k) {
        // Compute sum of first window
        long sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[i];
        }

        int l = 0, r = k; // next element to add is at index k
        long M = sum;

        while (r < nums.length) {
            sum = sum - nums[l] + nums[r]; // slide the window
            M = Math.max(M, sum);
            l++;
            r++;
        }

        return M;
    }
}
```

**How it works:**
1. Compute the sum of the first window `[0, k-1]` in O(k).
2. Slide: subtract the element leaving on the left (`nums[l]`), add the element entering on the right (`nums[r]`).
3. Update the maximum after each slide.

**Complexity:**
Time: O(n)
Space: O(1) — only two pointers and a running sum

---

## Optimization Journey

```
Naive  ──────────────────────────────────────────────────────────────────────►
       For every window, recompute the entire sum (k additions each time).
       Wasted work: each element is added & removed multiple times.
       Time: O(n·k)   Space: O(1)

           ▼  Observation: subarray sums can be expressed as prefix differences

Prefix Sum ──────────────────────────────────────────────────────────────────►
       Pre-build cumulative sums once. Any window sum = prefixSum[r] - prefixSum[l-1].
       Eliminates redundant inner loop, but allocates an O(n) auxiliary array.
       Time: O(n)    Space: O(n)

           ▼  Observation: we only need the *previous* window sum, not all prefix sums

Sliding Window ──────────────────────────────────────────────────────────────►
       Maintain a single running sum. On each step: subtract outgoing element,
       add incoming element. No extra array needed.
       Time: O(n)    Space: O(1)   ← Best possible
```

### Step-by-step derivation of Sliding Window from Naive

| Window | Naive computes | Sliding Window computes |
|--------|----------------|------------------------|
| `[0, k-1]` | `nums[0]+…+nums[k-1]` | `nums[0]+…+nums[k-1]` (same, one-time init) |
| `[1, k]` | `nums[1]+…+nums[k]` | `prev - nums[0] + nums[k]` |
| `[2, k+1]` | `nums[2]+…+nums[k+1]` | `prev - nums[1] + nums[k+1]` |
| `[i, i+k-1]` | `nums[i]+…+nums[i+k-1]` | `prev - nums[i-1] + nums[i+k-1]` |

Each transition costs exactly **2 operations** instead of **k operations** — this is why the inner loop disappears.

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n·k) | O(1) | Recomputes overlapping sums |
| Prefix Sum | O(n) | O(n) | O(1) query, but O(n) storage |
| Sliding Window | O(n) | O(1) | Optimal in both dimensions |

> **Best solution:** `OptimizedSolution` (Sliding Window) — linear time and constant space.

