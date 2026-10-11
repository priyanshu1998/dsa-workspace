# Heap-Based Event-Driven Simulation — Detailed Walkthrough

This document explains, in detail, how the confirmed-correct heap-based
simulation (strategy **#5a** in
`MaximumRunningTimeOfNComputers-strategy-notes.md`) works, with two fully
worked examples. The corresponding implementation lives in
`test_strategy7.py` (`heap_strategy`).

## The invariant it maintains

At every moment, `active` holds the `n` batteries currently powering the
computers, and `waiting` holds the rest. The algorithm keeps this true at
all times:

> **`active_min() ≥ waiting_max()`** — the weakest battery currently in
> use always has at least as much charge as the strongest battery sitting
> idle.

If that ever stopped being true, you'd be wasting a bigger battery by
leaving it idle while a smaller one keeps draining — suboptimal. The whole
algorithm is just "jump forward in time as far as possible without
breaking this invariant, then patch it up."

## The lazy-offset trick (why `active_offset` exists)

Every minute, **all `n` active batteries drain by 1 simultaneously** — not
just the weakest one. If we advance by `delta` minutes, every single value
in `active` needs `-= delta`. Touching all `n` heap entries every event
would be `O(n)` per event. Instead:

- `active_heap` stores **raw** numbers.
- `active_offset` accumulates the total time elapsed since those raw
  numbers were inserted.
- The **true** current charge of any entry is `raw - active_offset`.

Advancing time by `delta` becomes a single `active_offset += delta` —
O(1) — because subtracting `active_offset` from the heap's top
automatically reflects everyone's decay at read-time. `waiting_heap` never
decays (those batteries are idle), so it needs no offset.

The only care needed: when an element **moves** between the two heaps,
you must bake in / strip out the offset so its value is interpreted
correctly in its new home (that's the `cur_w + active_offset` /
`true_popped` conversions in the code).

## The main loop, piece by piece

```python
a = active_min()          # weakest currently-active battery
w = waiting_max()         # strongest currently-idle battery

if w is None:              # nothing left to swap in
    total += a              # ride the weakest battery to 0, then we're done
    drop it from active

delta = a - w if a > w else 1   # jump straight to the next crossing point...
                                  # ...or 1 minute if already tied (can't jump 0)
total += delta
active_offset += delta           # O(1) decay of ALL active batteries

while active_min() < waiting_max():   # STRICT <, ties need no action
    swap active's weakest out for waiting's strongest
    # (may fire more than once -- see Example 2)
```

Why `delta = a - w`: that's exactly the number of minutes until the
weakest active battery's charge drops down to meet the strongest waiting
battery's charge — the next moment the invariant *would* break if we
didn't swap. Why `delta = 1` on a tie: the gap is already 0, so you can't
"jump" — you must simulate one real minute to make any progress, after
which a genuine gap reopens (or a swap becomes necessary, handled by the
`while` loop immediately after).

---

## Example 1 — `n=2, batteries=[3,3,3]` (expected answer: 4)

Initial: `active = {3,3}`, `waiting = {3}`, `offset = 0`, `total = 0`.

| Step | a | w | delta | total | action |
|---|---|---|---|---|---|
| 1 | 3 | 3 | tie → 1 | 1 | offset→1, active true={2,2}; swap loop: 2<3 → swap (pop 2, bring in 3) → active={2,3}, waiting={2} |
| 2 | 2 | 2 | tie → 1 | 2 | offset→2, active true={1,2}; swap loop: 1<2 → swap → active={2,2}, waiting={1} |
| 3 | 2 | 1 | 2-1=1 | 3 | offset→3, active true={1,1}; swap loop: 1<1? no → no swap |
| 4 | 1 | 1 | tie → 1 | 4 | offset→4, active true={0,0}; swap loop: 0<1 → swap → active={0,1}, waiting={0} |
| 5 | active_min = 0 | — | — | — | loop condition `a ≤ 0` → **stop** |

**Result: `total = 4`** ✅ — matches the known answer, and matches the
LeetCode example's own construction (computer1: battery0 for 3 min +
battery2 for 1 min; computer2: battery1 for 3 min + battery2 for 2 min).

---

## Example 2 — `n=2, batteries=[6,9,5,7,12]` (expected answer: 19) — shows the multi-swap fix in action

Initial: `active = {9,12}`, `waiting = {5,6,7}`.

| Step | active (true) | waiting | a | w | delta | total | notes |
|---|---|---|---|---|---|---|---|
| 1 | {9,12} | {5,6,7} | 9 | 7 | 2 | 2 | → {7,10}; swap: 7<7? no |
| 2 | {7,10} | {5,6,7} | 7 | 7 | tie→1 | 3 | → {6,9}; swap: 6<7 → swap → {7,9}, waiting {5,6,6} |
| 3 | {7,9} | {5,6,6} | 7 | 6 | 1 | 4 | → {6,8}; swap: 6<6? no |
| 4 | {6,8} | {5,6,6} | 6 | 6 | tie→1 | 5 | → {5,7}; swap: 5<6 → swap → {6,7}, waiting {5,5,6} |
| 5 | {6,7} | {5,5,6} | 6 | 6 | tie→1 | 6 | → {5,6}; swap: 5<6 → swap → {6,6}, waiting {5,5,5} |
| 6 | {6,6} | {5,5,5} | 6 | 5 | 1 | 7 | → {5,5}; swap: 5<5? no |
| 7 | {5,5} | {5,5,5} | 5 | 5 | tie→1 | 8 | → **{4,4}** (both tied entries drop together!); swap loop fires **twice**: 4<5→swap→{4,5},waiting{4,5,5}; still 4<5→swap again→{5,5},waiting{4,4,5} |

**This is the critical moment**: at step 7, *both* active batteries were
tied at 5, so advancing by `delta=1` dropped **both** to 4
simultaneously, and **both** needed to be swapped out — which is exactly
why the `while` loop (not a single `if`) is required. A single swap here
would have left one battery stuck at 4 while a 5 sat idle, silently
losing 1 minute (this was the original bug).

Continuing the same pattern down through decreasing tie-levels
(5→4→3→2→1→0), the simulation eventually reaches `active_min ≤ 0` and
stops with **`total = 19`**, matching the brute-force per-minute ground
truth exactly.

---

## Takeaway

The algorithm is a fast-forwarded version of "always run the current best
`n` batteries" — it jumps straight to the next instant where that
selection would need to change (an exhaustion or a crossing), and because
entire tie-groups decay together, it must be ready to perform **several**
swaps per jump, not just one.
