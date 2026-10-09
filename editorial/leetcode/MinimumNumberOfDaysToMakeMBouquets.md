# Minimum Number of Days to Make m Bouquets

## Problem Statement

You are given an integer array `bloomDay` (`bloomDay[i]` is the day on which
the `i`-th flower blooms), and integers `m` and `k`. You want to make `m`
bouquets, each bouquet using exactly `k` **adjacent** flowers that have
**already bloomed** (bloomed flowers may be reused across different
candidate "days", but each flower is consumed by at most one bouquet on a
given day's optimal packing). Return the **minimum number of days** to wait
so that `m` bouquets can be made; return `-1` if it is never possible.

**Example:**
```
bloomDay = [1,10,3,10,2], m = 3, k = 1
Day 3: bloomed = [1,_,3,_,2] → flowers at indices 0,2,4 have bloomed
       → 3 bouquets of size 1 can be made → answer = 3

bloomDay = [1,10,3,10,2], m = 3, k = 2
Need 3 * 2 = 6 flowers but only 5 exist → answer = -1

bloomDay = [7,7,7,7,12,7,7], m = 2, k = 3
Day 12: all bloomed → runs: [7,7,7,7] (len 4) and [7,7] (len 2) around index 4
        → floor(4/3) + floor(2/3) = 1 + 0 = 1 bouquet, not enough with day<12
Day 12 is required to bloom index 4, giving one run of length 7
        → floor(7/3) = 2 bouquets → answer = 12
```

"Can we make at least `m` bouquets by day `d`?" is **monotonic** in `d`
(false for small `d`, true for large `d`, and once true stays true as `d`
grows), so the smallest feasible `d` can be found with **binary search on
the answer**, using the feasibility check as the predicate.

---

## Solution — Binary Search on the Answer + Run-Length Feasibility Check

**Key Insight.** Define `canMake(d) = true` iff, considering only flowers
with `bloomDay[i] ≤ d` as "available", `bloomDay` contains enough
**adjacency** to form `≥ m` bouquets of `k` consecutive available flowers.

1. Partition the array (conceptually) into maximal runs of consecutive
   indices that are all available on day `d`. Within a run of length `len`,
   the greedy tiling into consecutive non-overlapping blocks of size `k`
   yields exactly `⌊len / k⌋` bouquets — and this is **optimal** for that run
   (Lemma 1).
2. `canMake(d)` is **monotonic**: raising `d` can only turn unavailable
   flowers into available ones, which can only merge/extend runs, never
   shrink them, so the total bouquet count is non-decreasing (Lemma 2).
3. The answer always lies in `[1, max(bloomDay)]`: day `1` is the smallest
   meaningful day, and by `max(bloomDay)` every flower has bloomed, which is
   the best any day can ever do.
4. A quick necessary condition `m * k > bloomDay.length` immediately rules
   out impossible instances (not enough flowers even if all bloom at once).

Binary search then finds the smallest `d` in `[1, max(bloomDay)]` for which
`canMake(d)` holds.

```java
private boolean check(int[] bloomDay, int m, int k, int mid) {
    int bouquetCount = 0;
    int l = 0;
    int r = 0;

    while (r < bloomDay.length) {
        if (bloomDay[r] <= mid) {
            r++;
            if (r - l == k) {
                bouquetCount++;
                l = r;
            }
        } else {
            r++;
            l = r;
        }
    }
    return bouquetCount >= m;
}

@Override
public int minDays(int[] bloomDay, int m, int k) {
    if ((long) m * k > bloomDay.length) return -1;

    int l = 1;
    int r = max(bloomDay);

    while (l < r) {
        int mid = l + (r - l) / 2;
        if (check(bloomDay, m, k, mid)) {
            r = mid;
        } else {
            l = mid + 1;
        }
    }
    return r;
}
```

**How it works:**
1. The `m * k > bloomDay.length` guard rejects instances where even a
   single giant run of *all* flowers couldn't supply `m` bouquets of size
   `k` — these would otherwise cause the binary search to converge to
   `max(bloomDay)` without ever satisfying `check`, so we short-circuit.
2. `check(mid)` slides a window `[l, r)` left to right:
   - While the current flower `bloomDay[r] ≤ mid` (available), extend the
     window; the instant the window reaches exactly `k` available flowers,
     "cut" a bouquet and restart the window right after it (`l = r`). This
     greedily tiles every maximal available run into size-`k` blocks,
     left-aligned, dropping only the leftover remainder (`< k` flowers).
   - The moment an unavailable flower is seen, the run is broken: both `l`
     and `r` jump past it, discarding any partial (`< k`) window — a
     partial window can never become a bouquet since it is not adjacent to
     any more available flowers on its right within this run.
3. `l = 1`, `r = max(bloomDay)` bracket the true answer (Lemma 3 below).
   Each binary-search iteration tests `mid` as a candidate day:
   - If `check(mid)` is true, `mid` is a feasible day, so the answer is
     `≤ mid`; shrink `r = mid`.
   - Otherwise the answer must be `> mid`; raise `l = mid + 1`.
4. The loop narrows `[l, r]` until `l == r`, which is exactly the smallest
   feasible day.

---

## Correctness Proof

### Lemma 1 (Greedy left-aligned tiling is exact per run)

Fix day `d` and consider a maximal run of `len` **consecutive** indices all
available on day `d` (i.e., `bloomDay[i] ≤ d`), bounded on both sides by
either the array boundary or an unavailable flower. The maximum number of
disjoint, adjacent, size-`k` bouquets that can be formed **within this run**
is `⌊len / k⌋`, and the greedy left-aligned scan (`check`'s window logic
restricted to this run) achieves exactly `⌊len / k⌋`.

*Proof.* Any bouquet uses `k` adjacent available flowers, and distinct
bouquets use disjoint flowers (no flower is reused). Within a run of `len`
consecutive available positions, any packing of disjoint, adjacent,
size-`k` blocks uses at most `len` positions total, so the number of blocks
is at most `⌊len / k⌋` — this is the trivial upper bound. The greedy scan
cuts a bouquet every time it accumulates exactly `k` consecutive available
flowers and immediately restarts counting from the next position; applied
to a contiguous run of length `len`, this produces blocks of size `k` back
to back until fewer than `k` positions remain (which cannot form another
bouquet). The number of full blocks produced is exactly `⌊len / k⌋`,
matching the upper bound. Hence the greedy count is optimal for this run. ∎

Summing Lemma 1 over all maximal runs induced by day `d` shows that
`check`'s total `bouquetCount` equals `Σ ⌊len_i / k⌋`, the true maximum
number of bouquets obtainable by day `d` — i.e., `check(mid)` is an *exact*
feasibility test, not an approximation.

### Lemma 2 (Monotonicity of feasibility)

Let `f(d)` = `Σ ⌊len_i(d) / k⌋` be the true maximum bouquet count
achievable by day `d` (as computed by `check`). If `d₁ < d₂`, then
`f(d₁) ≤ f(d₂)`.

*Proof.* Every flower available on day `d₁` (`bloomDay[i] ≤ d₁`) is also
available on day `d₂`, since `d₁ < d₂` implies `bloomDay[i] ≤ d₁ < d₂`. So
the set of available indices only grows as `d` increases, and the
collection of maximal runs on day `d₂` is obtained from the runs on day
`d₁` only by (a) extending existing runs and/or (b) merging adjacent runs
that were previously separated by a now-bloomed flower — never by shrinking
or splitting a run. Since `⌊len / k⌋` is non-decreasing in `len`, and
merging two runs of lengths `a, b` into one run of length `a + b` yields
`⌊(a+b)/k⌋ ≥ ⌊a/k⌋ + ⌊b/k⌋`, every such transformation can only keep the
same or increase the total bouquet count. Hence `f(d₁) ≤ f(d₂)`. ∎

Consequently, `check(bloomDay, m, k, d) = (f(d) ≥ m)` is a **monotonic**
predicate in `d`: false for all `d` below some threshold `ANSWER`, true for
all `d ≥ ANSWER`, where `ANSWER = min { d : f(d) ≥ m }`.

### Lemma 3 (The search bracket is valid and contains the answer, when one exists)

If `m * k ≤ bloomDay.length`, then `1 ≤ ANSWER ≤ max(bloomDay)`.

*Proof.*
- *Lower bound:* `d = 1` is the smallest day under consideration (days are
  positive integers), so trivially `ANSWER ≥ 1` whenever an answer exists.
- *Upper bound:* at `d = max(bloomDay)`, every flower satisfies
  `bloomDay[i] ≤ d`, so the entire array is one maximal run of length `n`.
  By Lemma 1, `f(max(bloomDay)) = ⌊n / k⌋`. Since `m * k ≤ n` is given,
  `m ≤ n / k`, and as `m` is an integer, `m ≤ ⌊n / k⌋ = f(max(bloomDay))`.
  Hence `check(max(bloomDay))` is true, so `ANSWER ≤ max(bloomDay)`. ∎

The guard `if ((long) m * k > bloomDay.length) return -1;` at the top of
`minDays` exactly captures the complementary case where no `d` can ever
satisfy `f(d) ≥ m` (since `f(d) ≤ ⌊n / k⌋ < m` for every `d`, by the same
run-length upper bound used above), so the binary search is only ever run
when Lemma 3's bracket is guaranteed valid.

### Theorem (Binary search finds `ANSWER`)

**Loop invariant.** Before each iteration of the `while (l < r)` loop,
`l ≤ ANSWER ≤ r` and `check(bloomDay, m, k, r)` is true.

- **Initialization.** Before the first iteration, `l = 1` and
  `r = max(bloomDay)`. By Lemma 3 (applicable since the `-1` guard already
  excluded the infeasible case), `1 ≤ ANSWER ≤ max(bloomDay)`, so
  `l ≤ ANSWER ≤ r`. The same lemma's proof shows `check(max(bloomDay))` is
  true, establishing the invariant's second clause.

- **Maintenance.** Assume `l ≤ ANSWER ≤ r` and `check(r)` is true before an
  iteration with `l < r`. Let `mid = l + (r - l) / 2`; since `l < r`,
  `l ≤ mid < r`.
  - If `check(mid)` is true, then by Lemma 2 (monotonicity) `mid ≥ ANSWER`
    (feasibility at `mid` means the threshold has already been reached), so
    setting `r = mid` keeps `ANSWER ≤ r` and preserves "`check` true at
    `r`".
  - If `check(mid)` is false, then `mid < ANSWER` (by definition of
    `ANSWER` as the smallest feasible day, together with monotonicity), so
    `ANSWER ≥ mid + 1`; setting `l = mid + 1` keeps `l ≤ ANSWER`. The `r`
    bound and its feasibility are untouched, so they still hold.

  In both branches, `l ≤ ANSWER ≤ r` continues to hold after the update.

- **Termination.** Each iteration strictly shrinks `r - l`: when `r = mid`,
  the new width is `mid - l < r - l` (since `mid < r`); when `l = mid + 1`,
  the new width is `r - mid - 1 < r - l` (since `mid ≥ l`). As `r - l` is a
  non-negative integer that strictly decreases every iteration, the loop
  terminates after finitely many steps, at which point `l = r`. Since the
  invariant `l ≤ ANSWER ≤ r` holds at all times, `l = r` forces
  `l = r = ANSWER`.

The method returns `r`, which by the above equals `ANSWER`, the minimum
number of days needed to make `m` bouquets — or `-1`, correctly returned
up front whenever Lemma 3's precondition `m * k ≤ bloomDay.length` fails,
which is exactly when no finite `ANSWER` exists. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `max(bloomDay)` (bracket computation) | O(n) | O(1) |
| `check` (one feasibility scan) | O(n) | O(1) |
| Binary search over `[1, max(bloomDay)]` | O(log(max(bloomDay))) checks | — |
| **Overall** | **O(n · log(max(bloomDay)))** | **O(1)** |

> Each binary-search step costs O(n) for the sliding-window feasibility
> scan, and the search space shrinks geometrically, giving the final
> `O(n log(max(bloomDay)))` bound — far better than simulating every
> possible day one at a time, which is `O(n · max(bloomDay))` in the worst
> case.
