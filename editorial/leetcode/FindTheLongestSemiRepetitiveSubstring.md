# Find the Longest Semi-Repetitive Substring

## Problem Statement

You are given a digit string `s` (each character is `'0'`–`'9'`).

A string is called **semi-repetitive** if it has **at most one** pair of
adjacent equal characters. For example, `"0010"`, `"002020"`, `"0123"`,
`"2002"` and `"54944"` are semi-repetitive, while `"00101022"` (two pairs:
`00` and `22`) and `"1101234883"` (`11` and `88`) are not.

Return the length of the **longest semi-repetitive substring** of `s`.

A substring is a contiguous, non-empty sequence of characters within a string.

**Example:**
```
s = "52233"
"5223" has one adjacent pair (22) → semi-repetitive, length 4
"52233" has two pairs (22, 33)   → not semi-repetitive
Answer = 4

s = "5494"
No adjacent pairs at all → the whole string works
Answer = 4

s = "1111111"
Any substring of length 3 ("111") already has two pairs
Answer = 2
```

**Constraints:** `1 ≤ s.length ≤ 50`, `'0' ≤ s[i] ≤ '9'`.

---

## Core Insight: Count Pairs, Not Characters

A substring `s[l..r]` contains the adjacent pair `(i−1, i)` iff
`l ≤ i−1` and `i ≤ r`. So the condition "semi-repetitive" is really about
how many **pair boundaries** fall inside the window:

```
s = 5 2 2 3 3
     ^   ^
  pair at i=2 (s[1]==s[2]), pair at i=4 (s[3]==s[4])

window [0..3] "5223"  contains pair i=2           → 1 pair, ok
window [0..4] "52233" contains pairs i=2 and i=4  → 2 pairs, bad
window [2..4] "233"   contains pair i=4 only      → 1 pair, ok
```

And like every "at most K" condition, it is **closed under shrinking**:
dropping characters from either end can only remove pairs, never create
them. That's the monotonicity a variable-size sliding window needs.

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(1) Space

Fix every start `i`, extend `j` to the right, counting pairs as they appear,
and stop at the second one.

```java
@Override
public int longestSemiRepetitiveSubstring(String s) {
    int max = 1;
    for (int i = 0; i < s.length(); i++) {
        int cnt = 0;
        for (int j = i + 1; j < s.length(); j++) {
            if (s.charAt(j) == s.charAt(j - 1) && ++cnt > 1) {
                break;
            }
            max = Math.max(max, j - i + 1);
        }
    }
    return max;
}
```

With `n ≤ 50` this passes comfortably, but it re-scans the same characters
for every start: when moving from `i` to `i + 1`, we already knew how many
pairs `s[i+1..j−1]` had and threw that away.

---

### 2. Optimized Solution (Sliding Window) — O(n) Time, O(1) Space

**Key Insight.** Keep a window `[l, r]` that always contains at most one
pair, plus `cnt` = the number of pairs inside it.

- **Expand:** moving `r` forward adds exactly one new boundary, `(r−1, r)`.
  If `s[r] == s[r−1]`, that's a new pair → `cnt++`.
- **Shrink:** while `cnt > 1`, drop `s[l]`. Removing `s[l]` removes exactly
  one boundary, `(l, l+1)` — if `s[l] == s[l+1]` that was a pair → `cnt--`.
- **Record:** the window is valid again, so update the answer with `r − l + 1`.

```java
public interface FindTheLongestSemiRepetitiveSubstring {
    int longestSemiRepetitiveSubstring(String s);
}

class FindTheLongestSemiRepetitiveSubstringImpl implements FindTheLongestSemiRepetitiveSubstring {

    @Override
    public int longestSemiRepetitiveSubstring(String s) {
        int l = 0;
        int max = 1;
        int cnt = 0;


        for(int r=1; r<s.length(); r++){
            if(s.charAt(r) == s.charAt(r-1)){
                cnt++;
            }
            while(cnt > 1){
                if(s.charAt(l) == s.charAt(l+1)){
                    cnt--;
                }
                l++;
            }
            max = Math.max(max, r-l+1);
        }

        return max;
    }
}
```

**How it works:**
1. Start with the single-character window `[0, 0]`: `l = 0`, `cnt = 0`,
   `max = 1`.
2. For each `r` from `1`:
   - If `s[r] == s[r−1]`, a new pair entered the window → `cnt++`.
   - While `cnt > 1`, check whether the leftmost boundary `(l, l+1)` is a
     pair (if so `cnt--`), then advance `l`.
   - The window `[l, r]` now has at most one pair; update `max`.
3. Return `max`.

> **Why start `r` at 1 and `max` at 1?** A pair needs two characters, so the
> loop compares `s[r]` with `s[r−1]` and has nothing to do at `r = 0`. Any
> single character is trivially semi-repetitive, so `max = 1` covers
> `s.length() == 1`, where the loop never runs.

> **Why is `s.charAt(l+1)` safe?** The shrink loop only runs while
> `cnt ≥ 2`, which requires at least three characters in the window, so
> `l + 1 ≤ r` always holds.

---

### 3. Variant: Jump to the Last Pair — O(n) Time, O(1) Space

Since the limit is exactly one pair, we don't need to walk `l` forward one
step at a time. When a **second** pair appears at `r`, the new window must
exclude the **previous** pair. If that previous pair was `(last−1, last)`,
the smallest valid start is exactly `last`.

```java
@Override
public int longestSemiRepetitiveSubstring(String s) {
    int l = 0;
    int last = 0;   // index i of the most recent pair (s[i-1] == s[i]); 0 = none yet
    int max = 1;

    for (int r = 1; r < s.length(); r++) {
        if (s.charAt(r) == s.charAt(r - 1)) {
            l = last;   // cut off the previous pair (no-op if there wasn't one)
            last = r;
        }
        max = Math.max(max, r - l + 1);
    }
    return max;
}
```

Same complexity, but no inner loop. The counting version (Solution 2)
generalises directly to "at most `k` pairs", which is why it's the default
template; this variant is specific to `k = 1`.

---

## Step-by-step Trace

```
s = "52233"   (window is [l, r], inclusive)

r=1  '2' vs '5'  no pair   cnt=0   l=0  window "52"    max=2
r=2  '2' vs '2'  pair      cnt=1   l=0  window "522"   max=3
r=3  '3' vs '2'  no pair   cnt=1   l=0  window "5223"  max=4
r=4  '3' vs '3'  pair      cnt=2 → shrink:
       l=0: s[0]='5' vs s[1]='2'  not a pair         l=1
       l=1: s[1]='2' vs s[2]='2'  pair → cnt=1       l=2
                                   l=2  window "233"   max=4

Answer: 4 ✓
```

```
s = "1111111"

r=1  pair  cnt=1                         l=0  window "11"  max=2
r=2  pair  cnt=2 → s[0]==s[1], cnt=1     l=1  window "11"  max=2
r=3  pair  cnt=2 → s[1]==s[2], cnt=1     l=2  window "11"  max=2
...  (every step drops exactly one pair and one character)

Answer: 2 ✓
```

```
s = "5494"

r=1..3  no adjacent pair ever, cnt stays 0, l stays 0, window grows to "5494"

Answer: 4 ✓
```

---

## Correctness Proof

Call a window **good** if it contains at most one adjacent equal pair. For
each right end `r`, let `L(r)` be the **smallest** `l` such that `s[l..r]` is
good. (It exists: `s[r..r]` has no pairs.)

### Lemma 1 (Good starts form a suffix)

For fixed `r`, `s[l..r]` is good **iff** `l ≥ L(r)`.

*Proof.* For `l ≥ L(r)`, the pairs of `s[l..r]` are a subset of the pairs of
`s[L(r)..r]`, so there is at most one. For `l < L(r)` it is bad by
minimality. ∎

So the longest good substring ending at `r` has length `r − L(r) + 1`, and
the answer is `maxᵣ (r − L(r) + 1)`.

### Lemma 2 (`L(r)` is non-decreasing)

`L(r) ≤ L(r + 1)`.

*Proof.* `s[L(r+1)..r]` is a substring of the good `s[L(r+1)..r+1]`, so it
is good, hence `L(r) ≤ L(r+1)`. ∎

### Theorem

**Invariant.** After processing `r`, `l = L(r)` and `cnt` equals the number
of pairs inside `s[l..r]`.

- **Expanding.** Going from `s[l..r−1]` to `s[l..r]` adds exactly the boundary
  `(r−1, r)`, so the `cnt++` check keeps `cnt` exact. By Lemma 2,
  `l = L(r−1) ≤ L(r)`, so `l` never needs to move left.
- **Shrinking.** Going from `s[l..r]` to `s[l+1..r]` removes exactly the
  boundary `(l, l+1)`, so the `cnt--` check keeps `cnt` exact. The loop runs
  only while the window is bad, i.e. `l < L(r)` (Lemma 1), so it never moves
  `l` past `L(r)`.
- **After shrinking.** The loop exits when `cnt ≤ 1`, i.e. `l ≥ L(r)`.
  Combined with `l ≤ L(r)`, we get `l = L(r)`.

So at every `r` the code records `r − L(r) + 1`, and `max` ends as the
maximum over all `r` (with `max = 1` covering `r = 0`). ∎

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each start i, extend j counting pairs; break at the second pair.
       Re-scans overlapping ranges for every start.
       Time: O(n²)   Space: O(1)

           ▼  Observation: removing characters never creates a pair (Lemma 1),
              and the best start for each end only moves right (Lemma 2).
              Track the pair count incrementally with two forward pointers.

Sliding Window ──────────────────────────────────────────────────────────────►
       Expand r (+1 if s[r]==s[r-1]), shrink l while cnt > 1
       (−1 if s[l]==s[l+1]), record r − l + 1.
       Time: O(n)   Space: O(1)   ← Optimal

           ▼  Observation: with k = 1, the valid start after a new pair is
              always "just after the previous pair" — jump there directly.

Jump to Last Pair ───────────────────────────────────────────────────────────►
       On a new pair at r: l = last, last = r.
       Time: O(n)   Space: O(1)   (no inner loop, but only works for k = 1)
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(1) | Every start, extend until the second pair |
| Sliding Window | **O(n)** | **O(1)** | Count pairs; shrink while `cnt > 1` |
| Jump to Last Pair | **O(n)** | **O(1)** | On a new pair, set `l` to the previous pair's right index |

> The inner `while` does not make Solution 2 O(n²): `l` is never reset, so
> across the whole run it advances at most `n` times in total. Space is O(1)
> because we only track a count of pairs — no frequency map is needed, since
> a pair is determined by comparing two neighbouring characters.

---

## Related Problems

- [Length of Longest Subarray With at Most K Frequency](LengthOfLongestSubarrayWithAtMostKFrequency.md) — same "shrink while over the limit" template, with a frequency map instead of a pair count.
- [Longest Substring Without Repeating Characters](LongestSubstringWithoutRepeatingCharacters.md) — another "at most" window where the valid start only moves right.
