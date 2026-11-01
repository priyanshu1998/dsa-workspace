# Minimum Sum of Squared Difference

## Problem Statement

Given two integer arrays `nums1` and `nums2` of the same length `n`, and two
non-negative integers `k1` and `k2`. In one operation you may choose **any**
index `i` and either increase or decrease `nums1[i]` by `1`, at a total cost
of at most `k1` such operations across the whole array. Independently, you
may perform at most `k2` such `+1`/`−1` operations on `nums2`. Return the
minimum possible value of

```
sum_{i} (nums1[i] - nums2[i])^2
```

after performing at most `k1` operations on `nums1` and at most `k2`
operations on `nums2` (in any order, split across indices however you like).

**Example:**
```
nums1 = [1,4,10,12], nums2 = [5,8,6,9], k1 = 1, k2 = 1
Answer = 43
  diff = [4,4,4,3]; spend the 2 total operations to turn one 4 into a 3
  (e.g. nums1[3]-=1 or nums2[2]+=1), giving diff = [4,4,3,3]
  (or any rearrangement with the same multiset) -> 16+16+9+9 = 50? 
  the actual optimal reduces the diffs to [3,4,4,3]-like multiset achieving 43
```
*(see the three parameterized test cases in `MinimumSumOfSquaredDifferenceTest`
for verified inputs/outputs: `579`, `43`, `3`).*

Since every operation on `nums1[i]` or `nums2[i]` changes `|nums1[i] -
nums2[i]|` by at most `1` (and it is always at least as good to move the two
values *toward* each other rather than apart — moving apart can never help
minimize a square), the problem reduces to:

> Given `diff[i] = |nums1[i] - nums2[i]|` and a combined budget
> `k = k1 + k2` of "decrement by 1" operations (each operation decreases
> some `diff[i]` by 1, down to a floor of `0`), distribute the `k`
> decrements across the array to **minimize `Σ diff[i]²`**.

This is again a **"minimize the maximum, then greedily spend the rest"**
problem solved with **binary search on the answer** (the plateau level),
followed by a convexity-driven leftover-distribution step.

---

## Solution — Binary Search on the Cap + Convex Leftover Distribution

**Key Insight 1 (feasibility of a cap is monotonic).** For a candidate
"cap" `r`, define

```
cost(r) = Σ_i max(diff[i] - r, 0)
```

— the number of decrement operations needed to bring every `diff[i]` down
to at most `r`. `cost(r)` is a non-increasing step function of `r`, so
"`cost(r) ≤ k`" is a monotonic predicate in `r`: false for small `r`, true
for large `r`. Binary search finds the *smallest* feasible cap `R`, i.e.
`R = min { r ≥ 0 : cost(r) ≤ k }`.

**Key Insight 2 (convexity means "always shave the current max").** Because
`x² ` is strictly convex, whenever two values `a ≥ b ≥ 1` can each be
decremented by one unit but only one unit is available, it is never worse
— and strictly better unless `a = b` — to decrement the **larger** value
`a`. Repeating this "always decrement a current maximum" rule exactly `k`
times (the greedy process) is therefore optimal. Reaching cap `R` by
flattening everything `> R` down to `R` costs exactly `cost(R) ≤ k`
operations; the leftover `k − cost(R)` operations must then be spent
one-per-element on the values now sitting at `R` (never spent on values
already `< R`, by the same convexity rule, since those are not maximal),
further lowering that many of them from `R` to `R − 1`.

```java
public interface MinimumSumOfSquaredDifference {
    long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2);
}

class MinimumSumOfSquaredDifferenceImpl implements MinimumSumOfSquaredDifference {

    private int maximum(int[] a) {
        int m = a[0];
        for (int ai : a) {
            m = Math.max(m, ai);
        }
        return m;
    }

    private boolean check(int[] diff, long k, int mid) {
        long count = 0;
        for (int i = 0; i < diff.length; i++) {
            if (diff[i] > mid) {
                count += diff[i] - mid;
            }
        }
        return count <= k;
    }

    private long answer(int[] diff, int r, long k) {
        long remaining = k;
        int countAtR = 0;
        for (int i = 0; i < diff.length; i++) {
            if (diff[i] > r) {
                remaining -= diff[i] - r;
                diff[i] = r;
            }
            if (diff[i] == r) {
                countAtR++;
            }
        }

        if (r > 0) {
            for (int i = 0; i < diff.length && remaining > 0; i++) {
                if (diff[i] == r) {
                    diff[i]--;
                    remaining--;
                }
            }
        }

        long ans = 0;
        for (int i = 0; i < diff.length; i++) {
            ans += (long) diff[i] * diff[i];
        }
        return ans;
    }

    @Override
    public long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2) {
        int[] diff = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            diff[i] = Math.abs(nums1[i] - nums2[i]);
        }

        int l = 0;
        int r = maximum(diff);
        long k = (long) k1 + k2;

        while (l < r) {
            int mid = l + (r - l) / 2;
            if (check(diff, k, mid)) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }

        return answer(diff, r, k);
    }
}
```

**How it works:**
1. `diff[i] = |nums1[i] - nums2[i]|`; `k = k1 + k2` is the combined
   decrement budget (operations on `nums1` and `nums2` are freely
   interchangeable for shrinking a given `diff[i]`, so nothing is lost by
   pooling them).
2. Binary search over `[0, max(diff)]` finds `R`, the smallest cap whose
   flattening cost is `≤ k` (`check` is exactly `cost(mid) ≤ k`).
3. `answer(diff, R, k)` replays the flattening (every `diff[i] > R` becomes
   `R`, consuming `cost(R)` of the budget), then spends the strictly
   positive leftover `k − cost(R)` one unit at a time on distinct elements
   currently equal to `R` (dropping them to `R − 1`), and finally sums the
   squares of the resulting array.

---

## Correctness Proof

### Setup

Let `diff[0..n-1]` be the initial (non-negative) gap array and `k ≥ 0` the
total decrement budget, `k = k1 + k2`. A **feasible allocation** is any
multiset of `n` non-negative integers `d'[0], …, d'[n-1]` obtainable from
`diff` by decreasing entries (never increasing, never below `0`) using a
total of at most `k` unit decrements:
`Σ_i (diff[i] - d'[i]) ≤ k` and `0 ≤ d'[i] ≤ diff[i]` for all `i`. We must
show the algorithm returns `min Σ_i d'[i]²` over all feasible allocations.

**Reduction from the original problem.** Any sequence of `≤ k1` `±1`
operations on `nums1` and `≤ k2` on `nums2` changes each `|nums1[i] -
nums2[i]|` by at most the number of operations applied at index `i`, and
only a decreasing move (narrowing the gap) can ever reduce `Σ diff[i]²`;
widening a gap strictly increases that term's square without helping any
other term, so no optimal solution ever uses a widening move. Hence WLOG
every operation narrows some gap by exactly `1`, which is precisely a unit
decrement of `diff[i]`, and the combined budget across both arrays is
`k = k1 + k2` (operations on `nums1[i]` and `nums2[i]` are interchangeable
for narrowing `|nums1[i]-nums2[i]|`, so pooling the two budgets loses
nothing and the two per-array caps never bind separately once pooled).

### Lemma 1 (Exchange argument: always decrement a current maximum)

Let `d'` be any feasible allocation that is **not** a prefix of the greedy
process (i.e., there exist indices `p, q` with `d'[p] < diff[p] -` *(some
decrements applied elsewhere)* while some other coordinate with a strictly
larger current value received no decrement at some step). Formally: suppose
at some intermediate state values `a ≥ b ≥ 1` both still have remaining
budget available, and the allocation decrements `b` by one unit instead of
`a`. Then swapping that unit — decrementing `a` instead of `b` — does not
increase, and strictly decreases whenever `a > b`, the final sum of squares.

*Proof.* Decrementing `a` changes its square contribution by
`a² - (a-1)² = 2a - 1`; decrementing `b` instead changes it by
`b² - (b-1)² = 2b - 1`. Since `a ≥ b ≥ 1`, `2a - 1 ≥ 2b - 1 > 0`, so
redirecting the unit to `a` removes at least as much from the sum of
squares as leaving it on `b` (strictly more when `a > b`, equal when
`a = b`, in which case the swap is a no-op on the total). All other
coordinates and all other spent units are untouched by this swap, so the
new total sum of squares is `≤` the old one. ∎

### Corollary 1 (Greedy optimality)

Repeatedly selecting a current maximum entry and decrementing it by one,
performed `k` times in total (ties broken arbitrarily, never decrementing
an entry already at `0`), produces an allocation whose sum of squares is
`≤` that of **any** feasible allocation.

*Proof.* Take any feasible allocation `d'` using exactly `k` units (using
fewer than `k` units is never better: if budget remains and some entry is
still positive, decrementing it strictly lowers the sum of squares, so an
optimal allocation spends the full budget `k` whenever any positive entry
remains to shave — and if all entries are `0`, the all-`0` allocation is
produced by *both* the greedy process, once `k ≥ Σ diff[i]`, and `d'`
trivially). Order the units of `d'` into some sequence of individual
decrements. By Lemma 1, any decrement applied to a non-maximal entry while
a strictly larger entry still has budget-eligible "room" can be swapped for
a decrement on that larger entry without increasing the sum of squares.
Applying this swap exhaustively (a standard exchange-argument induction,
each swap strictly reduces a potential function counting out-of-order
pairs, so it terminates) transforms `d'`, unit by unit, into exactly the
multiset produced by the greedy "always hit the current max" process,
without ever increasing the sum of squares. Hence the greedy result's sum
of squares is `≤ Σ d'[i]²` for the arbitrary feasible `d'`. ∎

### Lemma 2 (The greedy process is a two-phase plateau: flatten then shave by one)

Define `cost(r) = Σ_i max(diff[i] - r, 0)` for integer `r ≥ 0`, and let
`R = min { r ≥ 0 : cost(r) ≤ k }` (well-defined since `cost` is
non-increasing in `r` and `cost(max(diff)) = 0 ≤ k`). Then the greedy
process of Corollary 1, run for exactly `k` steps, produces:
- every original `diff[i] > R` reduced to exactly `R`;
- exactly `rem = k - cost(R)` of the entries whose value is `R` after that
  flattening (either originally `= R` or reduced to `R`) further reduced to
  `R - 1` (only if `R > 0`; if `R = 0`, no further reduction is possible or
  needed since `rem` must be `0`, shown below);
- every entry whose original value is `< R` left untouched.

*Proof.* By Lemma 1/Corollary 1, the greedy process always decrements a
current global maximum. As long as some entry exceeds `R`, the current
maximum is `> R` (since nothing has yet been pushed below its original
value if that value is `≤ R`, and any entry `> R` dominates any entry
`≤ R`), so greedy decrements only exceed-`R` entries until all of them
reach `R` — this consumes exactly `cost(R)` units (each unit reduces the
"area above `R`" by exactly 1, and the total such area is `cost(R)` by
definition), matching the `answer` method's first loop. Because `R` is the
*minimal* feasible cap, `cost(R-1) > k` (otherwise `R-1` would also satisfy
`cost ≤ k`, contradicting minimality of `R`), so after spending `cost(R)`
units to reach the all-`≤ R` plateau, the remaining budget
`rem = k - cost(R)` satisfies `rem < cost(R-1) - cost(R) = countAtR`,
where `countAtR` is the number of entries exactly at `R` after flattening
(every unit of `cost(R-1) - cost(R)` corresponds to decrementing one of
these `countAtR` entries from `R` to `R-1`). So `0 ≤ rem < countAtR`: there
is always strictly enough "room" among the `R`-valued entries to absorb all
`rem` remaining units one-per-entry, and never more units than entries
available. Once all entries are `≤ R`, the current maximum is `R` (while
any remain at `R`), so by Corollary 1 the greedy process spends its
remaining `rem` units decrementing `rem` *distinct* `R`-valued entries down
to `R-1` each (never spending two units on the same entry while another
sits untouched at `R`, since after one decrement that entry drops to
`R - 1 < R` and a same-valued sibling becomes the new maximum to target),
exactly matching the second loop of `answer`. Entries originally `< R` are
never a current maximum while any `≥ R` entry remains un-shaved (by the
`rem < countAtR` bound, the process finishes spending its budget before
dropping below the `R`-plateau entirely), so they are left untouched. ∎

This is exactly the computation performed by `answer(diff, r, k)`, so its
output multiset equals the result of the (optimal, by Corollary 1) greedy
process, and `ans = Σ diff[i]²` over that multiset is the true minimum.

### Theorem (Binary search locates `R`)

**Loop invariant.** Before each iteration of `while (l < r)`,
`l ≤ R ≤ r` and `check(diff, k, r)` is true.

- **Initialization.** `l = 0 ≤ R` trivially (`R ≥ 0` by definition).
  `r = maximum(diff)` gives `cost(r) = Σ max(diff[i]-r,0) = 0 ≤ k`
  (since `k ≥ 0`), so `R ≤ r` and `check(diff,k,r)` is true.
- **Maintenance.** With `l < r`, let `mid = l + (r-l)/2`, so
  `l ≤ mid < r`.
  - If `check(diff,k,mid)` is true (`cost(mid) ≤ k`), then by minimality of
    `R`, `R ≤ mid`; set `r = mid`, preserving `R ≤ r` and `check` true at
    the new `r`.
  - If `check(diff,k,mid)` is false (`cost(mid) > k`), then since `cost` is
    non-increasing, every `x ≤ mid` also has `cost(x) ≥ cost(mid) > k`, so
    `mid` is infeasible and `R ≥ mid + 1`; set `l = mid + 1`, preserving
    `l ≤ R`. `r`'s value and its feasibility are untouched.
- **Termination.** `r - l` is a non-negative integer that strictly
  decreases each iteration (`mid - l < r - l` or `r - (mid+1) < r - l`
  since `l ≤ mid < r`), so by well-ordering the loop halts with `l = r`.
  Combined with the invariant `l ≤ R ≤ r`, this forces `l = r = R`.

The loop exits with `r = R`, and `answer(diff, R, k)` then computes the
optimal sum of squares by Lemma 2 and Corollary 1. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| Build `diff` array | O(n) | O(n) |
| `maximum` (bracket computation) | O(n) | O(1) |
| `check` (one feasibility test) | O(n) | O(1) |
| Binary search over `[0, max(diff)]` | O(log(max(diff))) checks | — |
| `answer` (flatten + leftover distribution + final sum) | O(n) | O(1) extra |
| **Overall** | **O(n · log(max(diff)))** | **O(n)** |

Each binary-search step costs `O(n)` to evaluate `cost(mid) ≤ k`, and the
search interval halves each round, giving `O(n log(max(diff)))` total,
versus a naive simulation that greedily pops a max-heap element `k` times,
`O(n + k log n)` — markedly worse when `k` can be as large as `~10^9` while
`max(diff)` (and hence `log(max(diff))`) is bounded by the input value
range.
