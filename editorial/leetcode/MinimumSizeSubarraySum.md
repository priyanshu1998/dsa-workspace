# Minimum Size Subarray Sum

## Problem Statement

Given an array of **positive integers** `nums` and a positive integer
`target`, return the **minimal length** of a contiguous subarray whose sum
is `>= target`. If no such subarray exists, return `0`.

**Example:**
```
nums = [2,3,1,2,4,3], target = 7
Answer = 2            // [4,3] has sum 7 and is the shortest such subarray

nums = [1,4,4], target = 4
Answer = 1             // [4] alone already meets the target

nums = [1,1,1,1,1,1,1,1], target = 11
Answer = 0             // total sum is 8 < 11, no subarray can reach it
```

All values in `nums` are positive, so a subarray's sum strictly increases as
it grows and strictly decreases as it shrinks — this monotonicity is exactly
what makes a **sliding window** feasible: as the right boundary advances,
the left boundary only ever needs to move forward, never backward.

The constraints push toward an `O(n)` time, `O(1)` extra-space solution.

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For every starting index `i`, extend the subarray one element at a time,
summing as you go, and record the first (shortest) extension whose running
sum reaches `target`.

```java
class NaiveSolution implements MinimumSizeSubarraySum {
    @Override
    public int minSubArrayLen(int target, int[] nums) {
        int n = nums.length;
        int best = n + 1;
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum >= target) {
                    best = Math.min(best, j - i + 1);
                    break; // no need to extend further from this i
                }
            }
        }
        return best == n + 1 ? 0 : best;
    }
}
```

**Complexity:** Time `O(n²)` (each start index can extend up to `n` times),
Space `O(1)`.

**Drawback:** Every start index recomputes its running sum from scratch,
even though the *end* of the shortest qualifying window for `i` and `i+1`
rarely moves far apart. Since sums are monotonically increasing as the
window grows (all elements are positive), there's no need to restart the
sum from `0` at every `i` — a single pair of pointers can slide across the
array once, reusing the previous sum instead of recomputing it.

---

### 2. Optimized Solution (Sliding Window / Two Pointers) — O(n) Time, O(1) Space

**Key Insight:** Because every `nums[i] > 0`, extending a window to the
right can only grow its sum, and shrinking a window from the left can only
shrink it. This monotonicity means: for a fixed left boundary `l`, once the
window `[l, r)` reaches `sum >= target`, shrinking it further (`l` moving
right) can only ever help minimize length, never need to look further right
again until the window becomes invalid. Maintaining a running `sum` for the
current window `[l, r)` and only ever moving `r` forward (expand) or `l`
forward (shrink) therefore visits each index O(1) amortized times.

```java
class MinimumSizeSubarraySumImpl implements MinimumSizeSubarraySum {

    @Override
    public int minSubArrayLen(int target, int[] nums) {
        int l = 0;
        int r = 0;

        long sum = 0;
        int len = nums.length + 1;

        while (l < nums.length) {
            // add the leading element to sum
            if (r < nums.length && nums[r] + sum < target) {
                sum += nums[r];
                r++;
            } else {
                // check if the window [l, r] (inclusive of nums[r], if it exists) meets the target
                var v = (r == nums.length) ? 0 : nums[r];
                if (sum + v >= target) {
                    len = Math.min(len, r - l + 1);
                }

                // remove tailing element from sum
                sum -= nums[l];
                l++;
            }
        }

        return len != nums.length + 1 ? len : 0;
    }
}
```

**How it works:**
1. **Expand** while the window `[l, r)` hasn't yet reached `target`: fold
   `nums[r]` into `sum` and advance `r`.
2. **Check & Shrink** otherwise: either `r` has run off the end, or adding
   `nums[r]` would reach/exceed `target`. Before dropping `nums[l]`, check
   whether the window `[l, r]` (i.e. `[l, r)` plus the one extra element
   `nums[r]`, if it exists) already meets `target`; if so, record its length
   as a candidate answer. Then unconditionally remove `nums[l]` from `sum`
   and advance `l`, since `l` can never again form a *shorter* valid window
   with the current `r` (the window is already minimal for this `l`, or
   never reaches `target` at all).

**Complexity:** Time `O(n)` — both `l` and `r` only ever increase, each
taking at most `n` steps total, Space `O(1)` extra.

---

### Correctness Proof

**Definitions.** At any point during the loop, let `l` and `r` denote the
current pointer values with `l <= r`, and let `sum` denote the running
variable. Define the **window sum** `S(l, r) = Σ nums[i]` for `i` in
`[l, r)` (empty sum `= 0` when `l == r`).

**Loop Invariant.** At the top of every iteration of the `while` loop:
```
sum == S(l, r)
```
i.e. `sum` always holds the exact sum of the current half-open window
`[l, r)`, and `len` equals the minimal length, among all windows that start
at an index `< l` and were fully examined so far, whose sum is `>= target`
(or `nums.length + 1` — a sentinel meaning "none found yet" — if no such
window has been seen).

* **Initialization:** Before the first iteration, `l = r = 0`, so the window
  is empty: `S(0, 0) = 0` (empty sum), matching `sum = 0`. Also
  `len = nums.length + 1`, correctly representing "no valid window found
  yet."

* **Maintenance:** Assume `sum == S(l, r)` holds before an iteration.
  Two branches:
  - **(Expand)** triggered when `r < n` and `nums[r] + sum < target`, i.e.
    `S(l, r) + nums[r] < target`. The window extends to `[l, r+1)`:
    `sum += nums[r]` gives `sum == S(l, r) + nums[r] == S(l, r+1)`, so the
    invariant holds for the new `(l, r+1)`.
  - **(Shrink)** triggered otherwise (either `r == n`, or
    `S(l, r) + nums[r] >= target`, meaning window `[l, r]` — inclusive of
    `nums[r]` — would already meet the target). First, the code computes
    `v = (r == n) ? 0 : nums[r]` and checks `sum + v >= target`: this is
    exactly `S(l, r) + v`, i.e. the sum of the window `[l, r]` if `r < n`
    (one element past the current half-open window), or simply `S(l, r)`
    itself if `r == n` (no element to add). If this holds, `r - l + 1` is a
    genuinely valid window length — `[l, r]` when `r < n` has sum `>=
    target` by construction, and this is the *shortest* valid window ending
    at this particular `r` *and* starting at `l`, because the Expand branch
    only stops extending once adding `nums[r]` would reach `target` (so
    every window `[l, r')` for `r' < r` strictly failed to reach `target`,
    and `[l,r]` is the first one that does). So `len` is correctly updated
    to the minimum of its previous value and `r - l + 1`. Then the window's
    left edge advances: `sum -= nums[l]` gives
    `sum == S(l, r) - nums[l] == S(l+1, r)` (exact, since `nums[l]` is
    literally one of the terms summed in `S(l,r)`), matching the shrunk
    window `[l+1, r)`.
  In both branches the invariant is preserved.

* **Termination:** The loop runs while `l < n`. Every iteration either
  increments `r` (Expand) or increments `l` (Shrink), and `r` is bounded
  above by `n`. Once `l == n`, the loop ends. At that point, for every
  start index `i` in `[0, n)`, the Shrink branch's check fired exactly once
  with `l == i` at the moment the window `[i, r)` first could no longer be
  extended without checking — meaning either `[i, r]` reached `target` (and
  its length was recorded), or `r` had already reached `n` without the sum
  reaching `target` (meaning no subarray starting at `i` can reach `target`,
  since `S(i, n)` is the maximum possible sum starting at `i`). Hence `len`
  equals the true minimum length over all qualifying subarrays in the whole
  array, or remains the sentinel `n + 1` if none qualify — in which case the
  function correctly returns `0`.

**Termination & Decreasing Variant.** The pair `(n - l, n - r)` lexicographically
decreases each iteration: `r` strictly increases (Expand) while `l` stays
fixed, or `l` strictly increases (Shrink) while `r` stays fixed. Since both
`l` and `r` are bounded above by `n` and never decrease, the quantity
`(n - l) + (n - r) >= 0` strictly decreases at every step, guaranteeing
termination within `O(n)` total iterations.

---

## Optimization Journey

```
Naive (restart sum per start index) ───────────────────────────────────►
       For each start index i, extend right, recomputing the sum from
       scratch each time.
       Time: O(n²)    Space: O(1)

           ▼  Observation: sums only grow as the window extends and only
              shrink as the window's left edge advances (all elements are
              positive) — so once a window from l reaches target, there's
              no benefit in ever re-extending from a smaller l again. A
              single pass with two pointers can reuse the running sum
              across start indices instead of recomputing it from scratch.

Sliding Window (two pointers) ──────────────────────────────────────────►
       Expand r while the window hasn't reached target; before shrinking
       from l, check whether the (about-to-close) window already reaches
       target and record its length.
       Time: O(n)    Space: O(1)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (restart sum per start index) | O(n²) | O(1) | Recomputes the sum from scratch per start index |
| Sliding Window (two pointers) | O(n) | O(1) | Reuses the running sum; checks the candidate window length right before each shrink |

> **Best solution:** `MinimumSizeSubarraySumImpl` (Sliding Window, two
> pointers) — linear time and constant extra space, exploiting the fact
> that all elements are positive so the window sum is monotone in both
> directions. The key subtlety is checking `sum + nums[r]` (the window
> *about to close*) for the target **before** shrinking `l`, rather than
> only checking the already-expanded window — this correctly captures the
> shortest valid window ending at the current `r`.
