# Subarray Product Less Than K

## Problem Statement

Given an array of positive integers `nums` and an integer `k`, return the
**number of contiguous subarrays** whose product is strictly less than `k`.

**Example:**
```
nums = [10, 5, 2, 6], k = 100
Answer = 8
Qualifying subarrays: [10], [5], [2], [6], [10,5], [5,2], [2,6], [5,2,6]
(Note [10,5,2] has product 100, which is NOT < 100, so it's excluded.)

nums = [1, 2, 3], k = 0
Answer = 0            // no product can ever be < 0 since all nums are positive
```

All values in `nums` are positive, so every subarray's product only grows
(or stays equal, impossible here since elements are `>= 1`) as it extends —
this monotonic-growth property is exactly what makes a **sliding window**
feasible: as the right boundary advances, the left boundary only ever needs
to move forward, never backward.

The constraints push toward an `O(n)` time, `O(1)` extra-space solution.

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For every starting index `i`, extend the subarray one element at a time,
multiplying as you go, and count every extension whose running product is
still `< k`.

```java
class NaiveSolution implements SubarrayProductLessThanK {
    @Override
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;
        int cnt = 0;
        for (int i = 0; i < nums.length; i++) {
            long prod = 1;
            for (int j = i; j < nums.length; j++) {
                prod *= nums[j];
                if (prod >= k) break;
                cnt++;
            }
        }
        return cnt;
    }
}
```

**Complexity:** Time `O(n²)` (each start index can extend up to `n` times),
Space `O(1)`.

**Drawback:** Every start index recomputes its running product from
scratch, even though the *end* of the valid window for `i` and `i+1` rarely
moves far apart. Since products are monotonically non-decreasing as the
window grows (all elements are positive), there's no need to restart the
product from `1` at every `i` — a single pair of pointers can slide across
the array once, reusing the previous product instead of recomputing it.

---

### 2. Optimized Solution (Sliding Window / Two Pointers) — O(n) Time, O(1) Space

**Key Insight:** Because every `nums[i] >= 1`, extending a window to the
right can only keep its product the same or grow it, and shrinking a window
from the left can only keep it the same or shrink it. This monotonicity
means: for a fixed left boundary `l`, the set of right boundaries `r` for
which `prod(nums[l..r]) < k` is a contiguous prefix of indices — so as `l`
advances, the furthest valid `r` never needs to move backward. Maintaining
a running `prod` for the current window `[l, r)` and only ever moving `r`
forward (expand) or `l` forward (shrink) therefore visits each index O(1)
amortized times.

Every time the window `[l, r)` is confirmed valid (`prod < k`), it
contributes exactly `r - l` new subarrays: all the subarrays ending at
index `r - 1` and starting anywhere from `l` to `r - 1` inclusive.

```java
class SubarrayProductLessThanKImpl implements SubarrayProductLessThanK {
    @Override
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;
        long prod = 1;

        int l = 0;
        int r = 0;
        int cnt = 0;

        while (l < nums.length) {
            if (r < nums.length && nums[r] * prod < k) {
                prod *= nums[r];
                r++;
            } else {
                cnt += r - l;
                if (l == r) {
                    // window is empty; nums[l] was never multiplied into prod
                    r++;
                } else {
                    prod = prod / nums[l];
                }
                l++;
            }
        }
        return cnt;
    }
}
```

**Complexity:** Time `O(n)` — both `l` and `r` only ever increase, each
taking at most `n` steps total, Space `O(1)` extra.

---

### Correctness Proof

**Definitions.** At any point during the loop, let `l` and `r` denote the
current pointer values with `l <= r`, and let `prod` denote the running
variable. Define the **window product** `P(l, r) = ∏ nums[i]` for
`i` in `[l, r)` (empty product `= 1` when `l == r`).

**Loop Invariant.** At the top of every iteration of the `while` loop:
```
prod == P(l, r)
```
i.e. `prod` always holds the exact product of the current half-open window
`[l, r)`, and `cnt` equals the number of subarrays with product `< k` found
entirely among indices `< l` whose end index is `< l`... more precisely,
`cnt` equals the number of valid subarrays whose *left endpoint* is strictly
less than the current `l` (all such subarrays have been fully accounted for
and will never be revisited, since `l` only increases).

* **Initialization:** Before the first iteration, `l = r = 0`, so the window
  is empty: `P(0, 0) = 1` (empty product), matching `prod = 1`. Also
  `cnt = 0`, correctly counting zero subarrays so far.

* **Maintenance:** Assume `prod == P(l, r)` holds before an iteration.
  Two branches:
  - **(Expand)** triggered when `r < n` and `nums[r] * prod < k`, i.e.
    `P(l, r) * nums[r] < k`. The window extends to `[l, r+1)`:
    `prod *= nums[r]` gives `prod == P(l, r) * nums[r] == P(l, r+1)`, so the
    invariant holds for the new `(l, r+1)`.
  - **(Shrink)** triggered otherwise (either `r == n`, or
    `P(l, r) * nums[r] >= k`, meaning window `[l, r]` would not be valid).
    First, `cnt += r - l` adds every subarray `nums[i..r)` for
    `l <= i < r` — each has product `P(i, r) <= P(l, r) < k` (since
    products only shrink as the start index moves right, all values being
    `>= 1`), so each of these `r - l` subarrays is genuinely valid and this
    is exactly the first time any of them is counted (they all start at an
    index `>= l`, the first unprocessed start index). Then the window's
    left edge advances:
    - If `l == r` (empty window, `P(l, r) = 1`): advancing `l` to `l+1`
      must also keep `r` in sync at `r+1` to preserve `r >= l`; the new
      window `[l+1, r+1)` is likewise empty, so `P(l+1, r+1) = 1`. Since
      `prod` was never multiplied by `nums[l]` in this branch (the empty
      window contains no elements yet), `prod` is correctly left
      untouched at `1`, matching `P(l+1, r+1) = 1`. (This is precisely the
      case a naive "always divide" implementation gets wrong — see
      *Bug History* below.)
    - If `l < r` (non-empty window): `P(l+1, r) = P(l, r) / nums[l]`
      exactly (integer division is exact here because `nums[l]` is a true
      factor of the product `P(l,r)` of the window it belongs to), so
      `prod = prod / nums[l]` gives `prod == P(l+1, r)`, matching the
      shrunk window `[l+1, r)`.
  In both branches the invariant is preserved.

* **Termination:** The loop runs while `l < n`. Every iteration either
  increments `r` (Expand) or increments `l` (Shrink), and `r` is bounded
  above by `n`. Once `l == n`, the loop ends. At that point every subarray
  `nums[i..j)` with `0 <= i < n` has had its start index `i` pass through
  the `l == i` state at some point (since `l` increases by exactly 1 at a
  time from `0` to `n`), and the Shrink branch's `cnt += r - l` step
  guarantees that for each such `i`, *all* valid subarrays starting at `i`
  were counted (the window `[i, r)` grows via Expand until it is maximal,
  i.e. `r` is pushed as far right as `P(i, r) < k` allows, before Shrink
  ever fires for that `l = i`). Hence `cnt` equals the exact count of all
  subarrays across the whole array with product `< k`.

**Termination & Decreasing Variant.** The pair `(n - l, n - r)` lexicographically
decreases each iteration: `r` strictly increases (Expand) while `l` stays
fixed, or `l` strictly increases (Shrink) while `r` stays fixed or also
increases. Since both `l` and `r` are bounded above by `n` and never
decrease, the quantity `(n - l) + (n - r) >= 0` strictly decreases at every
step, guaranteeing termination within `O(n)` total iterations.

---

### Bug History: The Empty-Window Division Trap

An earlier version of the Shrink branch unconditionally executed
`prod = prod / nums[l]` before advancing `l`, without the `l == r` guard:

```java
} else {
    cnt += r - l;
    prod = prod / nums[l];   // BUG: fires even when window [l, r) is empty
    l++;
}
```

This is unsound whenever `nums[r] >= k` is encountered **before any element
has been multiplied into the current window**, i.e. when the window is
empty (`l == r`). In that state `prod == 1` (the empty product), but the
code still divides it by `nums[l]` — an element that was never actually
incorporated into `prod` in the first place. Since `nums[l] > 1` typically,
integer division truncates `1 / nums[l]` down to `0`.

Once `prod` becomes `0`, the Expand condition `nums[r] * prod < k` becomes
`0 < k`, which is true for essentially every remaining `nums[r]` — so the
window expands without bound and `prod` stays stuck at `0` forever,
drastically over-counting `cnt`.

**Counterexample:** `nums = [323, 578, ...]` (many values `>= k = 259`
appear at the very start of the array). Tracing the buggy loop:

| step | `l` | `r` | `prod` (before) | action | `prod` (after) |
|---|---|---|---|---|---|
| 1 | 0 | 0 | 1 | `nums[0]=323`, `323*1 >= 259` → Shrink: `cnt += 0`; `prod = 1/323 = 0`; `l=1` | 0 |
| 2 | 1 | 0 | 0 | `nums[0]=323`, `323*0=0 < 259` → Expand: `prod = 0`, `r=1` | 0 |
| 3 | 1 | 1 | 0 | `nums[1]=578`, `578*0=0 < 259` → Expand: `prod = 0`, `r=2` | 0 |
| ... | | | 0 | every remaining element expands the window since `0 < 259` always | 0 |

`prod` never recovers from `0`, so the window swallows the entire rest of
the array as "valid," producing a wildly inflated `cnt` instead of the
correct answer. The fix guards exactly this empty-window case: when
`l == r`, advance both pointers without touching `prod`, since no element
was ever folded into it that needs to be divided back out.

---

## Optimization Journey

```
Naive (restart product per start index) ───────────────────────────────►
       For each start index i, extend right, recomputing the product
       from scratch each time.
       Time: O(n²)    Space: O(1)

           ▼  Observation: products only grow as the window extends and
              only shrink as the window's left edge advances (all
              elements are positive) — so the valid right boundary for
              each left boundary never moves backward. A single pass with
              two pointers can reuse the running product across start
              indices instead of recomputing it from scratch.

Sliding Window (two pointers) ──────────────────────────────────────────►
       Expand r while the window stays valid, shrink from l (dividing the
       product back out) once it isn't; count r - l new subarrays each
       time the window is confirmed valid.
       Time: O(n)    Space: O(1)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (restart product per start index) | O(n²) | O(1) | Recomputes the product from scratch per start index |
| Sliding Window (two pointers) | O(n) | O(1) | Reuses the running product; must guard the empty-window case before dividing — see Bug History |

> **Best solution:** `SubarrayProductLessThanKImpl` (Sliding Window, two
> pointers) — linear time and constant extra space, exploiting the fact
> that all elements are positive so the product is monotone in both
> directions. The subtle correctness hazard is the empty-window shrink
> step: dividing `prod` by `nums[l]` is only valid when `nums[l]` was
> actually multiplied into `prod`, i.e. when `l < r` (see Bug History).
