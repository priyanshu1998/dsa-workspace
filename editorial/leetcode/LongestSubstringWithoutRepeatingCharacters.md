# Longest Substring Without Repeating Characters

## Problem Statement

Given a string `s`, find the length of the **longest substring** that contains
**no repeating characters**.

A substring is a contiguous, non-empty sequence of characters within a string.

**Example:**
```
s = "abcabcbb"
"abc", "bca", "cab" are all unique, length 3
Any length-4 window repeats a character
Answer = 3

s = "bbbbb"
Every substring longer than 1 repeats 'b'
Answer = 1

s = "pwwkew"
"wke" is unique, length 3
"pwke" is a subsequence, not a substring → doesn't count
Answer = 3
```

**Constraints:** `0 ≤ s.length ≤ 5 · 10^4`, `s` consists of English letters,
digits, symbols and spaces.

---

## Core Insight: "Unique" Is Closed Under Shrinking

If a substring has no repeated characters, every substring **inside** it is
also repeat-free: removing characters can't create a duplicate.

```
"abc"   unique  →  "bc"    still unique
"abca"  repeats →  "abcab" still repeats (extending never fixes it)
```

This monotonicity is what a **variable-size sliding window** needs: when the
window gets a duplicate, shrinking from the left is the only way to fix it,
and the left positions we drop never need to be looked at again.

> This is the `k = 1` case of
> [Length of Longest Subarray With at Most K Frequency](LengthOfLongestSubarrayWithAtMostKFrequency.md):
> "no repeating characters" means "every character appears at most once".
> The code below is the same template with `> k` replaced by `> 1`.

---

## Solutions

### 1. Naive Solution: O(n²) Time, O(min(n, A)) Space

Fix every start `i`, extend `j` to the right while keeping a set of the
characters seen so far, and stop at the first duplicate.

```java
@Override
public int lengthOfLongestSubstring(String s) {
    int longest = 0;
    for (int i = 0; i < s.length(); i++) {
        Set<Character> seen = new HashSet<>();
        for (int j = i; j < s.length(); j++) {
            if (!seen.add(s.charAt(j))) {
                break;
            }
            longest = Math.max(longest, j - i + 1);
        }
    }
    return longest;
}
```

The `break` is valid because of the insight above: once `s[i..j]` has a
duplicate, every longer `s[i..j']` has it too.

**Drawback:** When moving from start `i` to `i + 1`, the set is thrown away
and rebuilt, even though `s[i+1..j-1]` was already known to be unique. The
inner loop runs at most `A + 1` times (`A` = alphabet size), so this is really
O(n · A). That's fast enough for this problem's limits, but it wastes work that
the sliding window keeps.

---

### 2. Optimized Solution (Sliding Window): O(n) Time, O(min(n, A)) Space

**Key Insight.** Keep a window `[l, r]` that never contains a repeated
character, along with the count of each character inside it.

- **Expand:** add `s[r]` to the window.
- **Shrink:** if that pushed `s[r]`'s count to 2, drop characters from the
  left until it is back to 1.
- **Record:** the window is unique again, so update the answer with `r − l + 1`.

Only `s[r]`'s count went up in this step, so **only `s[r]` can be
duplicated**. There's no need to scan the whole map to check the window.

```java
public interface LongestSubstringWithoutRepeatingCharacters {
    int lengthOfLongestSubstring(String s);
}

class LongestSubstringWithoutRepeatingCharactersImpl implements LongestSubstringWithoutRepeatingCharacters{

    private void add(Map<Character, Integer> freq, Character c){
        freq.put(c, freq.getOrDefault(c, 0)+1);
    }

    private void remove(Map<Character, Integer> freq, Character c){
        int v = freq.get(c);
        if(v == 1){
            freq.remove(c);
        }else{
            freq.put(c, v-1);
        }
    }

    @Override
    public int lengthOfLongestSubstring(String s) {
        int l = 0;
        Map<Character, Integer> freq = new HashMap<>();
        int longest = 0;

        for(int r=0; r<s.length(); r++){
            add(freq, s.charAt(r));

            while(freq.get(s.charAt(r)) > 1){
                remove(freq, s.charAt(l));
                l++;
            }

            longest = Math.max(longest, r-l+1);
        }

        return longest;
    }
}
```

**How it works:**
1. Start with an empty window: `l = 0`, `freq = {}`.
2. For each `r`:
   - Add `s[r]` to `freq`.
   - While `freq[s[r]] > 1`, remove `s[l]` from `freq` and advance `l`.
     The loop stops once `l` steps past the earlier copy of `s[r]`, because
     that removal brings its count back to 1.
   - The window `[l, r]` is now unique; update `longest`.
3. Return `longest`. An empty string skips the loop and returns `0`.

> **Since counts never exceed 1 after shrinking,** the map is really just a
> set: `freq.get(c) > 1` is the same as "`c` was already in the window before
> we added it". The frequency-map form is kept here because it is the same
> template that handles the general "at most `k`" version.

#### Alternative: the commented-out two-pointer loop

The source file also has an earlier version, commented out, that moves
exactly one pointer per iteration:

```java
while(l<s.length()){
    if(r<s.length() && !freq.containsKey(s.charAt(r))){
        add(freq, s.charAt(r));
        r++;
    }else{
        longest = Math.max(longest, r-l);
        remove(freq, s.charAt(l));
        l++;
    }
}
```

Here the window is `[l, r)` (half-open). It only records the answer in the
`else` branch, when the window **can't** grow any more. That's still correct
because once `r` hits the end of the string, every later iteration takes the
`else` branch, so the final window `[l, n)` gets recorded before `l` catches
up to `r`. It does the same O(n) work (at most `2n` iterations), but the
`for`-loop version is easier to reason about because it records after every
expansion and doesn't depend on that end-of-string detail.

---

### 3. Jump Optimization (Last-Seen Index): O(n) Time, O(A) Space

The sliding window moves `l` one step at a time until it passes the earlier
copy of `s[r]`. We can skip straight there if we remember **where** each
character was last seen.

```java
@Override
public int lengthOfLongestSubstring(String s) {
    int[] next = new int[128];   // next[c] = (last index of c) + 1, 0 if unseen
    int longest = 0;
    int l = 0;

    for (int r = 0; r < s.length(); r++) {
        char c = s.charAt(r);
        l = Math.max(l, next[c]);   // jump past the previous c, never move left
        longest = Math.max(longest, r - l + 1);
        next[c] = r + 1;
    }

    return longest;
}
```

- Storing `index + 1` means the default `0` already works as "unseen", so the
  array doesn't need to be filled with `-1`.
- **`Math.max` is essential.** The previous `c` might sit *left* of the
  current window, in which case it's no longer a conflict. For example, in
  `"abba"`, at the final `'a'` the window is `"b"` (`l = 2`), and `next['a'] = 1`
  would wrongly move `l` back to 1 without the `max`.
- A 128-entry array works because the input is ASCII (letters, digits,
  symbols, spaces).

This still has O(n) asymptotic cost, but it does a single pass with no inner
loop and no boxing of `Character` keys, so it is usually the fastest in
practice.

---

## Step-by-step Trace

```
s = "abcabcbb"   (sliding window, window is [l, r] inclusive)

r=0  add 'a'  freq{a:1}                       l=0  "a"     longest=1
r=1  add 'b'  freq{a:1, b:1}                  l=0  "ab"    longest=2
r=2  add 'c'  freq{a:1, b:1, c:1}             l=0  "abc"   longest=3
r=3  add 'a'  freq[a]=2 → shrink:
       remove s[0]='a'                        l=1  "bca"   longest=3
r=4  add 'b'  freq[b]=2 → shrink:
       remove s[1]='b'                        l=2  "cab"   longest=3
r=5  add 'c'  freq[c]=2 → shrink:
       remove s[2]='c'                        l=3  "abc"   longest=3
r=6  add 'b'  freq[b]=2 → shrink:
       remove s[3]='a'  (b still 2)           l=4
       remove s[4]='b'                        l=5  "cb"    longest=3
r=7  add 'b'  freq[b]=2 → shrink:
       remove s[5]='c'  (b still 2)           l=6
       remove s[6]='b'                        l=7  "b"     longest=3

Answer: 3 ✓
```

```
s = "pwwkew"

r=0  add 'p'                                  l=0  "p"     longest=1
r=1  add 'w'                                  l=0  "pw"    longest=2
r=2  add 'w'  freq[w]=2 → shrink:
       remove s[0]='p'  (w still 2)           l=1
       remove s[1]='w'                        l=2  "w"     longest=2
r=3  add 'k'                                  l=2  "wk"    longest=2
r=4  add 'e'                                  l=2  "wke"   longest=3
r=5  add 'w'  freq[w]=2 → shrink:
       remove s[2]='w'                        l=3  "kew"   longest=3

Answer: 3 ✓
```

```
s = "bbbbb"

r=0  add 'b'                                  l=0  "b"     longest=1
r=1..4  each time freq[b]=2 → remove s[l], l++ window stays "b"

Answer: 1 ✓
```

---

## Correctness Proof

For each right end `r`, let `L(r)` be the **smallest** `l` such that `s[l..r]`
has no repeated characters. (It always exists: `s[r..r]` is a single
character.)

### Lemma 1 (Valid starts form a suffix)

For fixed `r`, `s[l..r]` is unique **iff** `l ≥ L(r)`.

*Proof.* For `l ≥ L(r)`, `s[l..r]` is a substring of the unique `s[L(r)..r]`,
so it is unique. For `l < L(r)` it has a repeat by minimality. ∎

So the longest unique substring ending at `r` has length `r − L(r) + 1`, and
the answer is `maxᵣ (r − L(r) + 1)`.

### Lemma 2 (`L(r)` is non-decreasing)

`L(r) ≤ L(r + 1)`.

*Proof.* `s[L(r+1)..r]` is a substring of the unique `s[L(r+1)..r+1]`, so it
is unique, hence `L(r) ≤ L(r+1)`. ∎

### Theorem

**Invariant.** After processing `r`, `l = L(r)` and `freq` holds the exact
counts of `s[l..r]`.

- **Before shrinking.** By the invariant for `r − 1` and Lemma 2,
  `l = L(r−1) ≤ L(r)`, so `l` never needs to move left.
- **Shrinking.** `s[l..r−1]` was unique, and adding `s[r]` raised only
  `freq[s[r]]`. So the window has a repeat **iff** `freq[s[r]] > 1`, which is
  exactly the loop condition. While it has a repeat, `l < L(r)` by Lemma 1, so
  advancing `l` never skips past `L(r)`.
- **After shrinking.** The loop exits when the window is unique, i.e.
  `l ≥ L(r)`. Combined with `l ≤ L(r)`, we get `l = L(r)`.

So at every `r` the code records `r − L(r) + 1`, and `longest` ends up as the
maximum over all `r`. ∎

For the jump version, `L(r) = max(L(r−1), last[s[r]] + 1)`: the window must
start after the previous copy of `s[r]` and can't start before `L(r−1)` by
Lemma 2. That is exactly `l = Math.max(l, next[c])`.

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each start i, extend j with a fresh set; break on first duplicate.
       Rebuilds the set from scratch for every i.
       Time: O(n · A) ≤ O(n²)   Space: O(min(n, A))

           ▼  Observation: shrinking a unique window keeps it unique (Lemma 1),
              and the best start for each end only moves right (Lemma 2).
              So one map and two forward-only pointers are enough.

           ▼  Observation: after adding s[r], only s[r] can be duplicated,
              so checking validity is O(1) instead of scanning the map.

Sliding Window ──────────────────────────────────────────────────────────────►
       Expand r, shrink l while freq[s[r]] > 1, record r − l + 1.
       Each character enters and leaves the map at most once.
       Time: O(n)   Space: O(min(n, A))

           ▼  Observation: the shrink loop always stops just past the previous
              copy of s[r]. Store last-seen indices and jump there directly.

Last-Seen Index ─────────────────────────────────────────────────────────────►
       l = max(l, next[s[r]]); record r − l + 1; next[s[r]] = r + 1.
       One pass, no inner loop, array instead of HashMap.
       Time: O(n)   Space: O(A)   ← Optimal
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n · A) | O(min(n, A)) | Every start, extend until the first duplicate |
| Sliding Window | **O(n)** | **O(min(n, A))** | Shrink only while `freq[s[r]] > 1`; both pointers move forward |
| Last-Seen Index | **O(n)** | **O(A)** | Jump `l` past the previous copy of `s[r]` in one step |

> `A` is the alphabet size (128 for ASCII). The inner `while` in the sliding
> window does not make it O(n²): `l` is never reset, so across the whole run
> it advances at most `n` times in total.
