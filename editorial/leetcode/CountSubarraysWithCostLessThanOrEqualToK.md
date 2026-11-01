# Count Subarrays With Cost Less Than Or Equal To K

## Problem Statement

You are given an integer array `nums` and an integer `k`. Define the
**cost** of a subarray `nums[l..r]` as:

```
cost(l, r) = (max(nums[l..r]) - min(nums[l..r])) * (r - l + 1)
```

Return the number of **subarrays** (contiguous, non-empty) whose cost is
**less than or equal to `k`**.

**Example:**
```
nums = [1, 3, 2], k = 4
Subarrays and costs:
  [1]       -> (1-1)*1 = 0   <= 4  ✓
  [3]       -> (3-3)*1 = 0   <= 4  ✓
  [2]       -> (2-2)*1 = 0   <= 4  ✓
  [1,3]     -> (3-1)*2 = 4   <= 4  ✓
  [3,2]     -> (3-2)*2 = 2   <= 4  ✓
  [1,3,2]   -> (3-1)*3 = 6   >  4  ✗
Answer = 5
```

**Constraints:** `nums.length` up to `10^5`, values and `k` fit in standard
integer ranges (the running sum can overflow `int`, so the answer is
returned as `long`).

---

## Solution — Sliding Window with Monotonic Min/Max Deques

**Key Insight.** For a fixed left boundary `l`, as the right boundary `r`
grows, both `max(nums[l..r]) - min(nums[l..r])` and `(r - l + 1)` are
**non-decreasing**. Their product, `cost(l, r)`, is therefore also
non-decreasing in `r`. So the set of valid `r` for a fixed `l` is a prefix
`{l, l+1, ..., R(l)}`, and once `cost(l, r) > k` it stays `> k` for every
larger `r`.

Symmetrically, shrinking the window by advancing `l` (removing elements
from the left) can only decrease or keep the same `max - min`, so `R(l)` is
**non-decreasing** as `l` increases. This is exactly the monotonic
two-pointer shape: both `l` and `r` only move forward, giving an `O(n)`
total number of pointer moves.

To get `max` and `min` of the current window in `O(1)`, the window is
backed by two classic **monotonic deques**:
- `minDeque` — indices with strictly increasing values front-to-back; the
  front always holds the index of the window's minimum.
- `maxDeque` — indices with strictly decreasing values front-to-back; the
  front always holds the index of the window's maximum.

```java
package dev.priyanshu.leetcode.subarray;

import java.util.Deque;

public interface CountSubarraysWithCostLessThanOrEqualToK {
    long countSubarrays(int[] nums, long k);
}

class CountSubarraysWithCostLessThanOrEqualToKImpl implements CountSubarraysWithCostLessThanOrEqualToK {

    private void add(int[] nums, int i, Deque<Integer> minDeque, Deque<Integer> maxDeque) {
        while(!minDeque.isEmpty() && nums[minDeque.peekLast()] >= nums[i]) {
            minDeque.pollLast();
        }
        minDeque.addLast(i);

        while(!maxDeque.isEmpty() && nums[maxDeque.peekLast()] <= nums[i]) {
            maxDeque.pollLast();
        }
        maxDeque.addLast(i);
    }

    private void remove(int[] nums, int i, Deque<Integer> minDeque, Deque<Integer> maxDeque) {
        if (!minDeque.isEmpty() && minDeque.peekFirst() == i) {
            minDeque.pollFirst();
        }

        if (!maxDeque.isEmpty() && maxDeque.peekFirst() == i) {
            maxDeque.pollFirst();
        }
    }

    private long cost(int[] nums, Deque<Integer> maxDeque, Deque<Integer> minDeque, int l, int r) {
        assert !maxDeque.isEmpty();
        assert !minDeque.isEmpty();

        return (long) (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()]) * (r - l + 1);
    }

    @Override
    public long countSubarrays(int[] nums, long k) {
        Deque<Integer> minDeque = new java.util.ArrayDeque<>();
        Deque<Integer> maxDeque = new java.util.ArrayDeque<>();

        int l = 0;
        long result = 0;

        for (int r = 0; r < nums.length; r++) {
            add(nums, r, minDeque, maxDeque);

            while (cost(nums, maxDeque, minDeque, l, r) > k) {
                remove(nums, l, minDeque, maxDeque);
                l++;
            }

            result += r - l + 1;
        }
        return result;
    }
}
```

**How it works:**
1. `add(i, ...)` pushes index `i` onto both deques, first popping from the
   **back** any index whose value is no longer useful (`>= nums[i]` for the
   min deque, `<= nums[i]` for the max deque). This keeps both deques
   monotonic and their fronts equal to the current window's min and max.
2. `remove(i, ...)` is called only when `i == l`, the index about to leave
   the window on the left. Because both deques store indices in strictly
   increasing order, index `l` — if it is still tracked at all — must be at
   the **front**, so popping the front (if it matches) is enough; there is
   nothing to check elsewhere in the deque.
3. `cost(l, r)` reads the window extremes straight from the deque fronts:
   `O(1)`.
4. The main loop is the standard **two-pointer shrink-on-violation**
   pattern: add `r`, then shrink from the left while the window's cost
   exceeds `k`, then add `r - l + 1` — the number of subarrays ending at
   `r` whose left boundary is somewhere in the now-valid window `[l, r]`.

> **Why `long`?** With `n = 10^5`, the total number of subarrays can reach
> `n(n+1)/2 ≈ 5 · 10^9`, which overflows `int`.

---

## Step-by-step Trace

```
nums = [1, 3, 2], k = 4

r=0: add(0)                       minDeque=[0] maxDeque=[0]
     cost(0,0) = (1-1)*1 = 0 <= 4   no shrink
     result += 0-0+1 = 1            total=1

r=1: add(1)                       minDeque=[0,1] maxDeque=[1]   (nums[0]=1 kept as min, nums[1]=3 is new max)
     cost(0,1) = (3-1)*2 = 4 <= 4   no shrink
     result += 1-0+1 = 2            total=3

r=2: add(2)                       minDeque=[0,1,2]* maxDeque=[1,2]
     (* nums[2]=2 >= nums[1]=3? no, so minDeque back check: nums[1]=3>=2 -> pop 1; minDeque=[0,2])
     cost(0,2) = (3-1)*3 = 6 >  4   shrink: remove(l=0) -> minDeque front 0 matches, pop -> minDeque=[2]
                                     maxDeque front 1 != 0, unchanged
                                     l=1
     cost(1,2) = (3-2)*2 = 2 <= 4   no further shrink
     result += 2-1+1 = 2            total=5

Answer: 5 ✓
```

---

## Correctness Proof

Let `n = nums.length`. For a fixed left boundary `l`, let `R(l)` be the
largest `r ≥ l - 1` such that `nums[l..r]` is valid (`cost(l, r) ≤ k`), with
the convention `R(l) = l - 1` (empty, contributing 0) if even the
single-element window is invalid — this cannot happen since
`cost(l, l) = 0 ≤ k` for `k ≥ 0`, so in practice `R(l) ≥ l`.

### Lemma 1 (Cost is non-decreasing in `r`)

For fixed `l`, `cost(l, r)` is non-decreasing as `r` increases.

*Proof.* `max(nums[l..r+1]) ≥ max(nums[l..r])` and
`min(nums[l..r+1]) ≤ min(nums[l..r])`, so the spread
`max - min` cannot shrink. The length `r - l + 1` strictly increases. The
product of a non-decreasing non-negative factor and a strictly increasing
positive factor is non-decreasing. ∎

Hence, for fixed `l`, valid `r` form exactly the prefix `{l, ..., R(l)}`,
contributing `R(l) - l + 1` subarrays.

### Lemma 2 (`R(l)` is non-decreasing in `l`)

If `l < l'` then `R(l) ≤ R(l')`.

*Proof.* `nums[l'..r]` is a sub-window of `nums[l..r]` for any `r ≥ l'`, so
its max-min spread cannot exceed that of `nums[l..r]`, and its length is
shorter. Hence `cost(l', r) ≤ cost(l, r)` for all `r ≥ l'`. If
`cost(l, R(l)) ≤ k` then `cost(l', R(l)) ≤ cost(l, R(l)) ≤ k`, so
`R(l') ≥ R(l)`. ∎

This justifies the two-pointer approach: `l` need never move backward to
find `R(l)`.

### Deque invariant

**Invariant (per deque).** At all times, `minDeque` holds exactly the
indices `i ∈ [l, r]` for which `nums[i]` is strictly less than
`nums[j]` for every later `j ∈ [l, r]` with `j > i` that is still tracked —
i.e., the classic "monotonic minimum" candidates — stored in strictly
increasing index order and strictly increasing value order front-to-back.
Symmetrically for `maxDeque` with strictly decreasing values. Consequently
`nums[minDeque.peekFirst()] = min(nums[l..r])` and
`nums[maxDeque.peekFirst()] = max(nums[l..r])`.

- **Initialization.** Both deques start empty; the invariant holds
  vacuously for the empty window.
- **Maintenance on `add(r)`.** Popping from the back any index whose value
  is `≥ nums[r]` (resp. `≤ nums[r]` for max) removes indices that can never
  again be the window minimum (resp. maximum) while `r` remains in the
  window, since `nums[r]` is both later and no larger (resp. no smaller).
  Pushing `r` restores strictly increasing index/value order.
- **Maintenance on `remove(l)`.** Because indices in each deque are in
  strictly increasing order, the smallest surviving index is always at the
  front. If `l` is still tracked, it must therefore be the front element,
  so checking and popping only the front correctly evicts it (and if `l`
  was already pruned by an earlier `add`, the front is some index `> l`
  and nothing is popped, which is correct — `l` was never the extremum to
  begin with).

### Main loop invariant

**Invariant.** At the top of each iteration of the `for` loop (value of
`r`), `l` is the smallest valid left boundary for the current `r`, i.e.
`l = min{ l' : cost(l', r) ≤ k }`, and `result` equals the number of valid
subarrays with right end `< r`.

- **Initialization.** Before `r = 0`, `l = 0` and `result = 0` trivially
  satisfy this for an empty range of right ends.
- **Maintenance.** After `add(r)`, the `while` loop advances `l` exactly
  while `cost(l, r) > k`. By Lemma 1, once `cost(l, r) ≤ k` for some `l`,
  it remains so as `r` only grows later, and by Lemma 2 the minimal valid
  `l` for this `r` is reached by only moving `l` forward from its value for
  `r - 1` (never needing to go back). So the loop terminates with `l`
  equal to the smallest valid left boundary for the *current* `r` — unless
  `l` reaches `r + 1`... but `cost(l, l) = 0 ≤ k` always holds for `k ≥ 0`,
  so the loop always stops with `l ≤ r`. By Lemma 1, every `l'` with
  `l ≤ l' ≤ r` is valid, and `result += r - l + 1` adds exactly those
  subarrays ending at `r`.
- **Termination.** The loop runs until `r = n`; each of `l` and `r` only
  increases and is bounded by `n`, so the process terminates.

Summing the contribution `R(l) - l + 1`... equivalently, summing
`r - l + 1` for every `r` from `0` to `n - 1` counts, for each valid pair
`(l, r)`, exactly once (at the iteration for that `r`). This equals the
total number of valid subarrays. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| Each index pushed to each deque once (`add`) | O(n) amortized | O(n) |
| Each index popped from each deque at most once (`add` back-pops + `remove` front-pop) | O(n) amortized | — |
| `l` and `r` each advance at most `n` times total | O(n) | O(1) |
| **Overall** | **O(n)** | **O(n)** |

> The inner `while (cost(...) > k)` does not make this `O(n²)`: `l` never
> resets and never exceeds `r`, so across the whole run it advances at most
> `n` times in total — the same amortized argument as the classic
> monotonic-deque sliding-window maximum/minimum problems.
