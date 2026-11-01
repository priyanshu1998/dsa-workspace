# Maximum Running Time of N Computers — Alternative Strategy Notes

Scratch notes from exploring candidate greedy/simulation strategies as
alternatives to the binary-search solution in `MaximumRunningTimeOfNComputers.java`.
Each strategy below was tested against the proven-correct binary search
(`optimal(n, batteries)`, implementing `check(t) = Σ min(bᵢ,t) ≥ n·t`) over
thousands of random cases. Failing examples are recorded here for future
reference so these dead ends aren't re-explored.

`opt` = true optimal answer (ground truth). `got` = value produced by the
candidate strategy.

---

## 1. "Top-n halving, discard leftover" strategy — WRONG (62% mismatch rate)

Idea: repeatedly take the top `n` batteries, run for `ceil(min/2)` minutes,
then swap the just-halved battery out for the next-largest unused one,
discarding its remaining `floor(min/2)` charge.

| n | batteries | opt | got |
|---|-----------|-----|-----|
| 1 | [6,1,1,1] | 9 | 6 |
| 6 | [1,9,4,8,8,9,4] | 4 | 3 |
| 2 | [4,8,5,1,7,9] | 17 | 12 |
| 1 | [5,2] | 7 | 5 |
| 7 | [10,1,8,4,7,7,3,6] | 4 | 3 |

**Diagnosis:** discarding the leftover `floor(m/2)` charge wastes real,
usable capacity. Far from optimal.

## 2. "Top-n halving, recycle leftover" strategy — WRONG (close, but ~0.3% mismatch)

Same as above, but the leftover charge is returned to the pool instead of
discarded.

| n | batteries | opt | got |
|---|-----------|-----|-----|
| 6 | [5,5,2,4,10,9,3,5] | 6 | 5 |
| 5 | [8,9,2,9,9,10,4,4] | 11 | 10 |
| 5 | [6,2,2,6,7,8,7,7] | 9 | 8 |
| 4 | [7,6,7,8,8] | 9 | 8 |
| 4 | [7,10,7,7,7] | 9 | 8 |

**Diagnosis:** always off by exactly 1, always an underestimate. Caused by
`ceil(m/2)` rounding discarding a fractional minute of charge whenever `m`
is odd, plus arbitrary tie-breaking on which battery is "weakest."

## 3. "Always use current top-n, swap only when a battery is exhausted (or ties cross)" — WRONG (~16% mismatch)

Idea: keep running with whichever `n` batteries currently have the most
charge; only re-pick when one hits zero (ties are value-neutral no-ops).

Counterexample: `n=3, batteries=[3,4,6,2,2,7]` → `opt=8`, `got=7`.

**Diagnosis:** when two active batteries hit zero **simultaneously** and
the waiting pool can't replace both at once, one computer loses power a
minute early — permanently. The true optimal schedule can split a single
battery's usage across two different computers at different times, which
this real-time policy can't do.

## 4. "Per-minute: always use current top-n, decrement by 1, re-sort, repeat" — CONFIRMED OPTIMAL ✅

Same idea as #3, but re-evaluated **every single minute** instead of only
at exhaustion events.

- 6000+ random trials (batteries up to 50, up to 12 batteries): **0 mismatches**.
- Correct because of an exchange argument: if a weaker battery is drawn
  from while a stronger one sits idle, swapping which one is used that
  minute never hurts (and can only help) future feasibility. Per-minute
  re-selection removes the "simultaneous exhaustion" gap that broke #3.
- Complexity: `O(ANSWER · m log m)` — correct but far slower than binary
  search; useful mainly as a constructive/intuitive proof, not an
  efficient algorithm.

## 5a. "Heap-based: min-heap for active top-n, max-heap for waiting, run for (active_min - waiting_max) minutes" — CONFIRMED CORRECT ✅ (after two fixes)

This is the corrected version of the event-driven idea, using a min-heap for
the active top-`n` batteries and a max-heap for the rest:

```
active = min-heap of top n battery charges
waiting = max-heap of the remaining m-n battery charges
total = 0
while active has n elements and active_min > 0:
    a, w = active_min(), waiting_max()        (w = None if waiting empty)
    if w is None: total += a; drop the exhausted battery; done
    delta = a - w if a > w else 1             # tie -> advance exactly 1 minute
    total += delta
    decrement all active charges by delta (lazy offset, O(1))
    while waiting non-empty and active_min() < waiting_max():
        swap the active minimum out for the waiting maximum
```

Two subtle fixes were required versus the naive version:

1. **Strict `<` for the swap condition**, not `<=`. A true tie
   (`active_min == waiting_max`) needs no swap at all — the batteries are
   interchangeable at that value.
2. **A `while` loop for swapping, not a single `if`.** Because the entire
   active set decrements together, *multiple* active batteries can be
   tied at the old minimum and all cross below the waiting max
   simultaneously; one swap only fixes one of them.

Verified against both the binary-search optimum and the brute-force
per-minute simulation (#4) over 10,000+ random trials, including the
`n == m` no-slack edge case that broke strategy #7: **0 mismatches**.
Complexity: `O(events · log m)`, a real improvement over the `O(ANSWER)`
per-minute loop.

## 5b. "Event-driven: jump to next exhaustion/crossing, scanning past ties" — WRONG (superseded by 5a)

Attempted to skip ahead instead of ticking minute-by-minute, using
`delta = min(v_n, v_n - v_strict_next)` where `v_strict_next` scans past
tied values. Breaks because decrementing only indices `[0, n)` desyncs the
sorted-order invariant when ties straddle the boundary.

| n | batteries | opt | got |
|---|-----------|-----|-----|
| 4 | [13,39,12,15,16,9,49] | 32 | 28 |
| 4 | [44,14,10,30,43,12] | 33 | 36 |
| 6 | [6,46,34,44,50,6,31,21,26,3] | 42 | 47 |
| 3 | [3,8,2,11,31,44,27,6,28] | 53 | 57 |
| 4 | [15,17,8,24,12,44] | 25 | 27 |

## 6. "Event-driven with tie-groups treated as a fractionally-depleting shared pool" — WRONG

Tried modeling tied-value batteries as one pool depleting at an averaged
rate. Still wrong because it only checks the boundary group's crossing
with its immediate neighbor, not crossings between *every* pair of groups
(a faster-depleting "fully active" prefix group can cross below a
slower-depleting partially-active boundary group).

| n | batteries | opt | got |
|---|-----------|-----|-----|
| 3 | [47,38,20,13,47,27,49,46] | 95 | 96.83 |
| 5 | [47,40,14,20,35,46,22] | 43 | 56.0 |
| 2 | [47,50,14,45,49,47,30,46,42] | 185 | 208.0 |
| 4 | [22,23,13,21,28,27] | 33 | 56.0 |
| 5 | [14,27,15,14,3,48] | 15 | 15.5 |
| 7 | [40,8,22,42,39,50,15,16,30,24] | 38 | 45.0 |

## 7. "Closed-form sorted-prefix / reservoir-folding" (no simulation at all) — WRONG

Attempted an `O(m log m)` direct formula: sort ascending, fold the top-n
candidates (smallest first) into an "extra" reservoir while they're below
the achievable average, stop once the smallest remaining candidate is
`>=` that average.

| n | batteries | opt | got | note |
|---|-----------|-----|-----|------|
| 5 | [58,6,70,68,54,17,92,85,94,48] | 118 | 118.4 | floor matches opt — just continuous rounding |
| 2 | [61,64,51,7,86,95,27] | 195 | 195.5 | floor matches opt |
| 4 | [84,33,83,97] | **33** | **74.25** | genuine bug — `n == m`, no slack battery to "lend" charge at all |
| 9 | [82,36,11,3,23,31,33,77,56,13,66,29] | 35 | 51.11 | genuine bug |
| 8 | [2,89,26,86,69,6,98,90,74,65,83,86] | 96 | 96.75 | floor matches opt |
| 3 | [15,38,76,14,10,92,16] | 84 | 87.0 | genuine bug |

**Diagnosis:** when `n == m` (no spare/idle battery at all), there is no
actual reservoir to redistribute from, but the folding logic incorrectly
assumes charge is always freely shareable. Several other cases show the
floor of the continuous relaxation matching `opt`, suggesting the
underlying LP idea is close but the fold/stop condition is not yet
correctly derived.

---

## Conclusion

The strategies that are *provably correct*:

1. **Binary search on the answer** (`check(t) = Σ min(bᵢ,t) ≥ n·t`), already
   implemented in `MaximumRunningTimeOfNComputers.java` — `O(m log Σbatteries)`.
   Simplest and asymptotically best.
2. **Per-minute "always use current top-n" simulation** (#4 above) —
   correct but `O(ANSWER · m log m)`; useful mainly as an intuition/proof
   aid, not a practical algorithm for large inputs.
3. **Heap-based event-driven simulation** (#5a above) — correct and
   `O(events · log m)`, a genuinely efficient constructive alternative to
   #4 once the strict-inequality and multi-swap fixes are applied.

Every other attempt (#1, #2, #3, #5b, #6, #7) introduced subtle bugs
around tied values and/or no-slack (`n == m`) edge cases, underscoring how
easy it is to silently drop charge or stall on ties in a hand-rolled
simulation.
