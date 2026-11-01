# Minimum Discards to Balance Inventory

## Problem Statement

You are given two integers `w` and `m`, and an integer array `arrivals`, where
`arrivals[i]` is the type of the item arriving on day `i`.

Each arrival can be **kept** or **discarded**, and an item can only be
discarded on the day it arrives. The rule is:

- For every day `i`, look at the window of the last `w` days,
  `[max(0, i − w + 1), i]`.
- In every such window, each item type may appear **at most `m` times** among
  the **kept** arrivals.

Return the **minimum** number of arrivals you have to discard so that every
`w`-day window satisfies the rule.

**Example:**
```
arrivals = [1, 2, 1, 3, 1], w = 4, m = 2
Every 4-day window has at most two 1s → keep everything
Answer = 0

arrivals = [1, 2, 3, 3, 3, 4], w = 3, m = 2
Window [3, 3, 3] (days 2..4) has three 3s → discard the 3 on day 4
Answer = 1
```

**Constraints:** `1 ≤ arrivals.length ≤ 10^5`, `1 ≤ arrivals[i] ≤ 10^5`,
`1 ≤ w ≤ arrivals.length`, `1 ≤ m ≤ w`.

---

## Building Block — A Window Over *Kept* Items Only

A normal fixed-size sliding window tracks every element that enters it. Here
the window is still a fixed `w` days, but the frequency map must count only
the arrivals we **kept**:

- **Entering** day `i`: if we keep `arrivals[i]`, increment its count;
  if we discard it, the map does not change.
- **Leaving** day `i − w`: only decrement if that arrival was kept. A
  discarded arrival was never added, so removing it would corrupt the counts.

So besides the frequency map, we need to remember **which days were
discarded**. That is the job of the `skips` set.

---

## Solution — Greedy + Fixed Sliding Window

**Key Insight.** Keep every arrival you are allowed to keep; discard only
when keeping it would break the rule.

Why is this greedy safe? Discarding an item can only *help* future days by
leaving room in their windows. But an item kept **earlier** leaves the window
**earlier** than any item that comes after it. So if you have to choose
between keeping the current item or saving room for a later one of the same
type, keeping the current one is never worse: it frees its slot sooner. (The
full exchange argument is in the proof below.)

That leaves one check per day: *does the window of the last `w` days already
hold `m` kept copies of this type?*

```java
public interface MinimumDiscardsToBalanceInventory {
    int minArrivalsToDiscard(int[] arrivals, int w, int m);
}

class MinimumDiscardsToBalanceInventoryImpl implements MinimumDiscardsToBalanceInventory {

    private boolean canAccept(Map<Integer, Integer> freq, int key, int m){
        return freq.getOrDefault(key, 0) < m;
    }

    private void add(Map<Integer, Integer> freq, int key){
        freq.put(key, freq.getOrDefault(key, 0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int key){
        int v = freq.get(key);
        if(v == 1){
            freq.remove(key);
        }else{
            freq.put(key, v-1);
        }
    }

    @Override
    public int minArrivalsToDiscard(int[] arrivals, int w, int m) {

        Map<Integer, Integer> freq = new HashMap<>(arrivals.length);
        Set<Integer> skips = new HashSet<>();
        int discarded = 0;

        for(int i=0; i<w; i++){
            if(canAccept(freq, arrivals[i], m)){
                add(freq, arrivals[i]);
            }else{
                skips.add(i);
                discarded++;
            }
        }

        for(int i=w; i<arrivals.length; i++){
            if(!skips.contains(i-w)) {
                remove(freq, arrivals[i - w]);
            }
            if(canAccept(freq, arrivals[i], m)){
                add(freq, arrivals[i]);
            }else{
                skips.add(i);
                discarded++;
            }
        }

        return discarded;
    }
}
```

**How it works:**
1. `freq` maps each type to how many **kept** copies are in the current
   window. `skips` holds the indices of discarded arrivals.
2. **Fill the first window** (days `0 .. w−1`): nothing leaves yet. For each
   arrival, keep it if its type has fewer than `m` kept copies, otherwise
   discard it and record the index in `skips`.
3. **Slide** (days `w .. n−1`): first drop day `i − w` out of the window,
   but only call `remove` if that day is **not** in `skips`. Now `freq`
   describes days `[i − w + 1, i − 1]`, so the window ending at day `i` is
   exactly what we check against. Keep or discard `arrivals[i]` with the
   same rule as step 2.
4. `discarded` is the answer.

> **Why the `skips` check matters.** Without it, sliding past a discarded
> day would decrement a count that was never incremented. Take
> `arrivals = [8, 8, 8, 1, 7, 4, 3, 7, 5, 2], w = 7, m = 1`: days 1 and 2
> (both `8`) are discarded. When day 0 leaves, `8` correctly drops to `0`.
> If days 1 and 2 were also "removed", `freq.get(8)` would be `null` and the
> unboxing would throw a `NullPointerException`. In a version that used
> `getOrDefault`, the count would go **negative** instead and later
> windows would accept too many copies.

> **Why `< m` and not `≤ m`?** `freq[x]` is the count **before** adding the
> current arrival. Keeping it makes the count `freq[x] + 1`, which must
> stay `≤ m`. In other words, `freq[x] < m`.

---

## Step-by-step Trace

```
arrivals = [1, 2, 3, 3, 3, 4], w = 3, m = 2

First window (days 0..2):
i=0: 1 → freq[1]=0 < 2 keep   freq={1:1}
i=1: 2 → keep                 freq={1:1, 2:1}
i=2: 3 → keep                 freq={1:1, 2:1, 3:1}

Slide:
i=3: day 0 (1) kept → remove  freq={2:1, 3:1}
     3 → freq[3]=1 < 2 keep   freq={2:1, 3:2}
i=4: day 1 (2) kept → remove  freq={3:2}
     3 → freq[3]=2, not < 2   DISCARD   skips={4}   discarded=1
i=5: day 2 (3) kept → remove  freq={3:1}
     4 → keep                 freq={3:1, 4:1}

Answer: 1 ✓
```

```
arrivals = [8, 8, 8, 1, 7, 4, 3, 7, 5, 2], w = 7, m = 1

First window (days 0..6):
i=0: 8 → keep                 freq={8:1}
i=1: 8 → freq[8]=1, not < 1   DISCARD   skips={1}        discarded=1
i=2: 8 → DISCARD                        skips={1,2}      discarded=2
i=3..6: 1, 7, 4, 3 → all keep freq={8:1, 1:1, 7:1, 4:1, 3:1}

Slide:
i=7: day 0 (8) kept → remove  freq={1:1, 7:1, 4:1, 3:1}
     7 → freq[7]=1, not < 1   DISCARD   skips={1,2,7}    discarded=3
i=8: day 1 in skips → no remove
     5 → keep
i=9: day 2 in skips → no remove
     2 → keep

Answer: 3 ✓
```

```
arrivals = [1, 2, 1, 3, 1], w = 4, m = 2

First window (days 0..3):
i=0..3: 1, 2, 1, 3 → all keep  freq={1:2, 2:1, 3:1}

Slide:
i=4: day 0 (1) kept → remove   freq={1:1, 2:1, 3:1}
     1 → freq[1]=1 < 2 keep

Answer: 0 ✓
```

---

## Correctness Proof

Let `n = arrivals.length`. Call a set `S` of kept days **feasible** if every
window `[t − w + 1, t]` (for `0 ≤ t < n`) has at most `m` days in `S` of
any one type. We want a feasible `S` of **maximum** size; the answer is
`n − |S|`.

### Lemma 1 (Types are independent)

The constraint for type `x` only involves days whose arrival is `x`. So a
maximum feasible set is the union of the maximum feasible sets for each type
separately, and it is enough to prove the greedy is optimal for one type.

*Proof.* Keeping or discarding a day of type `y ≠ x` does not change any
count for `x`. ∎

From here on, fix a type and let `p₁ < p₂ < … < p_k` be the days it arrives.

### Lemma 2 (The algorithm checks exactly the right windows)

When day `i` is processed, `freq[arrivals[i]]` is the number of kept copies
in days `[i − w + 1, i − 1]`.

*Proof.* Each kept day is added once when it is processed. Day `i − w` is
removed at iteration `i` only if it was kept, and it was added only if it was
kept. So after the removal, `freq` counts exactly the kept days in
`[i − w + 1, i − 1]` (clipped at `0` during the first window). ∎

Adding a copy at day `i` only affects windows ending at days `t ∈ [i, i + w − 1]`.
Among the days kept so far (all `≤ i`), the window ending at `t = i` contains
the most of them, so checking that window is enough. With Lemma 2, the
greedy keeps day `i` **iff** keeping it leaves the kept set (so far) feasible.
In particular, the greedy set `G` is feasible.

### Lemma 3 (Greedy stays ahead)

Some maximum feasible set `O` agrees with `G` on every day.

*Proof.* Take a maximum feasible `O` that agrees with `G` on the longest
prefix of `p₁, p₂, …`, and suppose they first differ at `p`.

- **`O` keeps `p`, `G` discards it.** `G` discarded `p` because its window
  ending at `p` already had `m` kept copies before `p`. `O` keeps the same
  copies before `p`, so its window ending at `p` has `m + 1`. That
  contradicts `O` being feasible, so this case cannot happen.
- **`G` keeps `p`, `O` discards it.** Let `q` be the first day after `p` that
  `O` keeps (if any). Define `O' = O − {q} ∪ {p}`. Check every window
  `[t − w + 1, t]` that contains `p`:
  - If `t < q`, the kept copies of `O'` in it are those of `G` in
    `[t − w + 1, p]`. Since `t ≥ p`, that is a subset of `G`'s window ending
    at `p`, which has at most `m` copies.
  - If `t ≥ q`, the window holds `p` instead of `q`, so its count equals
    `O`'s count, which is at most `m`.

  Windows not containing `p` only lose copies. So `O'` is feasible,
  `|O'| = |O|`, and `O'` agrees with `G` beyond `p`, which contradicts our
  choice of `O`. If no `q` exists, the same argument shows `O ∪ {p}` is
  feasible, which contradicts `O` being maximum. ∎

### Theorem

By Lemma 3, `G` has maximum size for each type, and by Lemma 1 for the whole
array. So `discarded = n − |G|` is the minimum number of discards. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| Fill first window (`w` days) | O(w) | — |
| Slide over the remaining `n − w` days | O(n − w) | — |
| `freq` map (at most `w` kept items at a time) | O(1) per update, expected | O(min(n, w)) |
| `skips` set (every discarded index, never cleared) | O(1) per lookup, expected | O(n) |
| **Overall** | **O(n)** expected | **O(n)** |

> **Possible optimization.** Since `1 ≤ arrivals[i] ≤ 10^5`, `freq` can be a
> plain `int[100_001]`. `skips` can be a `boolean[n]`, or you can drop it
> entirely by setting `arrivals[i] = 0` when discarding, so day `i − w`
> removes "type 0", which nobody checks. That avoids boxing and hashing.
> The complexity stays the same, but the constant factor is much smaller.
