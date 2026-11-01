# Max Consecutive Ones III

## Problem Statement

Given a binary array `nums` and an integer `k`, return the maximum number of
consecutive `1`s in the array if you can flip **at most `k` zeroes** to `1`.

**Example:**
```
nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
Flip the two 0's at indices 5 and 10 (or 3 and 4) →
longest run of 1's = 6

nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3
Answer = 10
```

**Constraints:** `1 ≤ nums.length ≤ 10^5`, `nums[i] ∈ {0, 1}`, `0 ≤ k ≤ nums.length`.

---

## Core Insight

"Longest run after flipping at most `k` zeroes" is the same as "longest
subarray containing at most `k` zeroes" — a flip just means the zero is
allowed to live inside the window without being flipped for real. This turns
the problem into a classic **variable-size sliding window**: keep expanding
the window on the right, and whenever the number of zeroes inside it exceeds
`k`, shrink from the left until it's legal again. The window never needs to
shrink below its previous best size, so both pointers move strictly forward
across the whole scan.

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For every starting index, extend the window to the right, counting zeroes
flipped, and stop as soon as more than `k` zeroes would be needed.

```java
class NaiveSolution implements MaxConsecutiveOnes3 {
    @Override
    public int longestOnes(int[] nums, int k) {
        int n = nums.length;
        int best = 0;
        for (int l = 0; l < n; l++) {
            int zeros = 0;
            for (int r = l; r < n; r++) {
                if (nums[r] == 0) zeros++;
                if (zeros > k) break; // can't extend this window any further
                best = Math.max(best, r - l + 1);
            }
        }
        return best;
    }
}
```

**Complexity:** Time `O(n²)` worst case, Space `O(1)`.

**Drawback:** Each left bound re-scans from scratch even though sliding `l`
by one only removes a single element from an otherwise already-known window.

---

### 2. Optimized Solution (Sliding Window / Two Pointers) — O(n) Time, O(1) Space

**Key Insight:** Maintain a window `[l, r)` and a budget `k` of zero-flips
still available.
- If `nums[r]` is `1`, it's free — just extend the window.
- If `nums[r]` is `0` and there's budget left (`k > 0`), "spend" a flip and
  extend.
- Otherwise the window is maximal as-is: before trying to extend further,
  shrink from the left. If the element leaving was a flipped zero, refund
  the budget (`k++`).

Because every zero inside the window corresponds to exactly one unit of
spent budget, `k` always equals "flips remaining," and the window size at
any point is a valid candidate answer.

```java
class MaxConsecutiveOnes3Impl implements MaxConsecutiveOnes3 {

    @Override
    public int longestOnes(int[] nums, int k) {
        int l = 0;
        int r = 0;
        int result = 0;

        while (r < nums.length) {
            if (nums[r] == 1) {
                r++;
                result = Math.max(result, r - l);
            } else if (k > 0) {
                r++;
                k--;
                result = Math.max(result, r - l);
            } else {
                if (nums[l] == 0) {
                    k++;
                }
                l++;
            }
        }

        return result;
    }
}
```

**How it works:**
1. **Extend on `1`:** a real one costs nothing, so always include it and
   grow the window.
2. **Extend on `0` with budget:** spend one flip, include the zero, grow the
   window.
3. **Shrink when out of budget:** `nums[r]` is a zero and `k == 0` — the
   window can't grow without exceeding the flip budget. Drop `nums[l]`; if it
   was a (flipped) zero, refund the budget, since that flip is no longer
   needed. `r` is *not* advanced this iteration, so the next loop retries the
   same `r` against the shrunk window.
4. `result` is updated to `r - l` every time the window successfully grows,
   so it always reflects the best window seen so far.

**Complexity:** Time `O(n)` — `l` and `r` each advance at most `n` times
total, Space `O(1)`.

---

## Optimization Journey

```
Naive ────────────────────────────────────────────────────────────────────►
      For every left bound, re-walk right from scratch, recounting zeroes.
      Wasted work: overlapping windows are recomputed from zero each time.
      Time: O(n²)   Space: O(1)

          ▼ Observation: moving `l` forward only removes one element's
            contribution to the zero count — no need to recompute the rest.

Sliding Window ─────────────────────────────────────────────────────────────►
      Track a live zero-flip budget `k`. Grow `r` while budget allows;
      shrink `l` (refunding the budget) only when it's exhausted.
      Both pointers move strictly forward across the whole array.
      Time: O(n)    Space: O(1)   ← Best possible
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n²) | O(1) | Re-scans each window from scratch |
| Sliding Window | O(n) | O(1) | Optimal in both dimensions |

> **Best solution:** `MaxConsecutiveOnes3Impl` (Sliding Window) — linear
> time and constant space, with `k` doubling as both the flip budget and the
> running zero count inside the window.
