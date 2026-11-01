# Fruit Into Baskets

## Problem Statement

You are visiting a farm that has a single row of fruit trees arranged by
`fruits[i]`, where `fruits[i]` is the **type** of fruit the `i`-th tree
produces.

You want to collect as much fruit as possible, but the owner has rules:
you have exactly **two baskets**, and each basket can only hold a **single
type** of fruit (no limit on quantity). Starting from any tree you like, you
must pick exactly one fruit from every tree while moving to the right,
stopping once you encounter a fruit type that doesn't fit in either basket.

Given `fruits`, return the **maximum** number of fruits you can pick.

**Example:**
```
fruits = [1, 2, 1]
Two types only → pick everything
Answer = 3

fruits = [0, 1, 2, 2]
Picking from index 1: [1, 2, 2] fits two baskets
Answer = 3

fruits = [1, 2, 3, 2, 2]
Picking from index 1: [2, 3, 2, 2] fits two baskets
Answer = 4
```

**Constraints:** `1 ≤ fruits.length ≤ 10^5`, `0 ≤ fruits[i] < fruits.length`.

---

## Core Insight: This Is "At Most K Distinct" in Disguise

Stripping away the basket/tree story, the problem asks: **find the longest
contiguous subarray containing at most 2 distinct values.** Any subarray
with ≤ 2 distinct types is pickable in full; any subarray with 3+ distinct
types is not, no matter how it's arranged.

"At most 2 distinct" is closed under shrinking — removing elements can only
remove distinct values, never add them — so a **variable-size sliding
window** applies: whenever the window picks up a 3rd distinct type, shrink
from the left (the only direction that can remove a type) until it drops
back to 2.

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(1) Space

Fix every start `i`, extend `j` to the right while tracking how many
distinct types have been seen, and stop as soon as a 3rd type appears.

```java
@Override
public int totalFruit(int[] fruits) {
    int best = 0;
    for (int i = 0; i < fruits.length; i++) {
        int a = -1, b = -1;
        for (int j = i; j < fruits.length; j++) {
            if (fruits[j] != a && fruits[j] != b) {
                if (a == -1) a = fruits[j];
                else if (b == -1) b = fruits[j];
                else break; // 3rd distinct type
            }
            best = Math.max(best, j - i + 1);
        }
    }
    return best;
}
```

**Drawback:** Moving the start from `i` to `i + 1` throws away all type
information gathered for `nums[i+1..j-1]`, even though it was already known
to be good, and re-derives it from scratch for the new start.

---

### 2. Optimized Solution (Sliding Window) — O(n) Time, O(1) Space

**Key Insight.** Maintain a window `[l, r]` whose frequency map never holds
more than 2 keys.

- **Expand:** add `fruits[r]` to the frequency map.
- **Shrink:** while the map holds 3 distinct keys, drop `fruits[l]` and
  advance `l` — this can only remove one type at a time, so the loop ends
  as soon as the type that was entirely to the left of the window is gone.
- **Record:** the window size `r − l + 1` after shrinking.

```java
public interface FruitIntoBaskets {
    int totalFruit(int[] fruits);
}

class FruitIntoBasketsImpl implements FruitIntoBaskets {

    private void add(Map<Integer, Integer> freq, int fruit) {
        freq.put(fruit, freq.getOrDefault(fruit, 0) + 1);
    }

    private void remove(Map<Integer, Integer> freq, int fruit) {
        int v = freq.get(fruit);
        if (v == 1) {
            freq.remove(fruit);
        } else {
            freq.put(fruit, v - 1);
        }
    }

    @Override
    public int totalFruit(int[] fruits) {
        Map<Integer, Integer> freq = new HashMap<>();
        int l = 0;
        int tot = 0;

        for (int r = 0; r < fruits.length; r++) {
            add(freq, fruits[r]);

            while (freq.size() > 2) {
                remove(freq, fruits[l]);
                l++;
            }

            tot = Math.max(tot, r - l + 1);
        }

        return tot;
    }
}
```

> **Note on the shipped implementation:** the version in
> `FruitIntoBaskets.java` records the answer with a helper `mapTot(freq)`
> that sums every value in the frequency map, rather than `r − l + 1`.
> Both give the same result — the map's values always sum to the window
> size, since every fruit between `l` and `r` is counted exactly once — but
> `mapTot` costs O(map size) per iteration (up to O(1) here since the map
> never exceeds 2 keys, so it's still O(n) overall, just with a larger
> constant). `r - l + 1` is the simpler O(1) equivalent and is what the
> correctness proof below reasons about.

**How it works:**
1. Start with an empty window: `l = 0`, `freq = {}`.
2. For each `r`:
   - Add `fruits[r]` to `freq`.
   - While `freq` has more than 2 keys, remove `fruits[l]` and advance `l`.
     This terminates once the type that only occurred left of the new
     window has been fully removed.
   - The window `[l, r]` now has ≤ 2 distinct types; update `tot`.
3. Return `tot`.

---

## Step-by-step Trace

```
fruits = [1, 2, 3, 2, 2]
(window is [l, r], inclusive)

r=0  add 1  freq{1:1}                               l=0  tot=1
r=1  add 2  freq{1:1, 2:1}                           l=0  tot=2
r=2  add 3  freq{1:1, 2:1, 3:1} → size 3, shrink:
       remove fruits[0]=1  freq{2:1, 3:1}            l=1  tot=2
r=3  add 2  freq{2:2, 3:1}                           l=1  tot=3
r=4  add 2  freq{2:3, 3:1}                           l=1  tot=4

Answer: 4 ✓
```

---

## Correctness Proof

For each right end `r`, let `L(r)` be the **smallest** `l` such that
`fruits[l..r]` contains at most 2 distinct values. (It always exists:
`fruits[r..r]` has exactly 1 distinct value.)

### Lemma 1 (Good starts form a suffix)

For fixed `r`, `fruits[l..r]` is good (≤ 2 distinct values) **iff**
`l ≥ L(r)`.

*Proof.* For `l ≥ L(r)`, `fruits[l..r]` is a subarray of the good
`fruits[L(r)..r]`, and a subarray of a set with ≤ 2 distinct values has
≤ 2 distinct values too, so it is good. For `l < L(r)` it is bad by
minimality of `L(r)`. ∎

So the longest good subarray ending at `r` has length `r − L(r) + 1`, and
the answer is `maxᵣ (r − L(r) + 1)`.

### Lemma 2 (`L(r)` is non-decreasing)

`L(r) ≤ L(r + 1)`.

*Proof.* `fruits[L(r+1)..r]` is a subarray of the good
`fruits[L(r+1)..r+1]`, so it is good, hence `L(r) ≤ L(r+1)`. ∎

### Theorem

**Invariant.** After processing index `r`, `l = L(r)` and `freq` holds the
exact, nonzero counts of every value in `fruits[l..r]`.

- **Before shrinking.** By the invariant for `r − 1` and Lemma 2,
  `l = L(r−1) ≤ L(r)` — `l` never needs to move left.
- **Shrinking.** `fruits[l..r−1]` had ≤ 2 distinct values, and adding
  `fruits[r]` can introduce at most one new distinct value. So
  `freq.size() > 2` **iff** the window is currently bad — the loop
  condition checks exactly the right thing. While bad, `l < L(r)` by
  Lemma 1, so advancing `l` never skips past `L(r)`. Each removal strictly
  shrinks the map (a value is deleted once its count hits 0), so the loop
  terminates.
- **After shrinking.** The loop exits when `freq.size() ≤ 2`, i.e. the
  window is good, i.e. `l ≥ L(r)`. Combined with `l ≤ L(r)` from above,
  `l = L(r)`.

So at every `r`, the code records `r − L(r) + 1` (equivalently, the sum of
`freq`'s values, which always equals the window size), and `tot` ends as
the maximum over all `r`. ∎

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each start i, extend j tracking up to 2 distinct types; break on a 3rd.
       Re-derives type information from scratch for every start.
       Time: O(n²)   Space: O(1)

           ▼  Observation: "at most 2 distinct" is closed under shrinking
              (Lemma 1), and the best start for each end only moves right
              (Lemma 2). So one frequency map and two forward-only pointers
              suffice.

           ▼  Observation: adding fruits[r] introduces at most one new
              distinct value, so checking "is the window bad?" is an O(1)
              map-size check instead of a rescan.

Sliding Window ──────────────────────────────────────────────────────────────►
       Expand r, shrink l while freq.size() > 2, record r − l + 1.
       Each element enters and leaves the map at most once.
       Time: O(n)   Space: O(1)   ← Optimal
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(1) | Every start, extend until a 3rd distinct type appears |
| Sliding Window | **O(n)** | **O(1)** | Shrink only while `freq.size() > 2`; both pointers move forward |

> Space is O(1) (bounded by at most 3 keys at any instant, really ≤ 2 after
> shrinking) because the window can never hold more than 2 distinct fruit
> types — unlike a general "at most k distinct" problem, this map's size is
> capped by the constant 2, not by `n`.
