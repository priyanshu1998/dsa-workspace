# Maximum Value of an Ordered Triplet II

## Problem Statement

You are given a **0-indexed** integer array `nums` of length `n` (`3 <= n <= 10^5`),
with `1 <= nums[i] <= 10^6` (every value is **positive**). Find the maximum
value over all choices of three indices `i < j < k` of the expression

```
(nums[i] - nums[j]) * nums[k]
```

If every triplet yields a negative (or zero) value, the answer is `0` — you
are never forced to report a negative result.

**Example:**
```
nums = [12, 6, 1, 2, 7]
Best triplet: i=0, j=2, k=4 → (12 - 1) * 7 = 77
Answer = 77

nums = [1, 10, 3, 4, 19]
Best triplet: i=1, j=2, k=4 → (10 - 3) * 19 = 133
Answer = 133

nums = [1, 2, 3]
No triplet yields a positive value → Answer = 0
```

The positivity of every `nums[i]` is the crucial fact that makes a linear-time
solution possible — it means the `nums[k]` factor is always `> 0`, so it never
flips the sign of a comparison, and the problem reduces cleanly to: *"for
each middle index `j`, what's the largest value seen before it, and the
largest value seen after it?"*

The constraints here (`n` up to `10^5`) rule out the `O(n^3)` brute force and
the `O(n^2)` improvement, pushing toward an `O(n)` solution.

---

## Solutions

### 1. Naive Solution — O(n³) Time, O(1) Space

Try every triplet `(i, j, k)` with `i < j < k` directly.

```java
class NaiveSolution implements MaximumValueOfAnOrderedTriplet2 {
    @Override
    public long maximumTripletValue(int[] nums) {
        long max = 0;
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    max = Math.max(max, (long) (nums[i] - nums[j]) * nums[k]);
                }
            }
        }
        return max;
    }
}
```

**Complexity:** Time `O(n^3)`, Space `O(1)`.

**Drawback:** Fixing `i` and `j` and then scanning every `k > j` is wasteful —
for a fixed `j`, the best `k` is simply the largest `nums[k]` among indices
`> j`, since `nums[k] > 0` always, so a larger `nums[k]` never makes things
worse (when the left factor is negative the whole product is already
`<= 0`, which can't beat the running max anyway, and when the left factor is
positive, a larger `nums[k]` strictly helps). So the innermost loop over `k`
can be replaced by one precomputed "best suffix value."

---

### 2. Fix `j`, Scan for Best `i`/`k` — O(n²) Time, O(1) Space

**Key Insight:** For a fixed `j`, the best `i < j` is the one maximizing
`nums[i]`, and the best `k > j` is the one maximizing `nums[k]` — both are
single linear scans, so each `j` costs `O(n)` instead of `O(n^2)`.

```java
class QuadraticSolution implements MaximumValueOfAnOrderedTriplet2 {
    @Override
    public long maximumTripletValue(int[] nums) {
        long max = 0;
        int n = nums.length;
        for (int j = 1; j < n - 1; j++) {
            int bestLeft = 0;
            for (int i = 0; i < j; i++) bestLeft = Math.max(bestLeft, nums[i]);

            int bestRight = 0;
            for (int k = j + 1; k < n; k++) bestRight = Math.max(bestRight, nums[k]);

            max = Math.max(max, (long) (bestLeft - nums[j]) * bestRight);
        }
        return max;
    }
}
```

**Complexity:** Time `O(n^2)`, Space `O(1)`.

**Drawback:** `bestLeft` for index `j` and `bestLeft` for index `j+1` differ
by at most one comparison (`nums[j]` joining the pool), yet the loop
recomputes the whole left scan from scratch every time. The same is true for
`bestRight`. Precomputing both running maxima **once**, in two linear passes,
removes the need to ever rescan.

---

### 3. Optimized Solution (Prefix/Suffix Maximum) — O(n) Time, O(n) Space

**Key Insight:** Precompute, for every index `j`:
- `prefixMax[j] = max(nums[0..j])`, the largest value at or before `j`,
- `suffixMax[j] = max(nums[j..n-1])`, the largest value at or after `j`.

Then, fixing `j` as the middle index, the best `i < j` contributes
`prefixMax[j-1]` and the best `k > j` contributes `suffixMax[j+1]`, so the
best triplet value with middle index `j` is
`(prefixMax[j-1] - nums[j]) * suffixMax[j+1]`, computable in `O(1)` once the
two arrays exist.

```java
package dev.priyanshu.leetcode.search.linear;

public interface MaximumValueOfAnOrderedTriplet2 {
    long maximumTripletValue(int[] nums);
}

class MaximumValueOfAnOrderedTriplet2Impl implements MaximumValueOfAnOrderedTriplet2 {

    @Override
    public long maximumTripletValue(int[] nums) {
        int[] prefixMax = new int[nums.length];
        int[] suffixMax = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            prefixMax[i] = i != 0 ? Math.max(prefixMax[i - 1], nums[i]) : nums[i];
        }

        for (int j = nums.length - 1; j >= 0; j--) {
            suffixMax[j] = j != nums.length - 1 ? Math.max(suffixMax[j + 1], nums[j]) : nums[j];
        }

        long max = 0;
        for (int j = 1; j < nums.length - 1; j++) {
            max = Math.max(max, (long) (prefixMax[j - 1] - nums[j]) * suffixMax[j + 1]);
        }

        return max;
    }
}
```

**How it works:**
1. `prefixMax[i]` is built left to right as a running maximum of
   `nums[0..i]`, so `prefixMax[j-1]` is exactly the best possible `nums[i]`
   for any `i < j`.
2. `suffixMax[j]` is built right to left as a running maximum of
   `nums[j..n-1]`, so `suffixMax[j+1]` is exactly the best possible `nums[k]`
   for any `k > j`.
3. Each candidate middle index `j` needs at least one index before it and
   one after it, hence `1 <= j <= n-2`. For each such `j`,
   `(prefixMax[j-1] - nums[j]) * suffixMax[j+1]` is compared against the
   running `max`, which starts at `0` so the "no triplet is good" case is
   handled automatically.
4. Both auxiliary arrays are built in one linear pass each, and the final
   loop over `j` is also linear, so the whole algorithm is `O(n)`.

**Complexity:** Time `O(n)`, Space `O(n)` for the two auxiliary arrays.
(The arrays can be collapsed into two scalars for `O(1)` extra space by
scanning `j` left to right while maintaining a running `prefixMax` and
precomputed `suffixMax` array, or via two scalar passes — but the two-array
form above is the clearest `O(n)` formulation and is what's implemented in
this repository.)

---

### Correctness Proof

**Definitions.** For `0 <= t <= n-1`, let `P(t) = max(nums[0..t])` and
`S(t) = max(nums[t..n-1])`. For `1 <= j <= n-2`, define
`f(j) = (P(j-1) - nums[j]) * S(j+1)`.
Let `OPT = max(0, max over all i<j<k of (nums[i]-nums[j]) * nums[k])` be the
true answer. Recall the problem's guarantee `nums[t] >= 1 > 0` for every `t`.

**Claim.** `OPT` equals `max(0, f(1), f(2), ..., f(n-2))`, which is exactly
the value computed by `maximumTripletValue`.

*Proof.*

**Part 1 — Every triplet's value is bounded by `max(0, max of all f(j))`.**

Let `(i, j, k)` be any valid triplet (`i < j < k`), and let
`V = (nums[i] - nums[j]) * nums[k]` be its value. Since `i <= j-1` and
`k >= j+1`, the definitions of `P` and `S` give
`nums[i] <= P(j-1)` and `0 < nums[k] <= S(j+1)`. We consider two cases on
the sign of `nums[i] - nums[j]`:

- **Case A: `nums[i] - nums[j] <= 0`.** Since `nums[k] > 0`, a nonpositive
  number times a positive number is nonpositive, so `V <= 0`, which is
  trivially `<= max(0, max of all f(j))`.

- **Case B: `nums[i] - nums[j] > 0`.** From `nums[i] <= P(j-1)` we get
  `nums[i] - nums[j] <= P(j-1) - nums[j]`, and since `nums[k] > 0`,
  multiplying both sides of this inequality by `nums[k]` preserves its
  direction:
  ```
  V = (nums[i] - nums[j]) * nums[k] <= (P(j-1) - nums[j]) * nums[k]
  ```
  Moreover `P(j-1) - nums[j] >= nums[i] - nums[j] > 0`, so it is positive;
  multiplying the nonnegative quantity `(P(j-1) - nums[j])` by the larger
  value `S(j+1) >= nums[k]` can only increase the product:
  ```
  (P(j-1) - nums[j]) * nums[k] <= (P(j-1) - nums[j]) * S(j+1) = f(j)
  ```
  Chaining the two inequalities, `V <= f(j)`.

In both cases `V <= max(0, f(j)) <= max(0, max of all f(j))`. Since
`(i, j, k)` was an arbitrary valid triplet, every triplet's value is bounded
by `max(0, max of all f(j))`, so
```
OPT = max(0, max over all triplets of their value) <= max(0, max of all f(j)).
```

**Part 2 — `f(j)` is achieved by some actual triplet, for every `j`.**

Fix `j` with `1 <= j <= n-2`. By definition of `P(j-1)`, there is an index
`i* <= j-1` (so `i* < j`) with `nums[i*] = P(j-1)`. By definition of
`S(j+1)`, there is an index `k* >= j+1` (so `k* > j`) with
`nums[k*] = S(j+1)`. The triplet `(i*, j, k*)` is valid (`i* < j < k*`) and
its value is
```
(nums[i*] - nums[j]) * nums[k*] = (P(j-1) - nums[j]) * S(j+1) = f(j).
```
So `f(j)` is an achievable triplet value, hence `f(j) <= OPT` for every `j`
in range (`OPT` is, by definition, at least as large as any single triplet's
value), giving `max of all f(j) <= OPT`. Also trivially `0 <= OPT`.
Combining, `max(0, max of all f(j)) <= OPT`.

**Conclusion.** Parts 1 and 2 together give both
`OPT <= max(0, max of all f(j))` and `max(0, max of all f(j)) <= OPT`, so
```
OPT = max(0, max over j in [1, n-2] of f(j)).
```
Finally, the algorithm initializes `max = 0` and, for every `j` from `1` to
`n-2`, updates `max = Math.max(max, (prefixMax[j-1] - nums[j]) * suffixMax[j+1])
= Math.max(max, f(j))`, since `prefixMax[j-1] = P(j-1)` and
`suffixMax[j+1] = S(j+1)` by construction — each is exactly the running
maximum over the claimed range, which follows by a straightforward induction
on the loop that builds it. After the loop, `max = max(0, max of all f(j))
= OPT`, so the algorithm returns the correct answer. QED.

---

### 4. Fully Optimized Solution (Single-Pass, Three Running Scalars) — O(n) Time, O(1) Space

**Can we do better than O(n) extra space?** The prefix/suffix solution above
never needs the *entire* `prefixMax`/`suffixMax` arrays at once — at the
moment index `k` is visited, all we need is the single best
`(prefixMax - nums[j])` value seen for any `j < k`, and that quantity can be
updated incrementally as we scan left to right, without ever looking ahead.

**Key Insight:** Walk the array once, treating each element, in order, as a
candidate for *all three* roles `i`, `j`, and `k` relative to the elements
seen so far — in that order — using three running scalars:
- `best_i` = the largest `nums[t]` seen so far (candidate for role `i`),
- `best_diff` = the largest `(best_i - nums[t])` seen so far, i.e. the best
  `(nums[i] - nums[j])` achievable using only elements up to the current
  position (candidate for role `j`),
- `ans` = the running answer.

For the current element `num` (at position `t`), update in this exact order:
1. **Use `num` as `k`:** `ans = max(ans, best_diff * num)` — this uses only
   `best_diff` values finalized using strictly earlier indices.
2. **Use `num` as `j`:** `best_diff = max(best_diff, best_i - num)` — this
   uses only `best_i` values finalized using strictly earlier indices.
3. **Use `num` as `i`:** `best_i = max(best_i, num)` — updated last, so `num`
   itself only becomes available as an `i` candidate for *future* indices.

```java
class LinearConstantSpaceSolution implements MaximumValueOfAnOrderedTriplet2 {
    @Override
    public long maximumTripletValue(int[] nums) {
        long ans = 0;
        int bestI = 0;
        long bestDiff = 0;

        for (int num : nums) {
            ans = Math.max(ans, bestDiff * num);
            bestDiff = Math.max(bestDiff, bestI - num);
            bestI = Math.max(bestI, num);
        }

        return ans;
    }
}
```

**How it works:**
1. Because step 3 (updating `best_i`) happens *after* steps 1 and 2 for the
   same element, `num` can never be used as its own `i`, `j`, or `k` in the
   same iteration relative to itself — every value folded into `best_diff`
   when `num` plays role `j` came from a strictly earlier index (so `i < j`),
   and every `best_diff` used when `num` plays role `k` was finalized using
   only indices strictly before the current one, each of which already
   enforced `i < j` at its own earlier formation point and is now paired with
   a later `k` (so `j < k`).
2. `best_i` after processing index `t` equals `prefixMax[t]` from the earlier
   solution; `best_diff` after processing index `t` equals
   `max over j<=t of (prefixMax[j-1] - nums[j])`.
3. No arrays are allocated — only three scalars — so space drops from `O(n)`
   to `O(1)`, while time remains a single linear pass, `O(n)`.

**Complexity:** Time `O(n)`, Space `O(1)`.

**This is asymptotically optimal:** any algorithm must inspect every element
of `nums` at least once to compute a function that depends on all of them, so
`O(n)` time is the best possible; `O(1)` extra space (beyond the input) is
the smallest possible footprint. No further asymptotic improvement is
possible.

---

### Correctness Proof (Single-Pass Solution)

**Definitions.** For `0 <= t <= n-1`, let `best_i(t)`, `best_diff(t)`, and
`ans(t)` denote the values of `bestI`, `bestDiff`, and `ans` immediately
**after** the loop body has processed index `t` (i.e., after step 3 for
index `t` completes). Define `best_diff(-1) = 0` and `best_i(-1) = 0`
(the values before any iteration).

**Lemma 1.** For every `t >= 0`, `best_i(t) = max(nums[0..t]) = P(t)`.

*Proof by induction.* `best_i(t) = max(best_i(t-1), nums[t])` by step 3, and
by the inductive hypothesis `best_i(t-1) = P(t-1)`, so
`best_i(t) = max(P(t-1), nums[t]) = P(t)`. Base case `t = 0`:
`best_i(0) = max(0, nums[0])`; since `nums[0] >= 1 > 0`, this equals
`nums[0] = P(0)`. QED.

**Lemma 2.** For every `t >= 0`, `best_diff(t) = max(0, max over j in [0, t]
of (P(j-1) - nums[j]))`, where `P(-1)` is treated as `0` (no `i` available
before `j = 0`, so no triplet can use `j = 0` as its middle index — this
term is only ever a harmless lower bound of `0 - nums[0] <= 0` folded into
the running max, never an actual usable triplet term since Part 2 of the
earlier proof already restricts valid middle indices to `j >= 1`).

*Proof by induction.* Step 2 at index `t` computes
`best_diff(t) = max(best_diff(t-1), best_i(t-1) - nums[t])`. By Lemma 1,
`best_i(t-1) = P(t-1)`, so the new term folded in is exactly
`P(t-1) - nums[t]`, i.e., the `f`-numerator term for middle index `j = t`.
By the inductive hypothesis, `best_diff(t-1)` already equals the running max
of this quantity over `j in [0, t-1]`, so
`best_diff(t) = max(0, max over j in [0,t] of (P(j-1) - nums[j]))`. QED.

**Lemma 3.** For every `t >= 0`,
`ans(t) = max(0, max over all pairs 1 <= j < k <= t of (P(j-1) - nums[j]) * nums[k])`
(the inner max is vacuously `-infinity`, i.e. dropped, if no such pair exists).

*Proof by induction.* **Base case `t = 0`:** no pair `j < k <= 0` exists, so
the claimed value is `max(0) = 0`; indeed `ans(0) = max(ans(-1), best_diff(-1)
* nums[0]) = max(0, 0) = 0`.

**Inductive step.** Step 1 at index `t` computes
`ans(t) = max(ans(t-1), best_diff(t-1) * nums[t])`. By the inductive
hypothesis, `ans(t-1)` already equals `max(0, max over pairs 1<=j<k<=t-1 of
the term)` — i.e., every pair with `k <= t-1` is accounted for. It remains to
show `best_diff(t-1) * nums[t]` contributes exactly the pairs with `k = t`.

By Lemma 2, `best_diff(t-1) = max(0, max over j in [0, t-1] of (P(j-1) -
nums[j]))`. Since `nums[t] > 0`, multiplying a max by a positive constant
distributes over the max:
```
best_diff(t-1) * nums[t] = max(0, max over j in [0, t-1] of (P(j-1) - nums[j]) * nums[t]).
```
The `j = 0` term equals `(P(-1) - nums[0]) * nums[t] = -nums[0] * nums[t] < 0`
(as `nums[0], nums[t] >= 1`), so it is strictly dominated by the `0` floor and
can be dropped without changing the max:
```
best_diff(t-1) * nums[t] = max(0, max over j in [1, t-1] of (P(j-1) - nums[j]) * nums[t]).
```
This is exactly `max(0, max over pairs j < k = t of the term)`. Taking the
max of this with `ans(t-1)` (pairs with `k <= t-1`) yields the max over all
pairs with `k <= t`, establishing the inductive step. QED.

**Theorem.** After the loop completes (`t = n-1`), `ans(n-1) = OPT`.

*Proof.* By Lemma 3 with `t = n-1`,
```
ans(n-1) = max(0, max over pairs 1<=j<k<=n-1 of (P(j-1) - nums[j]) * nums[k]).
```
Fix `j` in `[1, n-2]` and consider the inner maximization over `k` in
`(j, n-1]` of `(P(j-1) - nums[j]) * nums[k]`. This is the same case split as
Part 1/Part 2 of the array-based proof: if `P(j-1) - nums[j] <= 0`, every
term for this `j` is `<= 0`, contributing nothing beyond the `0` floor; if
`P(j-1) - nums[j] > 0`, the term is maximized by the largest `nums[k]` for
`k in (j, n-1]`, which is `S(j+1)`, giving exactly `f(j)`. So
`max over k in (j,n-1] of (P(j-1)-nums[j])*nums[k] = max(0, f(j))` in either
case, i.e. taking the max over `j in [1,n-2]` too,
```
ans(n-1) = max(0, max over j in [1, n-2] of f(j)).
```
By the Claim proven in the array-based Correctness Proof section above, this
right-hand side equals `OPT`. Hence `ans(n-1) = OPT`. QED.

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Try every triplet (i, j, k) directly and evaluate its value.
       Time: O(n³)    Space: O(1)

           ▼  Observation: for a fixed j, the best i < j is whichever
              maximizes nums[i], and the best k > j is whichever maximizes
              nums[k] — since nums[k] > 0 always, a bigger nums[k] is never
              harmful, and a negative left factor is already capped by 0.
              So the innermost loop over k (and the loop over i) can each be
              replaced by a single running maximum.

Fix j, Scan for Best i/k ──────────────────────────────────────────────►
       For each j, scan left for the best i and right for the best k.
       Time: O(n²)    Space: O(1)

           ▼  Observation: the "best i for j" and "best i for j+1" differ
              by only one new candidate (nums[j] itself), so the whole left
              scan doesn't need to be redone per j — nor does the right
              scan. Precompute both running maxima once, in two linear
              passes, and every j becomes an O(1) lookup.

Prefix/Suffix Maximum ─────────────────────────────────────────────────►
       prefixMax[i] = max(nums[0..i]), suffixMax[i] = max(nums[i..n-1]),
       answer = max over j of (prefixMax[j-1]-nums[j]) * suffixMax[j+1].
       Time: O(n)    Space: O(n)   ← two auxiliary arrays

           ▼  Observation: the algorithm never needs the full prefixMax or
              suffixMax arrays at once — at the moment index k is visited,
              only the single best (prefixMax - nums[j]) value for j < k
              matters, and that can be maintained incrementally while
              scanning once, left to right, updating "i candidate", then
              "j candidate", then "k candidate" roles in that exact order.

Single-Pass, Three Running Scalars ────────────────────────────────────►
       bestI = running max(nums[i]); bestDiff = running max(bestI - nums[j]);
       ans = running max(bestDiff * nums[k]) — updated in k, j, i order.
       Time: O(n)    Space: O(1)   ← no auxiliary arrays, asymptotically optimal
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n³) | O(1) | Evaluates every triplet directly |
| Fix j, Scan for Best i/k | O(n²) | O(1) | Per-`j` linear scans for best left/right value |
| Prefix/Suffix Maximum | O(n) | O(n) | Precomputed running maxima turn each `j` into an O(1) lookup |
| Single-Pass, Three Running Scalars | O(n) | O(1) | Same idea, collapsed into scalars updated in `k`, `j`, `i` order |

> **Best solution:** The single-pass, three-scalar version is the fully
> optimized solution — `O(n)` time is required just to read every element,
> and `O(1)` extra space is the minimum possible, so no further asymptotic
> improvement exists. It keeps the same core insight as
> `MaximumValueOfAnOrderedTriplet2Impl` (every `nums[k] > 0`, so comparisons
> never flip sign) but updates its "best `i`", "best `(i,j)` difference", and
> "best overall answer" as three scalars in a single pass, updating them in
> `k`-then-`j`-then-`i` order each iteration so that `i < j < k` is enforced
> purely by the order of updates rather than by separate arrays.
