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

### 4. MostOptimizedSolution (Fixed-size Array + Unified Pass) — O(n) Time, O(1) Space

**Key Insight:** Every solution so far uses `HashMap<Character, Integer>`, which pays for:
- **Autoboxing** — `char` → `Character` and `int` → `Integer` on every read/write
- **Hashing overhead** — `hashCode()` + collision handling per access
- **Heap allocations** — each `Integer` box is a separate object

Since all characters are **lowercase English letters**, the entire frequency map fits in a fixed `int[26]` array. Array access by index (`c - 'a'`) is a single CPU instruction — no boxing, no hashing, no heap allocation. The 26-element array is a compile-time constant, so space becomes **O(1)**.

A secondary cleanup: `OptimizedSolution` calls `compareAnagram()` for the first window (which internally builds *two extra* HashMaps), and then builds *yet another* HashMap for the sliding part. All of that collapses into a single unified setup loop.

```java
class MostOptimizedSolution implements HasSubstringAnagram {

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();
        if (k > s.length()) return false;

        int[] anagramFreq = new int[26];
        int[] windowFreq  = new int[26];

        // Build both frequency arrays in one unified pass
        for (int i = 0; i < k; i++) {
            anagramFreq[anagram.charAt(i) - 'a']++;
            windowFreq[s.charAt(i) - 'a']++;
        }

        // Count mismatches for the initial window
        int mismatches = 0;
        for (int i = 0; i < 26; i++)
            if (anagramFreq[i] != windowFreq[i]) mismatches++;

        if (mismatches == 0) return true;

        // Slide the window — each step is O(1) with no allocations
        for (int r = k; r < s.length(); r++) {
            int addIdx = s.charAt(r)     - 'a';   // character entering
            int popIdx = s.charAt(r - k) - 'a';   // character leaving

            // Update mismatches BEFORE changing the frequency so comparisons
            // reflect the current (pre-update) state.

            // --- incoming character ---
            if      (windowFreq[addIdx] == anagramFreq[addIdx])     mismatches++; // was matching → now over
            else if (windowFreq[addIdx] == anagramFreq[addIdx] - 1) mismatches--; // was 1 short  → now matching
            windowFreq[addIdx]++;

            // --- outgoing character ---
            if      (windowFreq[popIdx] == anagramFreq[popIdx])     mismatches++; // was matching → now under
            else if (windowFreq[popIdx] == anagramFreq[popIdx] + 1) mismatches--; // was 1 over   → now matching
            windowFreq[popIdx]--;

            if (mismatches == 0) return true;
        }

        return false;
    }
}
```

**Why updating mismatches BEFORE the frequency matters:**

The mismatch logic asks "is this character currently at the exact boundary between matching and mismatching?" We need to check the count *before* the increment/decrement so the boundary comparison is correct:

```
windowFreq[addIdx] == anagramFreq[addIdx]      →  currently matching, +1 makes it over  → mismatch created
windowFreq[addIdx] == anagramFreq[addIdx] - 1  →  currently 1 short,  +1 makes it exact → mismatch resolved
(all other cases: the count is already mismatching and moving further away, or over by 2+)
```

The same logic applies symmetrically for the outgoing character.

**What about adding and removing the same character in one step?**

If `addIdx == popIdx` (e.g., window slides over a repeated character of the same type), the net change to `windowFreq` is zero. The mismatch adjustments also cancel out correctly — the incremented frequency used for the pop check already accounts for the add, and the two boundary checks produce equal and opposite adjustments. ✓

**Complexity:**
- Time: **O(n)** — O(k) init + O(26) mismatch seed + O(1) per slide
- Space: **O(1)** — two fixed 26-element arrays (constant, independent of input size)

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

OptimizedSolution ───────────────────────────────────────────────────────────►
       Maintain the window's frequency map incrementally. Track only a single
       integer (mismatches) that summarises whether the window is an anagram.
       Each slide: O(1) update to the map and the counter.
       Time: O(n)   Space: O(k)  — but HashMap has autoboxing + hashing overhead

           ▼  Observation: alphabet is fixed (26 letters) — HashMap is overkill.
              Replace Map<Character,Integer> with int[26] for O(1) indexed access.
              Also unify the redundant first-window setup into a single pass.

MostOptimizedSolution ───────────────────────────────────────────────────────►
       Two int[26] arrays instead of HashMaps. No autoboxing, no hashing,
       no heap allocation per access. Single unified setup loop.
       Time: O(n)   Space: O(1)   ← Optimal in both dimensions
```

### Step-by-step derivation

| Window | Naive / CompareOptimized | OptimizedSolution / MostOptimizedSolution |
|--------|--------------------------|-------------------------------------------|
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
| OptimizedSolution | O(n) | O(k) | O(1) incremental HashMap update + mismatch counter |
| MostOptimizedSolution | **O(n)** | **O(1)** | Fixed `int[26]` arrays — no boxing, no hashing |

> **Best solution:** `MostOptimizedSolution` — linear time and **true O(1) space** by replacing `HashMap<Character, Integer>` with a fixed-size `int[26]` array, exploiting the known 26-character alphabet.

