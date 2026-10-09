# Minimum Time to Complete Trips

## Problem Statement

You are given an array `time`, where `time[i]` denotes the time taken by the
`i`-th bus to complete **one trip**. Each bus can make multiple trips
**consecutively**, i.e. the `(i+1)`-th trip of a bus can start immediately
after the `i`-th trip of the same bus is completed. All buses operate
**simultaneously and independently**. Return the **minimum time** required
for all buses together to complete **at least** `totalTrips` trips.

**Example:**
```
time = [1, 2, 3], totalTrips = 5
At t = 3: bus0 made 3 trips, bus1 made 1 trip, bus2 made 1 trip → total 5
Answer = 3

time = [2], totalTrips = 1
Answer = 2
```

As the duration `t` we check increases, the number of trips completed by
time `t` never decreases — it is **monotonic**. Whenever "can `totalTrips`
trips be completed within time `t`?" is a monotonic predicate in `t`, the
smallest feasible `t` can be found with **binary search on the answer**.

---

## Solution — Binary Search on the Answer

**Key Insight.** For a fixed duration `t`, bus `i` completes exactly
`t / time[i]` (integer division) trips by time `t`, because it finishes a
trip the instant a multiple of `time[i]` elapses. The total number of trips
completed by all buses by time `t` is therefore:

```
completed(t) = Σ (t / time[i])
```

`completed(t)` is **non-decreasing** in `t`, so `check(t) = completed(t) ≥
totalTrips` is a monotonic predicate (false for small `t`, true for large
`t`, and once true stays true). Binary search finds the smallest such `t`.

```java
public interface MinimumTimeToCompleteTrips {
    long minimumTime(int[] time, int totalTrips);
}

class MinimumTimeToCompleteTripsImpl implements MinimumTimeToCompleteTrips {

    private int min(int[] time){
        int M = time[0];
        for(int t: time){
            M = Math.min(t, M);
        }
        return M;
    }

    private boolean check(int []time, int totalTrips, long mid){
        long count = 0;

        for(int i=0; i<time.length; i++){
            count += (mid/time[i]);
        }
        return count >= totalTrips;
    }


    @Override
    public long minimumTime(int[] time, int totalTrips) {
        long l = min(time);
        long r = 100_000_000_000_000L;

        while(l < r){
            var mid = l + (r-l)/2;
            if(check(time, totalTrips, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}
```

**How it works:**
1. `l = min(time)` is the earliest instant at which **any** trip can finish
   (the fastest bus's first trip), and is a safe lower bound: no shorter
   duration can complete even one trip.
2. `r = 100_000_000_000_000L` (`10^14`) is a safe, generously large upper
   bound — with `time[i], totalTrips ≤ 10^7`, the true answer never exceeds
   `time[i] * totalTrips ≤ 10^14`.
3. Each iteration tests midpoint `mid`:
   - If `check(time, totalTrips, mid)` is true, `mid` trips-complete the
     requirement, so the answer is `≤ mid`; shrink `r = mid`.
   - Otherwise `mid` is too small; raise `l = mid + 1`.
4. The loop narrows `[l, r]` to a single point, the minimum feasible time.

---

## Correctness Proof

### Lemma 1 (`completed(t)` is exact and monotonic)

For `t ≥ 0`, define `completed(t) = Σᵢ ⌊t / time[i]⌋`, the total trips
finished by all buses by time `t`. Then:

(a) `completed(t)` **exactly** counts the trips completed by time `t`.
(b) `completed(t)` is **non-decreasing** in `t`.

*Proof.*
(a) Bus `i` finishes its `k`-th trip at time `k · time[i]`. The number of
completed trips for bus `i` by time `t` is the largest `k` with
`k · time[i] ≤ t`, i.e. `k = ⌊t / time[i]⌋`. Summing over all independent
buses gives the total `completed(t)`.

(b) For `t₁ ≤ t₂` and any fixed `time[i] > 0`, `⌊t₁ / time[i]⌋ ≤
⌊t₂ / time[i]⌋` (floor division is non-decreasing in its numerator). Summing
non-decreasing terms over `i` preserves non-decreasingness, so
`completed(t₁) ≤ completed(t₂)`. ∎

### Lemma 2 (Monotonicity of feasibility / `check`)

`check(t) := completed(t) ≥ totalTrips` is monotonic: if `check(t₁)` is true
and `t₁ ≤ t₂`, then `check(t₂)` is true.

*Proof.* By Lemma 1(b), `completed(t₂) ≥ completed(t₁) ≥ totalTrips`, so
`check(t₂)` holds. ∎

This guarantees a well-defined threshold
`ANSWER = min { t ≥ 0 : check(t) is true }` — exactly the value
`minimumTime` must return — with `check` false for all `t < ANSWER` and true
for all `t ≥ ANSWER`.

### Lemma 3 (The search bracket `[min(time), 10^14]` contains `ANSWER`)

`min(time) ≤ ANSWER ≤ 100_000_000_000_000`.

*Proof.*
- *Lower bound:* for `t < min(time)`, every bus has `⌊t / time[i]⌋ = 0`
  (since `t` is smaller than even the fastest bus's single trip time), so
  `completed(t) = 0 < totalTrips` (as `totalTrips ≥ 1`). Hence `check(t)` is
  false for all `t < min(time)`, so `ANSWER ≥ min(time)`.
- *Upper bound:* let `fastest = min(time)`. At `t = fastest · totalTrips`,
  the fastest bus alone completes `⌊(fastest · totalTrips) / fastest⌋ =
  totalTrips` trips, so `completed(t) ≥ totalTrips`, i.e. `check(t)` is true.
  Under the problem's constraints (`time[i] ≤ 10^7`, `totalTrips ≤ 10^7`),
  `fastest · totalTrips ≤ 10^14`, so `ANSWER ≤ fastest · totalTrips ≤
  100_000_000_000_000`. ∎

### Theorem (Binary search finds `ANSWER`)

**Loop invariant.** Before each iteration of `while (l < r)`,
`l ≤ ANSWER ≤ r` and `check(r)` is true.

- **Initialization.** `l = min(time)`, `r = 10^14`. By Lemma 3,
  `l ≤ ANSWER ≤ r`. By the upper-bound argument in Lemma 3, `check(r)` is
  true since `r` is at least as large as the witnessed feasible duration.

- **Maintenance.** Assume `l ≤ ANSWER ≤ r` and `check(r)` true, with
  `l < r`. Let `mid = l + (r - l)/2`, so `l ≤ mid < r`.
  - If `check(mid)` is true, then by definition of `ANSWER` as the minimal
    feasible time, `mid ≥ ANSWER`; set `r = mid`. This keeps `ANSWER ≤ r`
    and `check(r)` true (it is the same `mid` just tested true).
  - If `check(mid)` is false, then `mid < ANSWER` (otherwise `check(mid)`
    would be true by definition of `ANSWER`); set `l = mid + 1`, which keeps
    `l ≤ ANSWER` since `ANSWER ≥ mid + 1`. The `r` bound and its feasibility
    are unchanged.

  In both cases, `l ≤ ANSWER ≤ r` continues to hold.

- **Termination.** Each branch strictly shrinks `r - l`: setting `r = mid`
  gives new width `mid - l < r - l` (since `mid < r`); setting `l = mid + 1`
  gives new width `r - mid - 1 < r - l` (since `mid ≥ l`). The width is a
  non-negative integer strictly decreasing each iteration, so the loop
  terminates in `O(log(r - l))` steps with `l = r`. Since the invariant
  `l ≤ ANSWER ≤ r` holds throughout, `l = r` forces `l = r = ANSWER`.

The method returns `r = ANSWER`, the minimum time for all buses to complete
at least `totalTrips` trips. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `min(time)` (bracket computation) | O(n) | O(1) |
| `check` (one feasibility sweep over buses) | O(n) | O(1) |
| Binary search over `[min(time), 10^14]` | O(log(10^14)) ≈ 47 checks | — |
| **Overall** | **O(n · log(maxTime · totalTrips))** | **O(1)** |

> Each binary-search step costs O(n) to sum `⌊mid / time[i]⌋` across all
> buses, and the search interval halves every step, giving a total cost of
> O(n log(upperBound)) — far cheaper than simulating trip-by-trip progress,
> which could take time proportional to `totalTrips` itself.
