# Count of Substrings Containing Every Vowel and K Consonants II

## Problem Statement

You are given a string `word` and a non-negative integer `k`. Return the
number of substrings of `word` that contain **every vowel** (`a`, `e`, `i`,
`o`, `u`) **at least once** and **exactly `k` consonants**.

**Example:**
```
word = "aeioqq", k = 1
No substring has all 5 vowels (there is no 'u').
Answer = 0

word = "aeiou", k = 0
The whole string has every vowel and 0 consonants.
Answer = 1

word = "ieaouqqieaouqq", k = 1
Answer = 3
```

**Constraints:** `5 ≤ word.length ≤ 2 · 10^5`, `word` consists only of
lowercase English letters, `0 ≤ k ≤ word.length - 5`.

---

## Building Block — "Exactly K" via Two "At Least K" Counts

Counting substrings with **exactly** `k` consonants (plus the all-vowels
requirement) is awkward to slide a window on directly: as the window grows,
the consonant count only ever increases, so "exactly `k`" is not monotonic in
the window's extent — but **"at least `k`" is**. This is the standard trick:

```
exactly(k) = atLeast(k) - atLeast(k + 1)
```

`atLeast(k)` counts substrings with every vowel present and **at least** `k`
consonants (the extra consonants beyond `k` don't matter). Subtracting
`atLeast(k + 1)` removes every substring counted that actually has `k + 1` or
more consonants, leaving exactly those with `k` consonants. Each `atLeast`
query is a textbook monotonic sliding window.

---

## Solution — Sliding Window on `atLeast(k)`

**Key Insight.** Fix the right end `r`. For a window to be valid for
`atLeast(k)` it must contain all 5 vowel types and at least `k` consonants.
Both conditions only get *easier* to satisfy as the left end `l` decreases
(shrinking `l` only adds characters to the window), so validity is
**monotonic**: if `word[i..r]` is valid, so is every `word[i'..r]` with
`i' ≤ i`. Hence the valid starts for a fixed `r` form a **prefix**
`0, 1, …, l − 1`, where `l` is the smallest start that makes the window
*invalid*. Exactly `l` valid substrings end at `r`.

```java
private long countOfSubstringsAtLeastK(String word, int k) {
    Map<Character, Integer> vowels = new HashMap<>();
    int consonants = 0;
    int l = 0;
    int ways = 0;

    for (int r = 0; r < word.length(); r++) {
        if (isVowel(word.charAt(r))) {
            add(vowels, word.charAt(r));
        } else {
            consonants++;
        }

        // shrink while the window already satisfies both conditions
        while (vowels.size() == 5 && consonants >= k) {
            if (isVowel(word.charAt(l))) {
                remove(vowels, word.charAt(l));
            } else {
                consonants--;
            }
            l++;
        }

        ways += l;
    }

    return ways;
}

@Override
public long countOfSubstrings(String word, int k) {
    return countOfSubstringsAtLeastK(word, k) - countOfSubstringsAtLeastK(word, k + 1);
}
```

**How it works:**
1. `vowels` is a frequency map of the distinct vowel characters currently in
   the window `word[l..r]`; `vowels.size() == 5` means all five are present.
   `consonants` is the count of consonants in the window.
2. For each `r`: **expand** by adding `word[r]` to the appropriate counter.
3. **Shrink** while the window at `[l, r]` is already valid
   (`vowels.size() == 5 && consonants >= k`): drop `word[l]` and advance
   `l`. This pushes `l` as far right as possible while the window `[l, r]`
   stays valid, so `l` lands one past the last valid start — i.e. `word[l..r]`
   is the first *invalid* window, and every `word[i..r]` with `i < l` is valid.
4. **Count:** add `l`, the number of valid substrings ending at `r`.
5. Call this twice, for `k` and `k + 1`, and subtract.

> **Why `vowels.size() == 5` and not checking each vowel individually?** A
> `HashMap` only ever holds keys with a positive count (the `remove` helper
> deletes a key when its count hits 0), so its size *is* exactly the number
> of distinct vowels present with count ≥ 1. Checking `size() == 5` is
> equivalent to, but cheaper than, checking all five vowels.

> **Why does the shrink loop terminate before deleting past a valid
> window?** The condition `vowels.size() == 5 && consonants >= k` is checked
> *before* each removal, so as soon as removing `word[l]` would break either
> condition, the loop stops — it never removes the element whose presence
> was necessary to stay valid.

> **Why is `k = 0` safe?** `consonants >= 0` is always true, so the loop
> shrinks down to the smallest window (by left end) that still has all 5
> vowels, for every `r`. No special-casing is needed.

---

## Step-by-step Trace

```
word = "ieaouqqieaouqq", k = 1  (indices: i e a o u q q i e a o  u  q  q
                                           0 1 2 3 4 5 6 7 8 9 10 11 12 13)

atLeast(1): shrink while all 5 vowels present AND consonants >= 1
```

Running the window to completion (each row below is one iteration of `r`,
after any shrinking):

| r | char | window | vowels present | consonants | l | contribution |
|---|------|--------|-----------------|------------|---|---------------|
| 0 | i | - | {i} | 0 | 0 | 0 |
| 1 | e | - | {e,i} | 0 | 0 | 0 |
| 2 | a | - | {a,e,i} | 0 | 0 | 0 |
| 3 | o | - | {a,e,i,o} | 0 | 0 | 0 |
| 4 | u | - | {a,e,i,o,u} | 0 | 0 | 0 |
| 5 | q | remove i | {a,e,o,u} | 1 | 1 | 1 |
| 6 | q | - | {a,e,o,u} | 2 | 1 | 2 |
| 7 | i | remove e | {a,i,o,u} | 2 | 2 | 4 |
| 8 | e | remove a | {e,i,o,u} | 2 | 3 | 7 |
| 9 | a | remove o | {a,e,i,u} | 2 | 4 | 11 |
| 10 | o | remove u | {a,e,i,o} | 2 | 5 | 16 |
| 11 | u | remove q,q | {a,e,i,o,u} | 0 | 7 | 23 |
| 12 | q | remove i | {a,e,o,u} | 1 | 8 | 31 |
| 13 | q | - | {a,e,o,u} | 2 | 8 | 39 |

`atLeast(1) = 39`. Running the same window with `k = 2` gives `atLeast(2) =
36` (the difference is that windows are kept one character larger, since
needing 2 consonants shrinks less aggressively at `r = 5, 6, 11, 12`).

```
countOfSubstrings = atLeast(1) - atLeast(2) = 39 - 36 = 3  ✓
```

```
word = "aeiou", k = 0

r=0..3: fewer than 5 vowel types seen so far → l=0, contribution 0 each
r=4  'u': vowels={a,e,i,o,u} (size=5), cons=0 >= 0 → shrink once:
            remove 'a' → size=4, stop                     l=1   contribution 1
atLeast(0) = 0+0+0+0+1 = 1

atLeast(1): the string has 0 consonants total, so `consonants >= 1` is never
            true → the window never shrinks and never reaches size 5 while
            satisfying the consonant bound at l → atLeast(1) = 0

countOfSubstrings = atLeast(0) - atLeast(1) = 1 - 0 = 1  ✓
```

---

## Correctness Proof

Let `n = word.length`. For a fixed `k`, let `V(i, r)` be true iff
`word[i..r]` contains all 5 vowels, and `C(i, r)` be the consonant count of
`word[i..r]`. Define validity as `Valid(i, r) ≡ V(i, r) ∧ C(i, r) ≥ k`. For
each `r`, let `s(r)` = the smallest `i ∈ [0, r+1]` with `¬Valid(i, r)` (such
an `i` always exists: `i = r + 1` gives the empty window, which is not
valid for `k ≥ 1`, and even for `k = 0` an empty window fails `V`). Let
`s(-1) = 0`.

### Lemma 1 (Valid starts form a prefix)

For fixed `r`, `word[i..r]` is valid **iff** `i < s(r)`.

*Proof.* Shrinking `i` (i.e. decreasing it) only adds characters to the
window, so both `V(i, r)` and `C(i, r)` are monotonic: if `word[i..r]` has
all vowels / `≥ k` consonants, so does `word[i'..r]` for every `i' ≤ i`.
Hence `Valid(i, r)` implies `Valid(i', r)` for all `i' ≤ i`. By minimality of
`s(r)`, every `i < s(r)` is valid and `i = s(r)` (if `≤ r`) is not, and by the
monotonicity above no `i > s(r)` can be valid either while `i < s(r)` fails
to be. ∎

So exactly `s(r)` valid substrings end at `r`, and `atLeast(k) = Σᵣ s(r)`.

### Lemma 2 (`s(r)` is non-decreasing)

`s(r) ≤ s(r+1)`.

*Proof.* Appending a character to the right only adds a vowel occurrence or
a consonant, so `V(i, r) ⟹ V(i, r+1)` and `C(i, r) ≤ C(i, r+1)`. Thus
`Valid(i, r) ⟹ Valid(i, r+1)`, so every `i < s(r)` remains valid at `r+1`,
giving `s(r+1) ≥ s(r)`. ∎

### Theorem

**Invariant.** At the start of iteration `r`, `vowels` is the frequency map
of vowels in `word[l..r-1]`, `consonants = C(l, r-1)`, and `l = s(r-1)`.

- **Initialization.** `r = 0`, `l = 0 = s(-1)`, window empty, `consonants = 0`.
- **Expansion.** Adding `word[r]` updates `vowels`/`consonants` to exactly
  match `word[l..r]`.
- **Shrinking.** By Lemma 2, `l = s(r-1) ≤ s(r)`. By Lemma 1, every `i` with
  `l ≤ i < s(r)` is valid at `r`, and `i = s(r)` is not. The loop removes
  `word[l]` exactly while `Valid(l, r)` holds (`vowels.size()==5 &&
  consonants>=k`), each removal correctly updating the counters to reflect
  `word[l+1..r]`. It therefore stops precisely when `l = s(r)`.
- **Counting.** The code adds `l = s(r)`, matching Lemma 1.
- **Maintenance.** `vowels`/`consonants` now reflect `word[s(r)..r]`, the
  invariant for `r + 1`.

Summing contributions gives `atLeast(k) = Σᵣ s(r)`, correctly computed. ∎

### Combining for "exactly k"

Let `Exactly(k)` = number of substrings with all 5 vowels and consonant count
`= k`. Since `atLeast(k)` counts substrings with consonant count `≥ k` (and
all vowels), and `atLeast(k+1)` counts the subset of those with count
`≥ k + 1`:

```
atLeast(k) - atLeast(k+1) = #{substrings : all vowels ∧ count ≥ k}
                           - #{substrings : all vowels ∧ count ≥ k+1}
                           = #{substrings : all vowels ∧ count = k}
                           = Exactly(k)
```

which is exactly `countOfSubstrings`. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `countOfSubstringsAtLeastK` (one pass, `l`/`r` each move forward ≤ n times) | O(n) | O(1) (at most 5 vowel keys) |
| Two calls (`k` and `k+1`) | O(n) | O(1) |
| **Overall** | **O(n)** | **O(1)** |

> The inner `while` does not make a single call O(n²): `l` never resets, so
> across the whole scan it advances at most `n` times in total.
