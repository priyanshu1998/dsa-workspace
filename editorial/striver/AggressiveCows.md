# Aggressive Cows

## Problem Statement

Given an array `nums` of `n` stall positions (not necessarily sorted) and an integer `k` (number of cows), place all `k` cows into stalls so that the **minimum distance between any two cows is as large as possible**. Return that maximum possible minimum distance.

**Example:**
```
nums = [0, 3, 4, 7, 10, 9], k = 4   → 3
nums = [4, 2, 1, 3, 6],     k = 2   → 5
nums = [10, 1, 2, 7, 5],    k = 3   → 4
```

---

## Solutions

### 1. Naive Solution — O(n · maxDist) Time, O(n log n) Space (for the sort)

Sort the stalls. Try every candidate distance `d` starting from `1` upward (up to the largest gap `nums[n-1] - nums[0]`). For each `d`, greedily check whether `k` cows can be placed such that every pair of consecutive placed cows is at least `d` apart. The largest `d` for which this check succeeds is the answer, since "can we place `k` cows at distance `>= d`" is monotonically non-increasing in `d`.

```java
int aggressiveCows(int[] nums, int k) {
    Arrays.sort(nums);
    int maxDist = nums[nums.length - 1] - nums[0];
    int best = 0;
    for (int d = 1; d <= maxDist; d++) {
        if (canBePlaced(nums, k, d)) {
            best = d;
        } else {
            break; // once infeasible, no larger d can work
        }
    }
    return best;
}
```

**Complexity:**
Time: O(n log n + n · maxDist) — sorting plus up to `maxDist` candidate distances tried, each costing O(n) to evaluate.
Space: O(log n) (sort stack) / O(1) extra beyond input.

**Drawback:** Ignores the fact that "can place `k` cows at distance `d`" is monotonic in `d` — a property that enables binary search instead of linear scanning over every candidate distance.

---

### 2. Optimized Solution (Binary Search on Answer) — O(n log n + n · log(maxDist)) Time, O(1) Extra Space

**Key Insight:** Sort the stalls first. Define `canBePlaced(d)` = "can we place at least `k` cows in sorted `nums` such that every two consecutive chosen cows are `>= d` apart (greedily, always placing the next cow at the first stall far enough from the last placed one)". As `d` increases, `canBePlaced(d)` is monotonically non-increasing (a larger required gap never makes placement easier). We want the **largest** `d` with `canBePlaced(d) == true`. This is the classic "search for the boundary of a monotonic predicate" pattern — binary search the answer space `[0, maxDist]` instead of the input array.

```java
class AggressiveCowsImpl implements AggressiveCows {

    private boolean canBePlaced(int[] nums, int k, int dist) {
        int i = 0;
        int count = 1; // first cow always placed at nums[0]
        for (int j = 1; j < nums.length; j++) {
            if (nums[j] - nums[i] >= dist) {
                i = j;
                count++;
            }
        }
        return count >= k;
    }

    @Override
    public int aggressiveCows(int[] nums, int k) {
        Arrays.sort(nums);

        int l = 0;
        int r = nums[nums.length - 1] - nums[0];

        while (l < r) {
            int mid = l + (r - l + 1) / 2; // upper mid, to search for the max

            if (canBePlaced(nums, k, mid)) {
                l = mid;
            } else {
                r = mid - 1;
            }
        }

        return l;
    }
}
```

**How it works:**
1. The search space is not the array — it is the set of possible minimum distances `[0, nums[n-1] - nums[0]]` (any larger distance is trivially infeasible since it exceeds the full span of stalls).
2. `canBePlaced(nums, k, d)` evaluates the monotone predicate in O(n) by greedily scanning sorted `nums` left to right, always placing the next cow as early as possible once a stall is `>= d` away from the last placed cow (greedy placement is optimal: delaying a placement can only reduce future flexibility, never increase the count achievable at a fixed `d`).
3. Binary search converges on the **largest** `d` for which the predicate is `true`: `mid = l + (r - l + 1) / 2` is the *upper* mid (to avoid infinite looping when `l = r - 1`); if `mid` is feasible, it's a candidate to keep (`l = mid`, never discard), otherwise `mid` is proven infeasible (`r = mid - 1`, safely discard it and everything above it, since the predicate only gets harder as `d` grows).
4. The loop ends when `l == r`, which is exactly the boundary — the last `d` where the predicate is still `true`.

**Complexity:**
Time: O(n log n + n · log(maxDist)) — O(n log n) to sort, then O(log maxDist) iterations each doing an O(n) feasibility check.
Space: O(1) extra (beyond the sort).

---

## Formal Proof of Correctness

**Setup.** Let `nums` be sorted (WLOG, since placement order doesn't depend on original indices) with `n` stalls and let `k <= n` be the number of cows. For any integer distance `d >= 0`, define the **greedy placement count**:

```
g(d) = number of cows placed by scanning nums left to right,
       always placing the first cow at nums[0], and placing the
       next cow at the first subsequent stall nums[j] with
       nums[j] - (position of last placed cow) >= d.
```

Let `d* = max { d >= 0 : g(d) >= k }` (this exists and is well-defined because `g(0) = n >= k` always holds — placing every stall trivially satisfies a zero-gap requirement). We must show the algorithm returns `d*`.

**Lemma 1 (Greedy optimality).** For a fixed `d`, the greedy scan computes the *maximum* number of cows placeable in sorted `nums` such that consecutive chosen stalls differ by `>= d`.
*Proof (exchange argument).* Suppose an optimal placement `P` chooses cows at positions `p_1 < p_2 < ... < p_m` (`m` maximal). Let the greedy placement choose `g_1 < g_2 < ... < g_t`. We show by induction that `g_i <= p_i` for all `i <= min(m, t)`. Base case: `g_1 = p_1 = nums[0]`. Inductive step: assume `g_i <= p_i`. The greedy algorithm picks `g_{i+1}` as the *earliest* stall `>= g_i + d`; since `p_{i+1} >= p_i + d >= g_i + d`, `p_{i+1}` is itself a valid candidate for `g_{i+1}`, so `g_{i+1} <= p_{i+1}` (greedy picks the earliest valid one). Since greedy never falls behind `P` at any prefix, whenever `P` can extend to a `(i+1)`-th cow, greedy — being at or before `P`'s position — can too. Hence `t >= m`, i.e., greedy places at least as many cows as any valid placement, so greedy is optimal: `g(d)` is indeed the maximum feasible count at distance `d`. ∎

**Lemma 2 (Monotonicity).** For all `d >= 0`, `g(d + 1) <= g(d)`.
*Proof.* Any placement satisfying consecutive-gap `>= d + 1` also satisfies gap `>= d`. So the set of valid placements at distance `d+1` is a subset of those valid at distance `d`. Taking the maximum placeable count (Lemma 1) over a subset of feasible placements can only be `<=` the maximum over the superset, giving `g(d+1) <= g(d)`. ∎

This monotonicity means the predicate `canBePlaced(nums, k, d) ≡ (g(d) >= k)` is **monotone (anti-tone in the useful direction)**: once `false` for some `d`, it remains `false` for every `d' > d` (by Lemma 2, if `g(d) < k` then `g(d') <= g(d) < k` for `d' > d`). So the set of feasible distances `{d : g(d) >= k}` is exactly the prefix `[0, d*]`, and binary search for the right boundary of this prefix is well-defined.

**Loop Invariant.** At the start (and end) of every iteration of the `while (l < r)` loop:
> `0 <= l <= d* <= r <= nums[n-1] - nums[0]`

i.e., the window `[l, r]` always contains `d*`.

**Initialization.** Before the first iteration, `l = 0` and `r = nums[n-1] - nums[0]`. Since `d*` is defined as the max feasible distance and `g(0) = n >= k` is always feasible, `d* >= 0 = l`. Also `d* <= nums[n-1] - nums[0] = r`, because no gap larger than the full span can ever be realized between consecutive placed cows (there is no room for it), so `g(d) = 1 < k` for any `d > nums[n-1] - nums[0]` once `k >= 2` (and the case `k <= 1` is trivially bounded too). Thus the invariant `l <= d* <= r` holds initially.

**Maintenance.** Assume the invariant holds at the start of an iteration with `l < r` (so the loop body executes). Let `mid = l + (r - l + 1) / 2` (upper mid), so `l < mid <= r`.

*Case A: `canBePlaced(nums, k, mid)` is `true`, i.e., `g(mid) >= k`.*
By definition, `mid` is a feasible distance, so by maximality of `d*`, `mid <= d*`. Combined with the invariant `d* <= r`, and now `mid <= d*`, updating `l = mid` preserves `l <= d* <= r` (since `mid <= d*` directly gives the new `l <= d*`).

*Case B: `canBePlaced(nums, k, mid)` is `false`, i.e., `g(mid) < k`.*
By Lemma 2 (monotonicity), `mid` infeasible implies every `d >= mid` is also infeasible (since `g` is non-increasing, `g(d) <= g(mid) < k` for `d >= mid`). Since `d*` is feasible by definition, `d*` cannot be `>= mid`, so `d* < mid`, i.e., `d* <= mid - 1`. Combined with the invariant `l <= d*`, updating `r = mid - 1` preserves `l <= d* <= r`.

In both cases the invariant holds after the update, and the window strictly shrinks: either `l` moves up to `mid > l`, or `r` moves down to `mid - 1 < r` — either way, `r - l` strictly decreases. (Using the *upper* mid is essential here: if the lower mid `l + (r - l) / 2` were used instead with `l = mid` in Case A, the window could fail to shrink when `r = l + 1`, causing an infinite loop.)

**Termination (decreasing variant).** Let the measure be `h = r - l >= 0`. Each iteration strictly decreases `h` (shown above), and `h` is a non-negative integer bounded below by `0` (the loop condition `l < r` fails exactly when `h = 0`). By well-foundedness of the non-negative integers under `<`, the loop terminates after finitely many iterations — specifically after at most `⌈log2(nums[n-1] - nums[0] + 1)⌉` iterations, since the window at least halves each time.

**Conclusion.** At termination, `l == r`. By the invariant `l <= d* <= r`, this forces `l = r = d*`. The algorithm returns `l`, which equals `d*` — the maximum minimum distance achievable while placing all `k` cows. ∎

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Sort, then try every distance d = 1, 2, 3, ... until
       canBePlaced(d) fails (first failure ends the search, since
       feasibility only shrinks as d grows).
       Time: O(n log n + n · maxDist)    Space: O(1) extra

           ▼  Observation: g(d) (max cows placeable at gap >= d) is
              monotonically non-increasing in d, so the set of feasible
              distances is a contiguous prefix [0, d*] — the classic
              signature for binary-searching the answer space instead
              of scanning it.

Binary Search on Answer ─────────────────────────────────────────────►
       Binary search d in [0, nums[n-1] - nums[0]]; feasibility check
       costs O(n) via a greedy scan.
       l = mid when mid is feasible (candidate answer, never discard),
       r = mid - 1 when mid is infeasible (proven too strict, safely
       discard it and everything above it).
       Time: O(n log n + n · log maxDist)    Space: O(1) extra
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n log n + n · maxDist) | O(1) extra | Scans every candidate distance linearly |
| Binary Search on Answer | O(n log n + n · log maxDist) | O(1) extra | Exploits monotonicity of placeable-cow-count in distance |

> **Best solution:** `AggressiveCowsImpl` (Binary Search on Answer) — O(n log n + n · log maxDist) time with O(1) extra space, exponentially faster than the naive linear scan over candidate distances.

---

## Implementation Note

The reference implementation in `AggressiveCowsImpl` contains a leftover debug statement (`System.out.printf` inside `aggressiveCows`). It does not affect correctness and is excluded from the code listings above for clarity; consider removing it in production code.
