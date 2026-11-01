# Minimum Consecutive Cards to Pick Up

## Problem Statement

You are given an integer array `cards` where `cards[i]` is the value of the
`i`-th card. Two cards are **matching** if they have the same value.

Return the **minimum number of consecutive cards** you have to pick up so that
the picked cards contain a pair of matching cards. If no pair of matching cards
exists, return `-1`.

**Example:**
```
cards = [3, 4, 2, 3, 4, 7]
[3,4,2,3] has the matching pair 3…3, length 4
[4,2,3,4] has the matching pair 4…4, length 4
No window of length ≤ 3 has a repeat
Answer = 4

cards = [1, 0, 5, 3]
All values are distinct
Answer = -1
```

**Constraints:** `1 ≤ cards.length ≤ 10^5`, `0 ≤ cards[i] ≤ 10^6`.

---

## Core Insight: The Best Window Is Bounded by Two Equal Cards

Take any window that contains a matching pair `cards[i] == cards[j]` (`i < j`).
The sub-window `cards[i..j]` also contains that pair and is no longer. So the
answer is always of the form

```
min over i < j with cards[i] == cards[j] of (j − i + 1)
```

and for a fixed right end `j`, only the **nearest** earlier copy of `cards[j]`
matters, since any earlier copy gives a longer window.

There's a second way to see the same thing. In the shortest such window
`cards[i..j]`, the cards strictly between `i` and `j` are all distinct and none
of them equals `cards[j]`, because otherwise a shorter window would exist. So
`cards[i+1..j]` is a **duplicate-free** window, and the answer is "one more
than the longest duplicate-free window that ends at `j` and is blocked on the
left by a copy of `cards[j]`". That's what the sliding-window solution uses.

> This is the mirror image of
> [Longest Substring Without Repeating Characters](LongestSubstringWithoutRepeatingCharacters.md):
> the same window (all elements unique) is maintained, but instead of recording
> its length, we record the length of the window **plus the one card that was
> just dropped** to make it unique again.

---

## Solutions

### 1. Naive Solution: O(n²) Time, O(1) Space

Try every pair `(i, j)` and keep the shortest matching one.

```java
@Override
public int minimumCardPickup(int[] cards) {
    int ans = Integer.MAX_VALUE;
    for (int i = 0; i < cards.length; i++) {
        for (int j = i + 1; j < cards.length; j++) {
            if (cards[i] == cards[j]) {
                ans = Math.min(ans, j - i + 1);
                break;              // a later j only gives a longer window
            }
        }
    }
    return ans == Integer.MAX_VALUE ? -1 : ans;
}
```

**Drawback:** With `n = 10^5` and mostly distinct cards, the inner loop runs
to the end almost every time, which is about `5 · 10^9` comparisons. That is
far too slow.

---

### 2. Optimized Solution (Sliding Window): O(n) Time, O(n) Space

**Key Insight.** Keep a window `[l, r]` that never contains a repeated card,
along with the count of each card inside it.

- **Expand:** add `cards[r]` to the window.
- **Shrink:** if that pushed `cards[r]`'s count to 2, drop cards from the left
  until it is back to 1. The last card dropped is exactly the previous copy of
  `cards[r]`, so it sits at `l − 1`.
- **Record:** if `cards[l − 1] == cards[r]`, then `cards[l−1..r]` is the
  shortest window ending at `r` with a matching pair. Its length is
  `r − (l − 1) + 1`.

```java
@Leetcode(id = 2260, name = "minimum-consecutive-cards-to-pick-up")
public interface MinimumConsecutiveCardsToPickUp {
    int minimumCardPickup(int[] cards);
}

class MinimumConsecutiveCardsToPickUpImpl implements MinimumConsecutiveCardsToPickUp {

    private void add(Map<Integer, Integer> freq, int card){
        freq.put(card, freq.getOrDefault(card,0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int card){
        int v = freq.get(card);
        if(v == 1){
            freq.remove(card);
        }else{
            freq.put(card, v-1);
        }
    }

    @Override
    public int minimumCardPickup(int[] cards) {
        int l = 0;

        int ans = cards.length+1;
        var freq = new HashMap<Integer, Integer>();

        for(int r=0; r < cards.length; r++){
            // add leading element
            add(freq, cards[r]);

            // remove trailing elements
            while(freq.get(cards[r]) > 1){
                remove(freq, cards[l]);
                l++;
            }

            if(l>0 && cards[l-1] == cards[r]) ans = Math.min(ans, r-(l-1)+1);
        }
        return ans == cards.length+1 ? -1 : ans;
    }
}
```

**How it works:**
1. Start with an empty window: `l = 0`, `freq = {}`, and `ans = n + 1`.
2. For each `r`:
   - Add `cards[r]` to `freq`.
   - While `freq[cards[r]] > 1`, remove `cards[l]` and advance `l`. Only
     `cards[r]`'s count went up, so only `cards[r]` can be duplicated. The
     loop stops right after `l` steps past the earlier copy of `cards[r]`.
   - Now `[l, r]` is duplicate-free. If `cards[l − 1] == cards[r]`, the window
     `[l − 1, r]` holds a matching pair, so update `ans` with `r − l + 2`.
3. If `ans` is still `n + 1`, no pair was ever found, so return `-1`.

> **Why `n + 1` as the sentinel?** Any matching window has length at most `n`,
> so `n + 1` can never be a real answer. That makes it safe to use as "not
> found", and it avoids `Integer.MAX_VALUE`.

> **Why `l > 0`?** `l − 1` is only a valid index once at least one card has
> been dropped. Before that, no duplicate has been seen at all.

> **Does the check also fire when nothing was removed this step?** Yes, and
> that is fine. If `cards[r]` is not in `[l, r−1]` but `cards[l − 1]` happens
> to equal it, then `l − 1` is still the nearest earlier copy of `cards[r]`,
> so `[l − 1, r]` is still the shortest matching window ending at `r`. In both
> cases the check records a real, tight window.

> **Not every pair gets recorded, and that's fine.** If the previous copy of
> `cards[r]` is further left than `l − 1`, the pair is skipped. But some other
> duplicate already forced `l` past it, and that duplicate's window is
> strictly shorter (see the proof below).

---

### 3. Last-Seen Index: O(n) Time, O(V) Space

The core insight says that for each `r` we only need the nearest earlier copy
of `cards[r]`. Store the last index of every value and check the distance
directly, with no window and no inner loop.

```java
@Override
public int minimumCardPickup(int[] cards) {
    int[] last = new int[1_000_001];   // last[v] = (last index of v) + 1, 0 if unseen
    int ans = Integer.MAX_VALUE;

    for (int r = 0; r < cards.length; r++) {
        int c = cards[r];
        if (last[c] > 0) {
            ans = Math.min(ans, r - (last[c] - 1) + 1);
        }
        last[c] = r + 1;
    }

    return ans == Integer.MAX_VALUE ? -1 : ans;
}
```

- Storing `index + 1` means the default `0` already means "unseen", so the
  array doesn't need to be filled with `-1`.
- `V = 10^6 + 1` comes from the value range. A 4 MB `int[]` is acceptable
  here. A `HashMap<Integer, Integer>` gives O(n) space instead if the value
  range were larger.
- Unlike the jump version of Longest Substring Without Repeating Characters,
  no `Math.max(l, …)` is needed: we measure the distance to the previous copy
  directly, and there is no window boundary to keep consistent.

---

## Step-by-step Trace

```
cards = [3, 4, 2, 3, 4, 7], n = 6, ans starts at 7
(sliding window, window is [l, r] inclusive)

r=0  add 3  freq{3:1}                      l=0  l=0, skip
r=1  add 4  freq{3:1, 4:1}                 l=0  l=0, skip
r=2  add 2  freq{3:1, 4:1, 2:1}            l=0  l=0, skip
r=3  add 3  freq[3]=2 → shrink:
       remove cards[0]=3                   l=1  cards[0]=3 == 3 → len 4   ans=4
r=4  add 4  freq[4]=2 → shrink:
       remove cards[1]=4                   l=2  cards[1]=4 == 4 → len 4   ans=4
r=5  add 7                                 l=2  cards[1]=4 ≠ 7            ans=4

Answer: 4 ✓
```

```
cards = [1, 0, 5, 3], n = 4, ans starts at 5

r=0..3  every card is new, l stays 0, the check is always skipped

ans = 5 = n + 1 → Answer: -1 ✓
```

```
cards = [70,37,70,41,1,62,71,49,38,50,29,76,29,41,22,66,88,18,85,53]
index:    0  1  2  3 4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19

r=2   add 70  freq[70]=2 → remove cards[0]=70     l=1
        cards[0]=70 == 70 → len 3                      ans=3
r=3..11  all new                                  l=1  cards[0]=70, no match
r=12  add 29  freq[29]=2 → shrink:
        remove 37, 70, 41, 1, 62, 71, 49, 38, 50  l=10
        remove cards[10]=29                       l=11
        cards[10]=29 == 29 → len 3                     ans=3
r=13  add 41  (the old 41 at index 3 is already out of the window)
                                                  l=11 cards[10]=29 ≠ 41
        The pair 41…41 (length 11) is never recorded: it is longer
        than the 29…29 window that pushed l past index 3.
r=14..19  all new                                 no match

Answer: 3 ✓
```

---

## Correctness Proof

Let `prev(r)` be the largest `i < r` with `cards[i] == cards[r]`, if one
exists. For each `r`, let `L(r)` be the **smallest** `l` such that
`cards[l..r]` has no repeated card.

### Lemma 1 (The answer is a nearest-copy distance)

The answer is `min over r with prev(r) defined of (r − prev(r) + 1)`.

*Proof.* Any window with a matching pair `cards[i] == cards[j]`, `i < j`,
contains `cards[i..j]`, so its length is at least `j − i + 1`. Since
`prev(j) ≥ i`, that is at least `j − prev(j) + 1`. And `cards[prev(j)..j]` is
itself a valid window of that length. ∎

### Lemma 2 (Window boundary)

The sliding window keeps `l = L(r)` after processing each `r`, and
`L(r) = max(L(r − 1), prev(r) + 1)` (taking `prev(r) + 1 = 0` if no previous
copy exists).

*Proof.* This is the same invariant as in
[Longest Substring Without Repeating Characters](LongestSubstringWithoutRepeatingCharacters.md#correctness-proof):
`L` is non-decreasing, and the shrink loop advances `l` exactly while
`cards[r]` is duplicated. The window must start after `prev(r)` to exclude
the other copy of `cards[r]`, and it can't start before `L(r − 1)`, because
`cards[L(r)..r−1]` must also be duplicate-free. ∎

### Lemma 3 (Whatever is recorded is valid)

If the check fires at `r`, the value recorded is `r − prev(r) + 1`.

*Proof.* The check fires when `l > 0` and `cards[l − 1] == cards[r]`. Since
`[l, r]` is duplicate-free, `cards[r]` doesn't occur in `[l, r − 1]`, so the
nearest earlier copy is `l − 1`, i.e. `prev(r) = l − 1`. The recorded length
is `r − (l − 1) + 1 = r − prev(r) + 1`. ∎

### Theorem

Let `r*` be a right end that achieves the minimum in Lemma 1, chosen with the
smallest `r*` among ties, and let `i* = prev(r*)`.

The cards in `cards[i*+1..r*]` are all distinct. Otherwise there would be a
pair `cards[a] == cards[b]` with `i* < a < b ≤ r*`. If `b < r*`, then `b` is
an earlier right end whose window `[prev(b), b] ⊆ [a, b]` is strictly shorter
than `[i*, r*]`, which is a contradiction. If `b = r*`, then `prev(r*) ≥ a > i*`,
also a contradiction.

So `cards[i*+1..r*]` is duplicate-free, while `cards[i*..r*]` is not. By
definition, `L(r*) = i* + 1`, so after processing `r*`, `l − 1 = i*` and
`cards[l − 1] == cards[r*]`. The check fires and records the optimal length.

By Lemma 3, every other recorded value is the length of a real matching
window, so it is `≥` the optimum. Therefore `ans` ends up equal to the
optimum. If no matching pair exists, the check never fires (it needs
`cards[l − 1] == cards[r]`), `ans` stays `n + 1`, and the code returns `-1`. ∎

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each i, scan right to the first j with cards[j] == cards[i].
       Mostly-distinct input makes every scan run to the end.
       Time: O(n²)   Space: O(1)

           ▼  Observation: the shortest matching window is
              "duplicate-free window + the copy just dropped on its left".
              Maintain the duplicate-free window with two forward pointers.

Sliding Window ──────────────────────────────────────────────────────────────►
       Expand r, shrink l while freq[cards[r]] > 1,
       record r − l + 2 when cards[l − 1] == cards[r].
       Each card enters and leaves the map at most once.
       Time: O(n)   Space: O(n)

           ▼  Observation: only the nearest earlier copy of cards[r] matters
              (Lemma 1). Remember last-seen indices and skip the window.

Last-Seen Index ─────────────────────────────────────────────────────────────►
       ans = min(ans, r − last[cards[r]] + 1); last[cards[r]] = r.
       One pass, no inner loop, array instead of HashMap.
       Time: O(n)   Space: O(V)   ← Optimal time
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(1) | Every start, scan to the first matching card |
| Sliding Window | **O(n)** | **O(n)** | Keep a duplicate-free window; the card dropped at `l − 1` closes the pair |
| Last-Seen Index | **O(n)** | **O(V)** | Distance from each card to its previous copy |

> `V = 10^6 + 1` is the value range. The inner `while` in the sliding window
> does not make it O(n²): `l` is never reset, so across the whole run it
> advances at most `n` times in total.
