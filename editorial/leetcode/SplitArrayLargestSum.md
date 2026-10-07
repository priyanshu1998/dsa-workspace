# Split Array Largest Sum

## Problem Statement

Given an integer array `nums` and an integer `k`, split `nums` into `k`
**non-empty, contiguous** subarrays such that the **largest sum** among these
`k` subarrays is **minimized**. Return that minimized largest sum.

**Example:**
```
nums = [7, 2, 5, 10, 8], k = 2
Best split: [7, 2, 5] | [10, 8]  → sums 14 and 18 → largest = 18
Answer = 18

nums = [1, 2, 3, 4, 5], k = 2
Best split: [1, 2, 3] | [4, 5]  → sums 6 and 9 → largest = 9
Answer = 9
```

Minimizing the maximum of `k` chunk sums is a classic **"minimize the
maximum"** shape. Whenever a quantity `isAMaximumSum(nums, k, cap)` — *"can
`nums` be split into at most `k` contiguous groups, each with sum ≤ `cap`?"* —
is **monotonic** in `cap` (false for small `cap`, true for large `cap`, and
once true stays true), the smallest feasible `cap` can be found with
**binary search on the answer**, using the feasibility check as the
predicate.

---

## Solution — Binary Search on the Answer + Greedy Feasibility Check

**Key Insight.** Define `canSplit(cap) = true` iff `nums` can be partitioned
into **at most `k`** contiguous, non-empty subarrays each with sum `≤ cap`.

1. `canSplit` is **monotonic**: if `cap₁ < cap₂` and `canSplit(cap₁)` is
   true, then `canSplit(cap₂)` is also true (any valid split for a tighter
   cap is also valid for a looser one).
2. The answer is the **smallest** `cap` for which `canSplit(cap)` holds, and
   it always lies in `[max(nums), sum(nums)]` — `max(nums)` because every
   single element must fit in some group, and `sum(nums)` because one giant
   group (`k ≥ 1`) always works.
3. For a *fixed* `cap`, the minimum number of groups needed is computed
   **greedily**: scan left to right, keep extending the current group while
   `runningSum + num ≤ cap`; otherwise close the group and start a new one
   with `num`. This greedy group count is proven optimal below (Lemma 1).

Binary search then finds the smallest `cap` in `[max(nums), sum(nums)]` for
which the greedy group count is `≤ k`.

```java
public interface SplitArrayLargestSum {
    int splitArray(int[] nums, int k);
}

class SplitArrayLargestSumImpl implements SplitArrayLargestSum {

    private int max(int[] nums) {
        int M = nums[0];
        for (int num : nums) M = Math.max(M, num);
        return M;
    }

    private int sum(int[] nums) {
        int tot = 0;
        for (int num : nums) tot += num;
        return tot;
    }

    private boolean isAMaximumSum(int[] nums, int k, int sum) {
        int cnt = 1;
        int runningSum = 0;
        for (int num : nums) {
            if (runningSum + num > sum) {
                cnt++;
                runningSum = num;
            } else {
                runningSum += num;
            }
        }
        return cnt <= k;
    }

    @Override
    public int splitArray(int[] nums, int k) {
        int l = max(nums);
        int r = sum(nums);

        while (l < r) {
            int mid = l + (r - l) / 2;
            if (isAMaximumSum(nums, k, mid)) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return r;
    }
}
```

**How it works:**
1. `l = max(nums)` and `r = sum(nums)` bracket the true answer on both ends.
2. Each iteration tests the midpoint `mid` as a candidate cap:
   - If `nums` can be split into `≤ k` groups with cap `mid`
     (`isAMaximumSum` returns `true`), `mid` is a feasible cap, so the answer
     is `≤ mid`; shrink the right bound to `r = mid`.
   - Otherwise `mid` is too small — not even the greedy (optimal) packing
     can do it in `≤ k` groups — so the answer must be `> mid`; raise the
     left bound to `l = mid + 1`.
3. The loop narrows `[l, r]` until `l == r`, which is exactly the smallest
   feasible cap.

---

## Correctness Proof

### Lemma 1 (Greedy feasibility check is exact)

For a fixed capacity `cap`, let `g(cap)` be the number of groups produced by
the greedy scan in `isAMaximumSum` (extend the current group until adding the
next element would exceed `cap`, then start a new group). Let `m(cap)` be the
**minimum** number of contiguous groups, each with sum `≤ cap`, needed to
cover all of `nums` (undefined / `+∞` if even single elements exceed `cap`).
Then `g(cap) = m(cap)` whenever `cap ≥ max(nums)`.

*Proof (exchange argument).* Clearly `g(cap) ≥ m(cap)`, since the greedy scan
produces *some* valid split, and `m(cap)` is a minimum over all valid splits.

For the reverse direction, we show by induction on groups that the greedy
split is never "behind" any optimal split. Let `g₁, g₂, …` be the greedy
groups (in order, with endpoints as indices into `nums`) and
`o₁, o₂, …, o_{m}` be the groups of *some* optimal split achieving `m(cap)`
groups. We prove: for every `t`, the greedy group `g_t` ends at an index
`≥` the index where `o_t` ends (i.e., greedy's `t`-th group reaches at least
as far right as the optimal's `t`-th group).

- **Base case `t = 1`:** `g₁` is, by construction, the **longest** prefix of
  `nums` whose sum is `≤ cap` (the greedy scan only closes a group when the
  *next* element would overflow it). `o₁` is some prefix with sum `≤ cap`.
  Since `g₁` is the longest such prefix, `g₁` ends at or after where `o₁`
  ends.
- **Inductive step:** assume `g_t` ends at index `≥` where `o_t` ends. Group
  `g_{t+1}` starts immediately after `g_t` and, by the same "longest valid
  extension" argument, is the longest run starting there with sum `≤ cap`.
  Because `g_t` ends no earlier than `o_t`, the suffix available to
  `g_{t+1}` starts no later than the suffix available to `o_{t+1}`, so the
  longest valid run from `g_{t+1}`'s start reaches at least as far as
  `o_{t+1}` does (a valid group for the optimal split starting later is a
  valid, possibly extendable, candidate from `g_{t+1}`'s earlier starting
  point too, since all sums are non-negative — prefix sums only grow).
  Hence `g_{t+1}` ends at or after where `o_{t+1}` ends.

By induction, after `m(cap)` groups the greedy scan has covered at least as
much of `nums` as the optimal split has after its `m(cap)` groups — i.e., all
of `nums`. Therefore the greedy scan needs **at most** `m(cap)` groups to
finish, so `g(cap) ≤ m(cap)`. Combined with `g(cap) ≥ m(cap)`, we get
`g(cap) = m(cap)`. ∎

Consequently, `isAMaximumSum(nums, k, cap) = (g(cap) ≤ k) = (m(cap) ≤ k)`, so
the greedy check exactly answers the feasibility question and is not an
approximation.

### Lemma 2 (Monotonicity of feasibility)

If `cap₁ < cap₂` and `m(cap₁) ≤ k`, then `m(cap₂) ≤ k`.

*Proof.* Take any split achieving `m(cap₁)` groups, each with sum `≤ cap₁`.
Since `cap₁ < cap₂`, every one of those groups also satisfies sum `≤ cap₂`,
so the **same split** is valid for `cap₂`, using `m(cap₁) ≤ k` groups. Hence
`m(cap₂) ≤ m(cap₁) ≤ k`. ∎

This means `isAMaximumSum(nums, k, ·)` is a monotonic predicate: false for all
`cap` below some threshold `ANSWER`, true for all `cap ≥ ANSWER`, where
`ANSWER = min { cap : m(cap) ≤ k }` is exactly the quantity `splitArray`
must return.

### Lemma 3 (The search bracket is valid and contains the answer)

`max(nums) ≤ ANSWER ≤ sum(nums)`.

*Proof.*
- *Lower bound:* for any `cap < max(nums)`, the element equal to
  `max(nums)` cannot fit in any single group, so no valid split exists at
  all — `m(cap) = +∞ > k`. Hence `ANSWER ≥ max(nums)`.
- *Upper bound:* `cap = sum(nums)` always admits the trivial split consisting
  of one group containing the whole array (valid for any `k ≥ 1`, since the
  problem guarantees `1 ≤ k ≤ nums.length`), so `m(sum(nums)) = 1 ≤ k`. Hence
  `ANSWER ≤ sum(nums)`. ∎

### Theorem (Binary search finds `ANSWER`)

**Loop invariant.** Before each iteration of the `while (l < r)` loop,
`l ≤ ANSWER ≤ r`, `isAMaximumSum(nums, k, l - 1)` is false (or `l` equals the
initial `max(nums)`, trivially the smallest candidate), and
`isAMaximumSum(nums, k, r)` is true.

- **Initialization.** Before the first iteration, `l = max(nums)` and
  `r = sum(nums)`. By Lemma 3, `ANSWER` lies in `[max(nums), sum(nums)]`, so
  `l ≤ ANSWER ≤ r`. By Lemma 1, `isAMaximumSum(nums, k, sum(nums))` is true
  (the single-group split witnesses `m(sum(nums)) = 1 ≤ k`), establishing the
  invariant's `r` condition.

- **Maintenance.** Assume `l ≤ ANSWER ≤ r` and `isAMaximumSum(nums, k, r)` is
  true before an iteration with `l < r`. Let `mid = l + (r - l)/2`; since
  `l < r`, `l ≤ mid < r`.
  - If `isAMaximumSum(nums, k, mid)` is true, then by Lemma 2's
    contrapositive `mid ≥ ANSWER` (feasibility at `mid` means the threshold
    has already been reached), so setting `r = mid` keeps `ANSWER ≤ r` and
    preserves "`isAMaximumSum` true at `r`".
  - If `isAMaximumSum(nums, k, mid)` is false, then `mid < ANSWER` (by
    definition of `ANSWER` as the smallest feasible cap, and monotonicity),
    so `ANSWER ≥ mid + 1`; setting `l = mid + 1` keeps `l ≤ ANSWER`. The `r`
    bound and its feasibility are untouched, so they still hold.

  In both branches, `l ≤ ANSWER ≤ r` continues to hold after the update.

- **Termination.** Each iteration strictly shrinks `r - l`: when `r = mid`,
  the new width is `mid - l < r - l` (since `mid < r`); when `l = mid + 1`,
  the new width is `r - mid - 1 < r - l` (since `mid ≥ l`). As `r - l` is a
  non-negative integer that strictly decreases every iteration, the loop
  terminates after finitely many steps, at which point `l = r`. Since the
  invariant `l ≤ ANSWER ≤ r` holds at all times, `l = r` forces
  `l = r = ANSWER`.

The method returns `r`, which by the above equals `ANSWER`, the minimized
largest subarray sum. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `max` / `sum` (bracket computation) | O(n) | O(1) |
| `isAMaximumSum` (one feasibility check) | O(n) | O(1) |
| Binary search over `[max(nums), sum(nums)]` | O(log(sum(nums) − max(nums))) checks | — |
| **Overall** | **O(n · log(sum(nums) − max(nums)))** | **O(1)** |

> Each binary-search step costs O(n) for the greedy feasibility scan, and the
> search space shrinks geometrically, giving the final
> `O(n log(sum − max))` bound — far better than enumerating all ways to
> place `k − 1` split points, which is exponential in the naive case.
