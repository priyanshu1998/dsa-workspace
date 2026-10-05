# Koko Eating Bananas

## Problem Statement

Koko loves bananas. There are `n` piles of bananas, the `i`-th pile has `piles[i]` bananas. The guards have gone and will come back in `h` hours.

Koko can decide her bananas-per-hour eating speed `k`. Each hour, she chooses some pile and eats `k` bananas from it. If the pile has fewer than `k` bananas, she eats all of them instead and does not eat any more bananas during that hour (she moves to a new pile, if any, in the next hour — a pile is never shared across hours).

Return the **minimum** integer `k` such that she can eat all the bananas within `h` hours.

**Example:**
```
piles = [3,6,7,11], h = 8   → 4
piles = [30,11,23,4,20], h = 5  → 30
piles = [30,11,23,4,20], h = 6  → 23
```

---

## Solutions

### 1. Naive Solution — O(n · max(piles)) Time, O(1) Space

Try every candidate speed `k` starting from `1` upward. For each `k`, compute the total hours needed (`⌈pile / k⌉` per pile, summed). The first `k` for which the total hours is `<= h` is the answer, since hours needed is monotonically non-increasing as `k` grows.

```java
int minEatingSpeed(int[] piles, int h) {
    int maxPile = Arrays.stream(piles).max().getAsInt();
    for (int k = 1; k <= maxPile; k++) {
        if (isEnoughSpeed(piles, k, h)) {
            return k;
        }
    }
    return maxPile;
}
```

**Complexity:**
Time: O(n · maxPile) — up to `maxPile` candidate speeds tried, each costing O(n) to evaluate.
Space: O(1)

**Drawback:** Ignores the fact that "hours needed" is monotonic in `k` — a property that enables binary search instead of linear scanning over every candidate speed.

---

### 2. Optimized Solution (Binary Search on Answer) — O(n · log(max(piles))) Time, O(1) Space

**Key Insight:** Define `f(k)` = total hours needed to finish all piles at speed `k` = `Σ ⌈piles[i] / k⌉`. As `k` increases, `f(k)` is monotonically non-increasing (a faster speed never needs more hours). We want the smallest `k` with `f(k) <= h`. This is the classic "search for the boundary of a monotonic predicate" pattern — binary search the answer space `[1, max(piles)]` instead of the input array.

```java
class KokoEatingBananasImpl implements KokoEatingBananas {

    private boolean isEnoughSpeed(int[] piles, int k, int h) {
        int iteration = 0;
        for (int pile : piles) {
            iteration += (pile + k - 1) / k;   // ceil(pile / k)
        }
        return iteration <= h;
    }

    @Override
    public int minEatingSpeed(int[] piles, int h) {
        int l = 1;
        int r = 1_000_000_000;

        while (l < r) {
            int mid = l + (r - l) / 2;

            if (isEnoughSpeed(piles, mid, h)) {
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
1. The search space is not the array — it is the set of possible eating speeds `[1, 10^9]` (an upper bound safely above `max(piles)`, since any speed `>= max(piles)` finishes every pile in a single hour).
2. `isEnoughSpeed(piles, k, h)` evaluates the monotone predicate `f(k) <= h` in O(n) using `⌈pile / k⌉ = (pile + k - 1) / k` (integer ceiling division).
3. Binary search converges on the **smallest** `k` for which the predicate is `true`: if `mid` is enough (`r = mid`, `mid` could be the answer, never discard it), otherwise `mid` is proven too slow (`l = mid + 1`, safely discard it).
4. The loop ends when `l == r`, which is exactly the boundary — the first `k` where the predicate flips from `false` to `true`.

**Complexity:**
Time: O(n · log(maxPile)) — O(log maxPile) iterations, each doing an O(n) feasibility check.
Space: O(1)

---

## Formal Proof of Correctness

**Setup.** Let `piles` be an array of `n` positive integers and `h >= n` be the hour budget (at least one hour per pile is required for feasibility, which the problem guarantees). Define, for any integer speed `k >= 1`:

```
f(k) = Σ_{i=0}^{n-1} ⌈ piles[i] / k ⌉
```

`f(k)` is exactly the number of hours Koko needs to finish all piles at speed `k`. Let `k*` be the smallest positive integer such that `f(k*) <= h` (this exists because `f(maxPile) = n <= h`). We must show the algorithm returns `k*`.

**Lemma (Monotonicity).** For all `k >= 1`, `f(k + 1) <= f(k)`.
*Proof.* For a fixed pile size `p`, `⌈p / (k+1)⌉ <= ⌈p / k⌉`, since `p / (k+1) <= p / k` and the ceiling function is non-decreasing in its argument. Summing this per-pile inequality over all piles gives `f(k+1) <= f(k)`. ∎

This monotonicity means the predicate `isEnoughSpeed(piles, k, h) ≡ (f(k) <= h)` is **monotone**: once `true` for some `k`, it remains `true` for every `k' > k`. So the set of valid speeds `{k : f(k) <= h}` is exactly the suffix `[k*, ∞)`, and binary search for the left boundary of this suffix is well-defined.

**Loop Invariant.** At the start (and end) of every iteration of the `while (l < r)` loop:
> `1 <= l <= k* <= r <= 10^9`

i.e., the window `[l, r]` always contains `k*`.

**Initialization.** Before the first iteration, `l = 1` and `r = 10^9`. Since `k* <= maxPile <= 10^9` (speed `maxPile` always finishes every pile in one hour, i.e., `f(maxPile) = n <= h`) and `k* >= 1` by definition, the invariant `1 <= l <= k* <= r <= 10^9` holds.

**Maintenance.** Assume the invariant holds at the start of an iteration with `l < r` (so the loop body executes). Let `mid = l + (r - l) / 2`, so `l <= mid < r`.

*Case A: `isEnoughSpeed(piles, mid, h)` is `true`, i.e., `f(mid) <= h`.*
By minimality of `k*`, `f(k) <= h` for `k = mid` implies `mid >= k*` (since `k*` is the *smallest* such `k`). Combined with the invariant `l <= k* <= r`, and now `k* <= mid`, we get `l <= k* <= mid`. Updating `r = mid` preserves `l <= k* <= r`.

*Case B: `isEnoughSpeed(piles, mid, h)` is `false`, i.e., `f(mid) > h`.*
By definition of `k*` as the smallest feasible speed, `mid` infeasible implies `mid < k*` (any `k < k*` must be infeasible, by minimality of `k*`; contrapositive: infeasible `mid` means `mid` cannot be `>= k*`, since `k*` and everything above it is feasible by monotonicity). So `mid < k*`, i.e., `k* >= mid + 1`. Combined with the invariant `k* <= r`, updating `l = mid + 1` preserves `l <= k* <= r`.

In both cases the invariant holds after the update, and the window strictly shrinks: either `r` moves down to `mid < r`, or `l` moves up to `mid + 1 > l` — either way, `r - l` strictly decreases.

**Termination (decreasing variant).** Let the measure be `g = r - l >= 0`. Each iteration strictly decreases `g` (shown above), and `g` is a non-negative integer bounded below by `0` (the loop condition `l < r` fails exactly when `g = 0`). By well-foundedness of the non-negative integers under `<`, the loop terminates after finitely many iterations — specifically after at most `⌈log2(10^9)⌉ ≈ 30` iterations, since the window at least halves each time (`mid` always splits `[l, r]` so that both the new `[l, mid]` and `[mid+1, r]` are no larger than `⌈(r-l+1)/2⌉`).

**Conclusion.** At termination, `l == r`. By the invariant `l <= k* <= r`, this forces `l = r = k*`. The algorithm returns `r`, which equals `k*` — the minimum eating speed satisfying `f(k*) <= h`. ∎

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Try every speed k = 1, 2, 3, ... until f(k) <= h.
       Time: O(n · maxPile)    Space: O(1)

           ▼  Observation: f(k) = Σ ceil(pile/k) is monotonically
              non-increasing in k, so the set of feasible speeds is a
              contiguous suffix [k*, ∞) — the classic signature for
              binary-searching the answer space instead of scanning it.

Binary Search on Answer ─────────────────────────────────────────────►
       Binary search k in [1, maxPile]; feasibility check costs O(n).
       r = mid when mid is feasible (could be the answer, never discard),
       l = mid + 1 when mid is infeasible (proven too slow, safely discard).
       Time: O(n · log maxPile)    Space: O(1)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n · maxPile) | O(1) | Scans every candidate speed linearly |
| Binary Search on Answer | O(n · log maxPile) | O(1) | Exploits monotonicity of hours-needed in speed |

> **Best solution:** `KokoEatingBananasImpl` (Binary Search on Answer) — O(n · log maxPile) time with O(1) space, exponentially faster than the naive linear scan over candidate speeds.
