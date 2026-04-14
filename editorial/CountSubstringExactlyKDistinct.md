# Count Substrings with Exactly K Distinct Characters

## Problem Statement

Given a string `s` and an integer `k`, count the number of **contiguous substrings** that contain **exactly `k` distinct characters**.

**Example:**
```
s = "pqpqs",  k = 2

All substrings with exactly 2 distinct characters:
"pq"    [0,1]  → {p,q} ✓
"pqp"   [0,2]  → {p,q} ✓
"pqpq"  [0,3]  → {p,q} ✓
"qp"    [1,2]  → {q,p} ✓
"qpq"   [1,3]  → {q,p} ✓
"pq"    [2,3]  → {p,q} ✓
"qs"    [3,4]  → {q,s} ✓

Answer = 7
```

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(n) Space

The most direct approach: fix a left boundary `i`, then expand a right boundary `j` one step at a time while maintaining a frequency map of characters in the current window.

```java
class NaiveSolution implements CountExactlyKDistinct {
    @Override
    public long countSubstringExactlyKDistinct(String s, int k) {
        long count = 0;
        for (int i = 0; i < s.length(); i++) {
            Map<Character, Integer> map = new HashMap<>();
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                map.merge(c, 1, Integer::sum);
                if (map.size() == k) {
                    count++;
                } else if (map.size() > k) {
                    break; // adding more characters can only increase distinct count
                }
            }
        }
        return count;
    }
}
```

**How it works:**
- For each left index `i`, scan rightward and track character frequencies.
- When `map.size() == k`, the window `[i, j]` qualifies — increment `count`.
- Once `map.size() > k`, no further extension of this window can bring it back to exactly `k`, so break early.

**Complexity:**
- Time: O(n²) — in the worst case (e.g., all same characters), the inner loop never breaks early.
- Space: O(n) — the frequency map holds at most all distinct characters.

**Drawback:** For each new left boundary, the right boundary restarts from scratch. There is no reuse of work done for overlapping windows. The key question is: can we process all windows in a single pass?

---

### 2. Optimized Solution (At-Most-K Reduction + Sliding Window) — O(n) Time, O(1) Space

#### The Core Mathematical Insight

Counting substrings with **exactly k** distinct characters is hard to do directly because the window can become valid (exactly k), then invalid (more than k), then potentially valid again as the left boundary moves — meaning no simple monotone property holds.

The key reduction is:

```
count(exactly k distinct) = count(at most k distinct) − count(at most k−1 distinct)
```

The "at most k" version **is** amenable to a sliding window, because the window only stops being valid when we add too many distinct characters, and we can restore validity by shrinking from the left. The window's validity is **monotone**: adding elements can only make it worse, and removing from the left can only make it better.

#### Counting "At Most K Distinct" Substrings

Use a variable-length sliding window `[l, r]` with a frequency map:

1. Expand `r` one step, adding `s[r]` to the map.
2. If `map.size() > k`, shrink from the left by removing `s[l]` (decrementing its count; removing the key when count reaches zero) and advancing `l`, until `map.size() <= k`.
3. At this point, **every substring ending at `r` with a left boundary anywhere between `l` and `r` has at most k distinct characters**. There are exactly `r − l + 1` such substrings, so add that to the count.

```java
private long countAtMostKDistinct(String s, int k) {
    long count = 0;
    int l = 0, r = 0;
    var map = new HashMap<Character, Integer>();

    while (r < s.length()) {
        extendLeadingEnd(map, s.charAt(r));       // add s[r] to window
        while (map.size() > k) {
            shrinkTailingEnd(map, s.charAt(l));   // remove s[l] from window
            l++;
        }
        count += r - l + 1;                       // all subarrays ending at r are valid
        r++;
    }
    return count;
}
```

#### The Full Solution

```java
class OptimizedSolution implements CountExactlyKDistinct {

    private void extendLeadingEnd(Map<Character, Integer> map, char c) {
        if (map.containsKey(c)) {
            map.put(c, map.get(c) + 1);
        } else {
            map.put(c, 1);
        }
    }

    private void shrinkTailingEnd(Map<Character, Integer> map, char c) {
        if (map.get(c) == 1) {
            map.remove(c);
        } else {
            map.put(c, map.get(c) - 1);
        }
    }

    @Override
    public long countSubstringExactlyKDistinct(String s, int k) {
        return countAtMostKDistinct(s, k) - countAtMostKDistinct(s, k - 1);
    }

    private long countAtMostKDistinct(String s, int k) {
        long count = 0;
        int l = 0, r = 0;
        var map = new HashMap<Character, Integer>();

        while (r < s.length()) {
            extendLeadingEnd(map, s.charAt(r));
            while (map.size() > k) {
                shrinkTailingEnd(map, s.charAt(l));
                l++;
            }
            count += r - l + 1;
            r++;
        }
        return count;
    }
}
```

**Complexity:**
- Time: O(n) — each character is added to the map exactly once (when `r` reaches it) and removed at most once (when `l` passes it). Both pointers traverse the array at most once each → O(2n) = O(n).
- Space: O(1) — the map holds at most `k` entries (bounded by the alphabet size, a constant for typical inputs).

---

## Correctness Proof

### Part 1 — `countAtMostKDistinct` is correct

**Claim:** After the inner `while` loop exits (and before `r` advances), the window `[l, r]` satisfies `map.size() <= k`, and `l` is the **smallest possible** index such that this holds.

**Proof by invariant:**
- When we first add `s[r]`, the window may gain a new distinct character.
- The inner `while` loop removes characters from the left until `map.size() <= k`. Each removal step is necessary — if removing `s[l]` still left `map.size() > k`, we correctly continue shrinking.
- Because `l` only advances, and we stop as soon as `map.size() <= k`, `l` is indeed the leftmost valid left boundary for window ending at `r`.
- Every substring `[l..r], [l+1..r], ..., [r..r]` contains a subset of characters from `[l..r]`, so all have ≤ `map.size()` ≤ `k` distinct characters → all are valid.
- Every substring `[l-1..r], [l-2..r], ...` would include the character that was just removed at position `l-1`, which pushed the distinct count over `k` — so none of those are valid.
- Therefore, adding `r - l + 1` to `count` correctly accounts for all valid substrings ending at `r`, and each substring is counted exactly once (once for each value of `r` that equals its right endpoint).

### Part 2 — The At-Most-K Reduction is correct

**Claim:** `count(exactly k) = atMost(k) − atMost(k−1)`.

**Proof:**
Let `A(k)` denote the set of all substrings with **at most** `k` distinct characters and `E(k)` those with **exactly** `k` distinct characters. Then:

```
A(k)   = E(0) ∪ E(1) ∪ ... ∪ E(k)
A(k−1) = E(0) ∪ E(1) ∪ ... ∪ E(k−1)
```

These unions are disjoint by definition (a substring cannot have two different numbers of distinct characters simultaneously). Therefore:

```
A(k) − A(k−1) = E(k)
|A(k)| − |A(k−1)| = |E(k)|
countAtMostKDistinct(k) − countAtMostKDistinct(k−1) = countExactlyKDistinct(k)
```

The subtraction of counts directly gives the answer. ∎

---

## Optimization Journey

```
Naive (O(n²))  ──────────────────────────────────────────────────────────────►
       Fix each left boundary i; scan right maintaining a frequency map.
       No reuse of work between different left boundaries.
       Time: O(n²)   Space: O(n)

           ▼  Observation: "exactly k" = "at most k" − "at most k−1"
              The "at most k" variant has a monotone validity property
              that enables a sliding window.

Variable Sliding Window (O(n))  ─────────────────────────────────────────────►
       Two pointers l and r share a single frequency map.
       r always advances (expand); l only advances to restore validity.
       Each character is inserted once (r) and removed at most once (l)
       → O(2n) = O(n) total map operations.
       countAtMostKDistinct called twice: O(2n) = O(n) total.
       Time: O(n)    Space: O(1)   ← Best possible
```

### Why "Exactly K" Alone Resists a Direct Sliding Window

The "at most k" condition is **monotone**: if `[l, r]` is valid, so is every sub-window `[l', r]` with `l' > l`. This allows a single left pointer `l` to track the boundary.

"Exactly k" is **not monotone**: shrinking a window that has exactly k distinct characters can either keep it at k (if the removed character appeared more than once) or drop it below k. This non-monotonicity means a single sliding window cannot efficiently track the exact count. The reduction to two at-most problems restores the monotone structure.

---

## Step-by-step Trace

```
s = "pqpqs",  k = 2

──── countAtMostKDistinct(s, k=2) ────

r=0 s[r]='p': map={p:1}          | l=0 | size=1 ≤ 2 | count += 0-0+1 = 1  | total=1
r=1 s[r]='q': map={p:1, q:1}     | l=0 | size=2 ≤ 2 | count += 1-0+1 = 2  | total=3
r=2 s[r]='p': map={p:2, q:1}     | l=0 | size=2 ≤ 2 | count += 2-0+1 = 3  | total=6
r=3 s[r]='q': map={p:2, q:2}     | l=0 | size=2 ≤ 2 | count += 3-0+1 = 4  | total=10
r=4 s[r]='s': map={p:2,q:2,s:1}  | l=0 | size=3 > 2 → shrink:
     remove s[0]='p': map={p:1,q:2,s:1} l=1  size=3 > 2 → continue
     remove s[1]='q': map={p:1,q:1,s:1} l=2  size=3 > 2 → continue
     remove s[2]='p': map={q:1,s:1}     l=3  size=2 ≤ 2 → stop
                                              count += 4-3+1 = 2  | total=12

atMost(2) = 12

──── countAtMostKDistinct(s, k=1) ────

r=0 s[r]='p': map={p:1} | l=0 | size=1 ≤ 1 | count += 1 = 1  | total=1
r=1 s[r]='q': map={p:1,q:1} size=2 > 1 → remove s[0]='p': map={q:1} l=1
                                              count += 1-1+1 = 1  | total=2
r=2 s[r]='p': map={q:1,p:1} size=2 > 1 → remove s[1]='q': map={p:1} l=2
                                              count += 1  | total=3
r=3 s[r]='q': map={p:1,q:1} size=2 > 1 → remove s[2]='p': map={q:1} l=3
                                              count += 1  | total=4
r=4 s[r]='s': map={q:1,s:1} size=2 > 1 → remove s[3]='q': map={s:1} l=4
                                              count += 1  | total=5

atMost(1) = 5

──── Result ────

exactly(2) = atMost(2) − atMost(1) = 12 − 5 = 7  ✓
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n²) | O(n) | Restarts from each left boundary; no cross-window reuse |
| Sliding Window (At-Most-K) | O(n) | O(1) | Each character processed at most twice across two passes |

> **Best solution:** `OptimizedSolution` — linear time via the "at most k" reduction, with each `countAtMostKDistinct` call running a single O(n) sliding window pass.

