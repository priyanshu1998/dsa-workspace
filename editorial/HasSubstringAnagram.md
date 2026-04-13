# Has Substring Anagram

## Problem Statement

Given two strings `s` and `anagram`, return `true` if any contiguous substring of `s` with the same length as `anagram` is an anagram of `anagram`, and `false` otherwise.

Two strings are **anagrams** of each other if one can be formed by rearranging the characters of the other (i.e., they have identical character frequency maps).

**Example:**
```
s = "odicf", anagram = "doc"
Substrings of length 3: "odi", "dic", "icf"
"dic" is an anagram of "doc"  →  return true

s = "odicf", anagram = "xyz"
No substring of length 3 matches  →  return false
```

---

## Core Insight: Anagram ↔ Equal Frequency Maps

Two strings of the same length are anagrams if and only if their **character frequency maps are identical**.

```
"doc"  →  { d:1, o:1, c:1 }
"cod"  →  { c:1, o:1, d:1 }   ← same map  ✓ anagram

"dic"  →  { d:1, i:1, c:1 }
"doc"  →  { d:1, o:1, c:1 }   ← different ✗ not an anagram
```

All three solutions are built on this idea — they differ only in **how efficiently they compare the frequency maps** as the window slides along `s`.

---

## Solutions

### 1. Naive Solution — O(n·k) Time, O(k) Space

The simplest approach: for every window of length `k`, build its frequency map from scratch and compare it against the anagram's frequency map.

```java
class NaiveSolution implements HasSubstringAnagram {

    private boolean compareKeysAndValues(Map<Character, Integer> map1,
                                         Map<Character, Integer> map2) {
        if (!map1.equals(map2)) return false;

        for (var entry : map1.entrySet()) {
            if (!map2.getOrDefault(entry.getKey(), 0).equals(entry.getValue()))
                return false;
        }
        return true;
    }

    private boolean compareAnagram(String substring, String anagram) {
        Map<Character, Integer> freq1 = new HashMap<>();
        Map<Character, Integer> freq2 = new HashMap<>();

        for (char c : substring.toCharArray())
            freq1.put(c, freq1.getOrDefault(c, 0) + 1);

        for (char c : anagram.toCharArray())
            freq2.put(c, freq2.getOrDefault(c, 0) + 1);

        return compareKeysAndValues(freq1, freq2);
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();
        for (int i = 0; i <= s.length() - k; i++) {
            if (compareAnagram(s.substring(i, i + k), anagram))
                return true;
        }
        return false;
    }
}
```

**How it works:**
1. Slide a window of size `k` across `s` — there are `n - k + 1` positions.
2. For each position, build a frequency map for both the current window and `anagram` (`O(k)` each).
3. Compare the two maps (`O(k)`).
4. Return `true` immediately on the first match.

**Complexity:**
- Time: **O(n·k)** — building maps and comparing costs O(k) per window
- Space: **O(k)** — two frequency maps of size at most `k`

**Drawback:** The anagram's frequency map is rebuilt on every iteration, even though it never changes. The window map is also rebuilt entirely, even though it shares `k-1` characters with the previous window.

---

### 2. CompareOptimized — O(n·k) Time, O(k) Space

**Key Insight:** The anagram's frequency map is constant — build it **once** before the loop. Reuse a copy of it to check each window using a consume-and-check strategy instead of building a second map.

```java
class CompareOptimized implements HasSubstringAnagram {

    Map<Character, Integer> freq = new HashMap<>();

    // Consume window characters from a copy of the anagram freq map.
    // If every character can be "spent" without hitting zero, it's an anagram.
    private boolean optimizedCompareAnagram(String subString,
                                            Map<Character, Integer> freq2) {
        for (char c : subString.toCharArray()) {
            var count = freq2.getOrDefault(c, 0);
            if (count == 0) return false;   // character not needed or already spent
            freq2.put(c, count - 1);
        }
        return true;
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();

        // Build anagram freq map once
        for (char c : anagram.toCharArray())
            freq.put(c, freq.getOrDefault(c, 0) + 1);

        for (int i = 0; i <= s.length() - k; i++) {
            var substring = s.substring(i, i + k);
            var anagramFreq = new HashMap<>(freq);   // cheap copy
            if (optimizedCompareAnagram(substring, anagramFreq))
                return true;
        }
        return false;
    }
}
```

**How it works:**
1. Build the anagram's frequency map **once** in O(k).
2. For each window, make a **shallow copy** of that map and traverse the window's characters:
   - If a character has remaining count → decrement it (character "matched").
   - If a character has count 0 → it is either absent from the anagram or already fully consumed → not an anagram, stop early.
3. If all characters are consumed without hitting zero, the window is an anagram.

**Why this comparison is correct:**
The window and anagram have the same length `k`. If every character in the window can be "spent" from the anagram's budget without going negative, both the types and counts must match exactly.

**Complexity:**
- Time: **O(n·k)** — the window is still traversed fully for each position (in the worst case)
- Space: **O(k)** — one frequency map plus a copy per window

**Improvement over Naive:** The anagram's map is built only once, and the comparison short-circuits on the first mismatch. In practice this is faster, but the worst-case asymptotic complexity is unchanged because we still traverse up to `k` characters per window.

---

### 3. OptimizedSolution (Sliding Window + Mismatch Counter) — O(n) Time, O(k) Space

**Key Insight:** When the window slides one step to the right:
- Only **one character leaves** (the leftmost, `popped`).
- Only **one character enters** (the new rightmost, `added`).

Instead of recomputing the entire frequency map, maintain a **mismatch counter**: the number of anagram characters whose required count is not currently satisfied in the window. When mismatches reach 0, the window is an anagram.

```java
class OptimizedSolution implements HasSubstringAnagram {

    Map<Character, Integer> anagramFreq = new HashMap<>();
    int mismatches = 0;

    // Update the mismatch count for a single slide step.
    private void compareNext(char popped, char added, Map<Character, Integer> freq) {
        // --- Handle the character leaving the window ---
        if (anagramFreq.containsKey(popped)) {
            int cnt = freq.getOrDefault(popped, 0);
            // Was exactly right → now one short → new mismatch
            if (cnt - anagramFreq.get(popped) == 0) mismatches += 1;
            // Was one too many → now exactly right → mismatch resolved
            else if (cnt - anagramFreq.get(popped) == 1) mismatches -= 1;
            freq.put(popped, cnt - 1);
        }

        // --- Handle the character entering the window ---
        if (anagramFreq.containsKey(added)) {
            int cnt = freq.getOrDefault(added, 0);
            // Was one short → now exactly right → mismatch resolved
            if (anagramFreq.get(added) - cnt == 1) mismatches -= 1;
            // Was exactly right → now one too many → new mismatch
            else if (anagramFreq.get(added) - cnt == 0) mismatches += 1;
            freq.put(added, cnt + 1);
        }
    }

    // Count mismatches for the initial window.
    private boolean compareKeysAndValues(Map<Character, Integer> windowFreq,
                                         Map<Character, Integer> anagramFreq) {
        for (var entry : anagramFreq.entrySet()) {
            if (!windowFreq.getOrDefault(entry.getKey(), 0).equals(entry.getValue()))
                mismatches += 1;
        }
        return mismatches == 0;
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();

        for (char c : anagram.toCharArray())
            anagramFreq.put(c, anagramFreq.getOrDefault(c, 0) + 1);

        // Check the first window
        if (compareAnagram(s.substring(0, k), anagram))
            return true;

        // Build the frequency map for the first window
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.substring(0, k).toCharArray())
            freq.put(c, freq.getOrDefault(c, 0) + 1);

        // Slide
        int l = 0, r = k;
        while (r < s.length()) {
            compareNext(s.charAt(l), s.charAt(r), freq);
            if (mismatches == 0) return true;
            l++;
            r++;
        }

        return false;
    }
}
```

**How the mismatch counter works:**

`mismatches` = number of characters in the anagram whose count in the current window is **not** equal to the required count.

When the window slides (one char `popped`, one char `added`):

| Event | Condition | Effect on `mismatches` |
|-------|-----------|------------------------|
| `popped` leaves | `windowFreq[popped] == anagramFreq[popped]` | Was satisfied → now under → **+1** |
| `popped` leaves | `windowFreq[popped] == anagramFreq[popped] + 1` | Was over by 1 → now satisfied → **−1** |
| `added` enters  | `windowFreq[added] == anagramFreq[added] - 1` | Was under by 1 → now satisfied → **−1** |
| `added` enters  | `windowFreq[added] == anagramFreq[added]` | Was satisfied → now over → **+1** |
| All other cases | difference changes but not through the "boundary" | no change |

Characters **not in the anagram** are ignored entirely — they can never satisfy or break anagram equality.

**Why this is O(n):**
Each slide does a **fixed O(1)** update (at most 2 map lookups and 2 mismatches adjustments), regardless of window size. The `n - k` slides total O(n).

**Complexity:**
- Time: **O(n)** — O(k) for initial setup, O(1) per slide
- Space: **O(k)** — one map for the anagram and one for the current window

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For every window, rebuild both frequency maps from scratch (O(k) each).
       Anagram map is rebuilt n-k+1 times even though it never changes.
       Wasted work: O(k) per window → O(n·k) total.

           ▼  Observation: the anagram map is constant — build it once

CompareOptimized ────────────────────────────────────────────────────────────►
       Build anagram map once before the loop. Per window: copy that map and
       consume the window's characters against it (short-circuits on mismatch).
       Still O(n·k) worst-case, but eliminates one redundant map build per
       window and exits early on mismatches.

           ▼  Observation: consecutive windows share k-1 characters —
              only 1 character leaves and 1 enters on each slide

Sliding Window + Mismatch Counter ──────────────────────────────────────────►
       Maintain the window's frequency map incrementally. Track only a single
       integer (mismatches) that summarises whether the window is an anagram.
       Each slide: O(1) update to the map and the counter.
       Time: O(n)   Space: O(k)   ← Best possible
```

### Step-by-step derivation

| Window | Naive / CompareOptimized | OptimizedSolution |
|--------|--------------------------|-------------------|
| `s[0..k-1]` | Build full freq map (O(k)) | Build full freq map once (O(k)) |
| `s[1..k]`   | Rebuild full map (O(k)) | Remove `s[0]`, add `s[k]` (O(1)) |
| `s[2..k+1]` | Rebuild full map (O(k)) | Remove `s[1]`, add `s[k+1]` (O(1)) |
| `s[i..i+k-1]` | Rebuild full map (O(k)) | Remove `s[i-1]`, add `s[i+k-1]` (O(1)) |

Each transition drops from **O(k) operations** to **O(1) operations** — this is why the overall complexity collapses from O(n·k) to O(n).

---

## Worked Example

```
s = "cbaebacd",  anagram = "abc"   (k = 3)
anagramFreq = { a:1, b:1, c:1 }

Initial window "cba":
  windowFreq = { c:1, b:1, a:1 }  →  mismatches = 0  ✓ RETURN TRUE
```

```
s = "odicf",  anagram = "doc"   (k = 3)
anagramFreq = { d:1, o:1, c:1 }

Initial window "odi":
  windowFreq = { o:1, d:1, i:1 }
  mismatches: 'c' not present → +1    →  mismatches = 1

Slide 1  (pop 'o', add 'c'):
  pop 'o': freq[o]=1 == anagramFreq[o]=1  → was satisfied → mismatches +1 = 2
           freq[o] = 0
  add 'c': anagramFreq[c]-freq[c] = 1-0 = 1 → was under by 1 → mismatches -1 = 1
           freq[c] = 1
  window "dic", mismatches = 1  ✗

Slide 2  (pop 'd', add 'f'):
  pop 'd': 'd' in anagramFreq; freq[d]=1 == anagramFreq[d]=1 → mismatches +1 = 2
           freq[d] = 0
  add 'f': 'f' NOT in anagramFreq → ignored
  window "icf", mismatches = 2  ✗

No more slides.  RETURN FALSE
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n·k) | O(k) | Rebuild both freq maps per window |
| CompareOptimized | O(n·k) | O(k) | Build anagram map once; early exit on mismatch |
| Sliding Window | **O(n)** | O(k) | O(1) incremental map update + mismatch counter |

> **Best solution:** `OptimizedSolution` — linear time by incrementally maintaining the frequency map and a single mismatch counter across slides.

