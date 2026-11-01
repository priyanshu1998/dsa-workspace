# Alternating Groups II

## Problem Statement

There is a **circle** of red and blue tiles. You are given an array `colors`
and an integer `k`, where `colors[i]` is the color of tile `i`:

- `colors[i] == 0` means tile `i` is **red**,
- `colors[i] == 1` means tile `i` is **blue**.

An **alternating group** is any `k` contiguous tiles in the circle whose
colors alternate — every tile in the group, except the first and last, has a
different color from both its left and right neighbour.

Return the number of alternating groups.

Because the tiles form a circle, the first and last tiles are neighbours, so
a group may wrap around from the end of the array back to the start.

**Example:**
```
colors = [0, 1, 0, 1, 0], k = 3
Groups (by start index): 0→[0,1,0] ✓  1→[1,0,1] ✓  2→[0,1,0] ✓
                         3→[1,0,0] ✗  4→[0,0,1] ✗   (3 and 4 wrap around)
Answer = 3

colors = [0, 1, 0, 0, 1, 0, 1], k = 6
Only the groups starting at 3 → [0,1,0,1,0,1] and 4 → [1,0,1,0,1,0] alternate
Answer = 2

colors = [1, 1, 0, 1], k = 4
Every rotation contains the adjacent pair 1,1
Answer = 0
```

**Constraints:** `3 ≤ colors.length ≤ 10^5`, `0 ≤ colors[i] ≤ 1`,
`3 ≤ k ≤ colors.length`.

---

## Core Insight: Alternating = "Color Is Fixed by Index Parity"

A binary window alternates iff every element at an even index has the same
color, every element at an odd index has the other color. There are only two
such patterns:

```
index parity :  E  O  E  O  E  O
pattern A    :  0  1  0  1  0  1     (even → 0, odd → 1)
pattern B    :  1  0  1  0  1  0     (even → 1, odd → 0)
```

So for any window it's enough to know four counts:

| | color 0 | color 1 |
|---|---|---|
| **even index** | `ZeroAtEvenIndexes` | `OneAtEvenIndexes` |
| **odd index**  | `ZeroAtOddIndexes`  | `OneAtOddIndexes`  |

- Pattern A ⇔ `OneAtEvenIndexes == 0 && ZeroAtOddIndexes == 0`
- Pattern B ⇔ `ZeroAtEvenIndexes == 0 && OneAtOddIndexes == 0`

These counts are additive: sliding the window by one changes exactly one
cell when an element leaves and one when an element enters. That makes the
check `O(1)` per window — a fixed-size sliding window.

> The parity used is the parity of the element's **absolute** index, not its
> offset inside the window. That's fine: shifting the window by one flips
> every offset's parity, which just swaps pattern A and pattern B — and we
> accept both.

---

## Handling the Circle: Unroll It

A group can start at any of the `n` tiles and wrap around. The standard
trick is to append the first `k − 1` tiles to the end of the array:

```
colors = [0, 1, 0, 1, 0], k = 3
groups = [0, 1, 0, 1, 0, 0, 1]
          └───── n ─────┘ └k-1┘
```

Now every circular group is an ordinary contiguous window of length `k` in
`groups`, and there are exactly `(n + k − 1) − k + 1 = n` such windows — one
per start tile, no duplicates.

---

## Solutions

### 1. Naive Solution — O(n·k) Time, O(1) Space

For each start `s`, walk the `k − 1` adjacent pairs and reject on the first
equal pair.

```java
@Override
public int numberOfAlternatingGroups(int[] colors, int k) {
    int n = colors.length;
    int count = 0;
    for (int s = 0; s < n; s++) {
        boolean ok = true;
        for (int j = 1; j < k && ok; j++) {
            ok = colors[(s + j) % n] != colors[(s + j - 1) % n];
        }
        if (ok) {
            count++;
        }
    }
    return count;
}
```

With `n = k = 10^5` that is up to `10^10` comparisons — far too slow.
Consecutive windows share `k − 1` elements, and the naive approach re-checks
all of them.

---

### 2. Optimized Solution (Fixed Window + Parity Counts) — O(n + k) Time, O(n + k) Space

```java
public interface AlternatingGroups2 {
    int numberOfAlternatingGroups(int[] colors, int k);
}

class AlternatingGroups2Impl implements AlternatingGroups2 {

    private record Parity(int ZeroAtEvenIndexes,
                          int OneAtEvenIndexes,
                          int ZeroAtOddIndexes,
                          int OneAtOddIndexes){}

    private void initializeGroups(int[] colors, int k, int[] groups){
        for(int i=0; i<colors.length; i++){
            groups[i] = colors[i];
        }

        for(int i=0; i<k-1; i++){
            groups[colors.length+i] = colors[i];
        }
    }

    private Parity initializeWindow(int[] groups, int k){
        int ZeroAtEvenIndexes = 0;
        int OneAtEvenIndexes = 0;
        int ZeroAtOddIndexes = 0;
        int OneAtOddIndexes = 0;
        for(int i=0; i<k; i++){
            if(i%2 == 0){
                if(groups[i] == 0){
                    ZeroAtEvenIndexes += 1;
                }else{
                    OneAtEvenIndexes += 1;
                }
            }else{
                if(groups[i] == 0){
                    ZeroAtOddIndexes += 1;
                }else{
                    OneAtOddIndexes += 1;
                }
            }
        }

        return new Parity(ZeroAtEvenIndexes, OneAtEvenIndexes, ZeroAtOddIndexes, OneAtOddIndexes);
    }

    private boolean check(Parity state){
        if(state.OneAtEvenIndexes == 0 && state.ZeroAtOddIndexes == 0){
            return true;
        }else if(state.ZeroAtEvenIndexes == 0 && state.OneAtOddIndexes == 0){
            return true;
        }
        return false;
    }

    private Parity updateWindow(Parity current, int l, int r, int[] groups){
        int ZeroAtEvenIndexes = current.ZeroAtEvenIndexes;
        int OneAtEvenIndexes = current.OneAtEvenIndexes;
        int ZeroAtOddIndexes = current.ZeroAtOddIndexes;
        int OneAtOddIndexes = current.OneAtOddIndexes;

        // remove l
        if(l%2==0){
            if(groups[l] == 0){
                ZeroAtEvenIndexes -= 1;
            }else{
                OneAtEvenIndexes -= 1;
            }
        }else{
            if(groups[l] == 0){
                ZeroAtOddIndexes -=1;
            }else{
                OneAtOddIndexes -= 1;
            }
        }

        // add r

        if(r%2==0){
            if(groups[r] == 0){
                ZeroAtEvenIndexes += 1;
            }else{
                OneAtEvenIndexes += 1;
            }
        }else{
            if(groups[r] == 0){
                ZeroAtOddIndexes +=1;
            }else{
                OneAtOddIndexes += 1;
            }
        }

        return new Parity(ZeroAtEvenIndexes, OneAtEvenIndexes, ZeroAtOddIndexes, OneAtOddIndexes);
    }

    @Override
    public int numberOfAlternatingGroups(int[] colors, int k) {
        var groups = new int[colors.length+k-1];
        initializeGroups(colors, k, groups);
        int count = 0;

        var state = initializeWindow(groups, k);
        if(check(state)){
            count += 1;
        }


        for(int i=k; i<groups.length; i++){
            state = updateWindow(state, i-k, i, groups);
            if(check(state)){
                count += 1;
            }
        }

        return count;
    }
}
```

**How it works:**
1. **Unroll the circle** (`initializeGroups`): copy `colors` into `groups`
   and append `colors[0..k−2]`, giving an array of length `n + k − 1`.
2. **Build the first window** (`initializeWindow`): bucket each of
   `groups[0..k−1]` by (index parity, color) into the four counters.
3. **Check** (`check`): the window alternates iff it matches pattern A
   (no `1` at even, no `0` at odd) or pattern B (no `0` at even, no `1` at
   odd).
4. **Slide** (`updateWindow`): for each new right end `r = i`, the element at
   `l = i − k` leaves — decrement its bucket — and `groups[r]` enters —
   increment its bucket. Then check again.
5. The loop visits windows starting at `1 … n − 1`, plus the initial window
   at `0`: `n` windows total, one per tile.

> **Why the `Parity` record is immutable.** `updateWindow` copies the four
> fields into locals, adjusts them and returns a fresh record. That's one
> small allocation per step — perfectly fine for `10^5` steps (the JIT will
> usually scalar-replace it), and it keeps `check` a pure function of the
> state.

---

## Step-by-step Trace

```
colors = [0, 1, 0, 1, 0], k = 3, n = 5
groups = [0, 1, 0, 1, 0, 0, 1]     (indices 0..6)

counters shown as (Z_e, O_e, Z_o, O_o)

init  window [0..2] = 0 1 0   idx0 E:0, idx1 O:1, idx2 E:0
      (2, 0, 0, 1)            O_e=0, Z_o=0 → pattern A ✓      count=1

i=3   −idx0 (E,0)  +idx3 (O,1)
      window [1..3] = 1 0 1   (1, 0, 0, 2) → pattern A ✓      count=2

i=4   −idx1 (O,1)  +idx4 (E,0)
      window [2..4] = 0 1 0   (2, 0, 0, 1) → pattern A ✓      count=3

i=5   −idx2 (E,0)  +idx5 (O,0)
      window [3..5] = 1 0 0   (1, 0, 1, 1) → A needs Z_o=0 ✗
                                             B needs Z_e=0 ✗

i=6   −idx3 (O,1)  +idx6 (E,1)
      window [4..6] = 0 0 1   (1, 1, 1, 0) → A ✗  B ✗

Answer: 3 ✓
```

Note how windows `[1..3] = 1 0 1` and `[0..2] = 0 1 0` both match
**pattern A** in absolute-index terms: `1 0 1` sits at indices `O E O`, so its
evens are `0` and its odds are `1`. That's the "shifting swaps the patterns"
remark in action.

```
colors = [0, 1, 0, 0, 1, 0, 1], k = 6, n = 7
groups = [0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1]   (indices 0..11)

start 0  [0..5]  0 1 0 0 1 0   (2, 1, 2, 1) ✗
start 1  [1..6]  1 0 0 1 0 1   (1, 2, 2, 1) ✗
start 2  [2..7]  0 0 1 0 1 0   (1, 2, 3, 0) ✗
start 3  [3..8]  0 1 0 1 0 1   (0, 3, 3, 0) → B ✓              count=1
start 4  [4..9]  1 0 1 0 1 0   (0, 3, 3, 0) → B ✓              count=2
start 5  [5..10] 0 1 0 1 0 0   (1, 2, 3, 0) ✗
start 6  [6..11] 1 0 1 0 0 1   (1, 2, 2, 1) ✗

Answer: 2 ✓
```

```
colors = [1, 1, 0, 1], k = 4, n = 4
groups = [1, 1, 0, 1, 1, 1, 0]

start 0  1 1 0 1   (1, 1, 0, 2) ✗
start 1  1 0 1 1   (1, 1, 0, 2) ✗
start 2  0 1 1 1   (1, 1, 0, 2) ✗
start 3  1 1 1 0   (1, 1, 0, 2) ✗

Answer: 0 ✓
```

---

## Correctness Proof

### Lemma 1 (Unrolling is a bijection)

For each start tile `s ∈ [0, n)`, the circular group of `k` tiles starting
at `s` equals `groups[s..s+k−1]`.

*Proof.* `groups[j] = colors[j mod n]` for every `j ∈ [0, n + k − 1)`, since
`k − 1 < n`. The circular group is `colors[s mod n], …, colors[(s+k−1) mod n]`,
which is exactly that slice. The `n` starts map to the `n` windows of
`groups`, one to one. ∎

### Lemma 2 (Parity characterization)

A window `w = groups[l..r]` alternates **iff** there is a bit `b` such that
`groups[j] = b ⊕ (j mod 2)` for all `j ∈ [l, r]`.

*Proof.* (⇐) Consecutive indices have different parities, so consecutive
values differ. (⇒) If adjacent values always differ then, in a binary
alphabet, `groups[j] = groups[l] ⊕ ((j − l) mod 2)`. Take
`b = groups[l] ⊕ (l mod 2)`. ∎

`b = 0` is pattern A (even → 0, odd → 1); `b = 1` is pattern B.

### Lemma 3 (Counter test)

Pattern A holds on `w` iff `w` has no `1` at an even index and no `0` at an
odd index, i.e. `OneAtEvenIndexes = ZeroAtOddIndexes = 0`. Symmetrically
for pattern B. This is just the definition restated as counts.

### Theorem

**Invariant.** Before each `check`, the four counters equal the true
(parity, color) counts of the current window `groups[i−k+1..i]`.

- **Initialization.** `initializeWindow` counts `groups[0..k−1]` directly.
- **Maintenance.** Going from `[i−k..i−1]` to `[i−k+1..i]` removes exactly
  index `i − k` and adds exactly index `i`. `updateWindow` decrements the
  bucket of the former and increments the bucket of the latter, so all four
  counters stay exact.

By Lemmas 2 and 3, `check` returns `true` exactly for alternating windows,
and by Lemma 1 the windows checked are exactly the `n` circular groups. ∎

---

## Alternatives

### 1. Count Bad Adjacent Pairs — O(n + k) Time, O(1) Space

A window alternates iff it contains **zero** adjacent equal pairs. A window of
length `k` contains `k − 1` adjacent boundaries; sliding it drops boundary
`(l, l+1)` and adds boundary `(r−1, r)`. Track `bad` = number of equal
boundaries inside and count windows where `bad == 0`. Using `% n` indexing
removes the need for the unrolled array:

```java
int n = colors.length, bad = 0, count = 0;
for (int j = 1; j < k; j++) {
    if (colors[j] == colors[j - 1]) bad++;
}
if (bad == 0) count++;
for (int s = 1; s < n; s++) {
    if (colors[s] == colors[s - 1]) bad--;                                  // drop (s-1, s)
    if (colors[(s + k - 1) % n] == colors[(s + k - 2) % n]) bad++;          // add (s+k-2, s+k-1)
    if (bad == 0) count++;
}
return count;
```

This is the same "count pairs, not characters" idea as
*Find the Longest Semi-Repetitive Substring*, with a fixed window and a
limit of zero.

### 2. Running Alternating Streak — O(n + k) Time, O(1) Space

Walk the unrolled circle once and keep `len` = length of the current
alternating run ending at `j` (`len++` if `colors[j % n] != colors[(j−1) % n]`,
else `len = 1`). Every position `j ≥ k − 1` with `len ≥ k` is the right end
of one alternating group. This is the most compact solution and is the one
usually seen in the LeetCode discussion.

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each of the n starts, check k−1 adjacent pairs.
       Time: O(n·k)   Space: O(1)

           ▼  Observation: consecutive windows share k−1 elements, and
              "alternating" is decidable from additive counts
              (parity × color, or number of bad pairs).

Fixed Window + Parity Counts ────────────────────────────────────────────────►
       Unroll the circle, keep four (parity, color) counters,
       accept when pattern A or pattern B has no violations.
       Time: O(n + k)   Space: O(n + k)

           ▼  Observation: index with % n instead of copying, and a single
              "bad pair" counter is enough.

Bad-Pair Window / Alternating Streak ────────────────────────────────────────►
       Time: O(n + k)   Space: O(1)   ← Optimal
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n·k) | O(1) | Re-check every window from scratch |
| Fixed Window + Parity Counts | **O(n + k)** | O(n + k) | Four (parity, color) counters; match pattern A or B |
| Bad-Pair Window | **O(n + k)** | **O(1)** | Window alternates iff it has no equal adjacent pair |
| Alternating Streak | **O(n + k)** | **O(1)** | Count ends where the current run length ≥ `k` |

> Since `k ≤ n`, `O(n + k)` is just `O(n)`. The extra space in Solution 2
> is the unrolled `groups` array of length `n + k − 1 < 2n`; replacing
> `groups[j]` with `colors[j % n]` would make it `O(1)` as well.

---

## Related Problems

- [Find the Longest Semi-Repetitive Substring](FindTheLongestSemiRepetitiveSubstring.md) — counts adjacent equal pairs inside a window, the basis of the bad-pair alternative.
- [Count the Number of Good Subarrays](CountTheNumberOfGoodSubarrays.md) — another window whose validity is tracked through incrementally maintained counts.
