# Minimum Subarray Length With Distinct Sum At Least K

## Problem Statement

Given an array of **positive integers** `nums` and a positive integer `k`,
return the **minimal length** of a contiguous subarray whose **distinct
sum** — the sum of its *unique* values, each counted only once no matter how
many times it repeats inside the subarray — is `>= k`. If no such subarray
exists, return `-1`.

**Example:**
```
nums = [2,2,3,1], k = 4
Answer = 2             // [2,3] -> distinct values {2,3} -> sum 5 >= 4

nums = [3,2,3,4], k = 5
Answer = 2             // [3,2] -> distinct values {3,2} -> sum 5 >= 5

nums = [5,5,4], k = 5
Answer = 1             // [5]   -> distinct values {5}   -> sum 5 >= 5

nums = [63,47,9,23,47,29,47,23,36,47,9], k = 121
Answer = 4             // [29,47,23,36] -> distinct values {29,47,23,36} -> sum 135 >= 121
```

The twist versus the classic "minimum subarray sum >= target" problem
(LeetCode 209) is that **repeated values inside the window don't count
twice**. A naive running sum (`windowSum += nums[right]`,
`windowSum -= nums[left]`) is *wrong* here: it would double count repeated
values and under-count when a duplicate leaves the window while a copy of
it still remains.

---

## Why plain sliding-window sum fails

Consider `nums = [63,47,9,23,47,29,47,23,36,47,9]`, `k = 121`. The *plain*
sum sliding window (ignoring duplicates) reports a shortest window of
length `3`: `[9,23,47,29,47]`'s suffix `[47,29,47]` has a **plain** sum
`47+29+47 = 123 >= 121`. But its **distinct** sum is only
`47 + 29 = 76 < 121`, because the two `47`s are the same value counted once
— that window is **not** actually valid under distinct-sum rules. The true
answer, using distinct sums, is `4` (e.g. `[29,47,23,36]`, distinct sum
`29+47+23+36 = 135 >= 121`). Plain-sum sliding window systematically
*overestimates* how much a window is "worth" whenever it contains repeats,
because it double-counts them.

So the window needs to track **how many times each value currently appears**
(a frequency map) and only adjust the tracked `distinctSum` when a value's
count transitions `0 -> 1` (entering the window) or `1 -> 0` (fully leaving
the window).

---

## Solution — Sliding Window with a Frequency Map

**Key Insight.** Distinct sum is **monotonic with respect to set
inclusion**: if window `A` is a subset of window `B` (e.g. `A = [l+1, r]` is
contained in `B = [l, r]`), then every distinct value present in `A` is also
present in `B`, so `distinctSum(A) <= distinctSum(B)`. This is exactly the
monotonicity a two-pointer sliding window needs: expanding the window
(growing `r`) can only ever increase or hold steady the distinct sum, and
shrinking it (growing `l`) can only ever decrease or hold it steady. That
guarantees both pointers only ever move forward.

```java
class MinimumSubarrayLengthWithDistinctSumAtLeastKImpl implements MinimumSubarrayLengthWithDistinctSumAtLeastK {
    @Override
    public int minLength(int[] nums, int k) {
        int n = nums.length;
        int minLen = Integer.MAX_VALUE;
        int left = 0;
        int distinctSum = 0;
        Map<Integer, Integer> freq = new HashMap<>();

        for (int right = 0; right < n; right++) {
            // a value only contributes to distinctSum the first time it enters the window
            if (freq.merge(nums[right], 1, Integer::sum) == 1) {
                distinctSum += nums[right];
            }

            while (distinctSum >= k) {
                minLen = Math.min(minLen, right - left + 1);
                // a value is removed from distinctSum only once its last copy leaves the window
                if (freq.merge(nums[left], -1, Integer::sum) == 0) {
                    distinctSum -= nums[left];
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }
}
```

**How it works:**
1. **Expand:** advance `right`, bump `nums[right]`'s frequency. If that
   frequency just became `1` (the value is new to the window), add it to
   `distinctSum`. If it was already present, `distinctSum` doesn't change —
   the duplicate is "free."
2. **Check & Shrink:** while `distinctSum >= k`, the window `[left, right]`
   is valid — record its length, then try to shrink from the left for a
   potentially shorter valid window. Decrement `nums[left]`'s frequency;
   only when it drops to `0` (its last copy has left the window) subtract
   it from `distinctSum`.
3. Repeat until `right` exhausts the array. Return the smallest recorded
   length, or `-1` if `distinctSum` never reached `k`.

---

## Step-by-step Trace

```
nums = [63,47,9,23,47,29,47,23,36,47,9], k = 121  (left=0 initially)

right=0  val=63  distinctSum=63    (< 121, no shrink)
right=1  val=47  distinctSum=110   (< 121, no shrink)
right=2  val=9   distinctSum=119   (< 121, no shrink)
right=3  val=23  distinctSum=142   (>= 121!)
  VALID [0..3] len=4 -> minLen=4
  remove 63 (freq 1->0, drop from sum): distinctSum=79, left=1    (< 121, stop)
right=4  val=47  (freq 1->2, already in window): distinctSum=79   (< 121, no shrink)
right=5  val=29  distinctSum=108   (< 121, no shrink)
right=6  val=47  (freq 2->3): distinctSum=108                     (< 121, no shrink)
right=7  val=23  (freq 1->2): distinctSum=108                     (< 121, no shrink)
right=8  val=36  distinctSum=144   (>= 121!)
  VALID [1..8] len=8 -> minLen stays 4
  remove 47 (freq 3->2, still present): distinctSum=144, left=2   (still >= 121)
  VALID [2..8] len=7 -> minLen stays 4
  remove 9  (freq 1->0, drop from sum): distinctSum=135, left=3   (still >= 121)
  VALID [3..8] len=6 -> minLen stays 4
  remove 23 (freq 2->1, still present): distinctSum=135, left=4   (still >= 121)
  VALID [4..8] len=5 -> minLen stays 4
  remove 47 (freq 2->1, still present): distinctSum=135, left=5   (still >= 121)
  VALID [5..8] len=4 -> minLen stays 4
  remove 29 (freq 1->0, drop from sum): distinctSum=106, left=6   (< 121, stop)
right=9  val=47  distinctSum=106    (< 121, no shrink)
right=10 val=9   distinctSum=115    (< 121, no shrink, end of array)

Final answer: minLen = 4 ✓
```

---

## Correctness Proof

Let `n = nums.length`. For indices `0 <= l <= r <= n`, define the **distinct
sum** of the half-open window `[l, r)` as
`D(l, r) = Σ_{v ∈ distinct(nums[l..r-1])} v`
(sum over the *set* of values appearing in that slice, each counted once).

### Lemma 1 (Monotonicity under inclusion)

If `l <= l' <= r' <= r`, i.e. `[l', r')` ⊆ `[l, r)`, then
`D(l', r') <= D(l, r)`.

*Proof.* Every value appearing in `nums[l'..r'-1]` also appears in
`nums[l..r-1]` (it's a sub-slice), so the set of distinct values in the
smaller window is a subset of the set of distinct values in the larger
window. Summing over a subset of non-negative terms can't exceed summing
over the superset (all `nums[i] > 0`). ∎

**Corollary.** For fixed `l`, `D(l, r)` is non-decreasing in `r` (expanding
only grows the window). For fixed `r`, `D(l, r)` is non-increasing in `l`
(shrinking only grows `l`, i.e. only removes elements).

### Loop Invariant

At the top of every outer iteration (fixed `right`, before/after the inner
`while`):
```
freq[v] == (number of occurrences of value v in nums[left..right])  for every v
distinctSum == D(left, right+1)   (after the for-loop body processes `right`)
```

- **Initialization:** before any iteration, `left = 0`, `freq` is empty,
  `distinctSum = 0 = D(0, 0)` (empty window, empty distinct set).

- **Maintenance (expand):** processing `right`, `freq.merge(nums[right], 1,
  ...)` increments the true occurrence count of `nums[right]` in
  `[left, right]`, preserving the first invariant. If the returned count is
  `1`, `nums[right]` had zero prior occurrences in `[left, right)`
  (by the invariant before this step) — i.e. it's a genuinely new distinct
  value for the window — so adding it to `distinctSum` correctly yields
  `D(left, right+1) = D(left, right) + nums[right]`. If the returned count
  is `> 1`, `nums[right]` was already present, so
  `distinct(nums[left..right]) = distinct(nums[left..right-1])` and
  `D(left, right+1) = D(left, right)` — correctly left unchanged.

- **Maintenance (shrink):** while `distinctSum >= k`, i.e.
  `D(left, right+1) >= k`, the window `[left, right]` is valid, so recording
  `right - left + 1` as a length candidate is sound. Then
  `freq.merge(nums[left], -1, ...)` decrements the true occurrence count of
  `nums[left]` in `[left, right]`, preserving the first invariant for the
  shrunk window `[left+1, right]`. If the returned count is `0`,
  `nums[left]` had exactly one occurrence in `[left, right]` (its last),
  so it is no longer in `distinct(nums[left+1..right])`, and subtracting it
  gives `D(left+1, right+1) = D(left, right+1) - nums[left]` — correct. If
  the returned count is `> 0`, a duplicate of `nums[left]` still remains in
  `[left+1, right]`, so it's still in the distinct set, and
  `D(left+1, right+1) = D(left, right+1)` — correctly left unchanged.

Both branches preserve the invariant, and `left` advances by exactly one
each time, matching the window update `[left, right] -> [left+1, right]`.

### Theorem (Optimality of `minLen`)

Because the inner `while` loop shrinks *as long as* `distinctSum >= k` — by
Lemma 1's corollary, once a window `[left, right]` is valid, every
`[left', right]` with `left' < left` is *also* valid (it's a superset, so
its distinct sum is at least as large) — the algorithm, for each `right`,
finds the unique *smallest* valid `left` for that `right` (the loop stops
precisely when shrinking further would make `D(left, right+1) < k`, by
monotonicity no smaller window ending at `right` can be valid once that
happens). Hence every minimal-length valid window ending at some `right` is
discovered and compared against `minLen`. Since **every** valid subarray
has *some* rightmost index, and the loop examines every possible
`right` from `0` to `n-1`, no valid subarray's optimal (shrunk) left
boundary is skipped. Therefore `minLen` at the end of the loop equals the
true minimum length among all valid subarrays, or remains
`Integer.MAX_VALUE` (mapped to `-1`) if none exists.

### Termination

`right` strictly increases once per outer-loop iteration, bounded by `n`.
Within a fixed `right`, `left` strictly increases each inner-loop iteration
and is bounded above by `right + 1` (shrinking stops once the window is
empty or `distinctSum < k`). So the quantity `(n - right) + (right - left)`
is bounded and non-increasing, guaranteeing the whole algorithm terminates
in at most `O(n)` total pointer moves.

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| Sliding window (`left`, `right` each advance at most `n` times) | O(n) | O(n) — frequency map holds at most `n` distinct keys |

> **Why `O(n)` space?** Unlike the classic positive-sum sliding window
> (LeetCode 209, `O(1)` space), this variant must track *per-value*
> occurrence counts to correctly detect when a value enters/leaves the
> window's distinct set — a single running `int sum` is not enough because
> duplicates must not be double-counted or dropped early.

> **Best (and only reasonable) solution:**
> `MinimumSubarrayLengthWithDistinctSumAtLeastKImpl` — linear time, using a
> frequency map to make "add/remove from distinct sum" an `O(1)` amortized
> operation keyed on the `0 <-> 1` frequency transition, while the
> underlying two-pointer structure is justified by the same window
> monotonicity (Lemma 1) that powers the classic sliding-window pattern.
