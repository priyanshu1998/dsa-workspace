# Longest Unique Substring

## Problem Statement

Given a string `s`, find the length of the **longest substring** that contains **no repeating characters**.

**Example:**
```
s = "abccba"
Substrings with all unique chars: "a", "ab", "abc", "bc", "cb", "cba", "ba", ...
Longest = "abc" (or "cba")  →  return 3

s = "xxxx"
Every substring of length > 1 repeats 'x'  →  return 1
```

---

## Core Insight: Uniqueness ↔ Set Size

A substring has all unique characters if and only if its **character set has the same size as the substring length**. Equivalently, inserting every character into a `HashSet` should never find a duplicate.

```
"abc"  →  set = {a, b, c}  size 3 == length 3  ✓ unique
"abca" →  set = {a, b, c}  size 3 ≠ length 4   ✗ not unique
```

---

## Solutions

### 1. Naive Solution — O(n³) Time, O(n) Space

The most straightforward approach: enumerate **every** substring, check if it has all unique characters, and track the maximum length.

```java
class NaiveSolution implements LongestUniqueSubstring {

    private boolean isUnique(String s) {
        var set = new HashSet<Character>();
        for (char c : s.toCharArray()) {
            if (set.contains(c))
                return false;
            set.add(c);
        }
        return true;
    }

    @Override
    public int longestUniqueSubstring(String s) {
        int maxLength = 0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                if (isUnique(s.substring(i, j))) {
                    maxLength = Math.max(maxLength, j - i);
                } else {
                    break;
                }
            }
        }
        return maxLength;
    }
}
```

**How it works:**
1. For every starting index `i`, try extending the substring to every ending index `j`.
2. For each candidate substring `s[i..j)`, call `isUnique()` which iterates the entire substring and uses a `HashSet` to detect duplicates.
3. If a duplicate is found, `break` — extending further from the same `i` will still contain the duplicate, so no point continuing.
4. Track the maximum length of any unique substring seen.

**The `break` optimization:**
The inner loop breaks as soon as a duplicate is detected. This is correct because if `s[i..j)` contains a duplicate, then every longer substring `s[i..j+1)`, `s[i..j+2)`, ... also contains that duplicate. This prunes many branches but doesn't change the worst-case complexity.

**Complexity:**
- Time: **O(n³)** — O(n²) substrings × O(n) uniqueness check each (worst case: all unique characters)
- Space: **O(n)** — the `HashSet` can hold up to `n` characters

**Drawback:** For every candidate substring, the uniqueness check scans it from scratch. When we extend a substring by one character, we already know the prefix was unique — we only need to check if the **new character** conflicts with the existing ones. This redundant re-scanning is what we eliminate next.

---

### 2. Optimized Solution (Sliding Window) — O(n) Time, O(n) Space

**Key Insight:** Instead of checking every substring independently, maintain a **window** `[l, r)` that always contains unique characters. Use a `HashSet` to track which characters are currently in the window.

- **Expand** the window by moving `r` rightward and adding `s[r]` to the set.
- **Shrink** the window by moving `l` rightward (removing `s[l]` from the set) whenever the new character `s[r]` already exists in the set.

The window invariant — all characters in `[l, r)` are unique — is maintained at every step.

```java
class OptimizedSolution implements LongestUniqueSubstring {

    @Override
    public int longestUniqueSubstring(String s) {
        int maxLength = 0;

        int l = 0;
        int r = 0;

        var set = new HashSet<Character>();
        while (r < s.length()) {

            while (set.contains(s.charAt(r))) {
                set.remove(s.charAt(l));
                l++;
            }
            set.add(s.charAt(r));
            maxLength = Math.max(maxLength, set.size());
            r++;
        }

        return maxLength;
    }
}
```

**How it works:**

1. Start with an empty window: `l = 0`, `r = 0`, `set = {}`.
2. At each step, try to add `s[r]` to the window:
   - If `s[r]` is **not** in the set → add it, update `maxLength`, and advance `r`.
   - If `s[r]` **is** in the set → the window would have a duplicate. Remove `s[l]` from the set and advance `l`. Repeat until `s[r]` is no longer in the set.
3. The maximum value of `set.size()` across all steps is the answer.

**Why this is correct:**
- The window `[l, r)` always satisfies the invariant: all characters are unique.
- Every possible maximal unique substring starting at some index `l` will be considered, because `l` only moves forward when forced by a duplicate, and `r` always extends as far as possible.
- We never skip a potentially longer unique substring.

**Why this is O(n):**
Each pointer `l` and `r` moves **only forward** and each moves **at most `n` times** total across all iterations. Every character is added to the set at most once (when `r` passes it) and removed at most once (when `l` passes it). So the total work is **O(2n) = O(n)**.

```
Pointer movement:

l:  0 → 0 → 0 → 3 → 3 → 4 → ...   (moves forward only when shrinking)
r:  0 → 1 → 2 → 2 → 3 → 4 → ...   (moves forward every outer iteration)

Total moves: l moves at most n times + r moves exactly n times = O(n)
```

**Complexity:**
- Time: **O(n)** — each character is added/removed from the set at most once
- Space: **O(min(n, A))** — the set holds at most `min(n, A)` characters, where `A` is the alphabet size (e.g., 26 for lowercase English letters)

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For every starting index i, try all ending indices j.
       For each substring s[i..j), scan it entirely to check uniqueness.
       Redundant work: re-checking characters already verified as unique.
       Time: O(n³)   Space: O(n)

           ▼  Observation: when extending s[i..j) to s[i..j+1), the prefix
              s[i..j) was already unique — we only need to check if s[j]
              conflicts with the existing characters in the window.

           ▼  Observation: when s[j] IS a duplicate, we don't need to restart
              from i+1 and re-scan. We can shrink from the left just enough
              to remove the conflicting character, preserving work already done.

Sliding Window ──────────────────────────────────────────────────────────────►
       Maintain a HashSet for the current window [l, r).
       Expand r: add s[r] to the set (O(1)).
       Shrink l: remove s[l] from the set (O(1)) until the duplicate is gone.
       Each character enters and leaves the set at most once.
       Time: O(n)   Space: O(n)   ← Optimal
```

### Step-by-step derivation of Sliding Window from Naive

Consider what the naive solution does for each starting index `i`:

| Step | Naive | Sliding Window |
|------|-------|----------------|
| Start at `i=0` | Scan `s[0..1)`, `s[0..2)`, ... checking uniqueness from scratch each time | Expand `r` rightward, adding one char to the set each time |
| Hit duplicate at `j` | `break`, move to `i=1`, rescan `s[1..2)`, `s[1..3)`, ... | Shrink `l` rightward until the duplicate is removed, then continue expanding `r` |
| Key difference | Throws away all knowledge about the previous window when moving `i` forward | Preserves the set, only removing the minimum number of characters needed |

The sliding window eliminates **two sources of redundancy**:
1. **Re-scanning the window:** The naive approach calls `isUnique()` which traverses the full substring. The sliding window maintains a set incrementally — each new character is checked against the set in O(1).
2. **Restarting from scratch:** When the naive approach moves from `i` to `i+1`, it forgets everything. The sliding window keeps the valid portion of the window intact and only removes characters from the left edge as needed.

---

## Worked Example

```
s = "abcbda"

Step 1: l=0, r=0, set={}
  s[0]='a' not in set → add → set={a}, maxLength=1
  r=1

Step 2: l=0, r=1, set={a}
  s[1]='b' not in set → add → set={a,b}, maxLength=2
  r=2

Step 3: l=0, r=2, set={a,b}
  s[2]='c' not in set → add → set={a,b,c}, maxLength=3
  r=3

Step 4: l=0, r=3, set={a,b,c}
  s[3]='b' IS in set → shrink:
    remove s[0]='a', l=1 → set={b,c}
    remove s[1]='b', l=2 → set={c}
  s[3]='b' not in set → add → set={c,b}, maxLength=3
  r=4

Step 5: l=2, r=4, set={c,b}
  s[4]='d' not in set → add → set={c,b,d}, maxLength=3
  r=5

Step 6: l=2, r=5, set={c,b,d}
  s[5]='a' not in set → add → set={c,b,d,a}, maxLength=4
  r=6

Done. Answer: 4 (substring "cbda")
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n³) | O(n) | Enumerate all substrings, check uniqueness from scratch |
| Sliding Window | **O(n)** | **O(n)** | Maintain a set incrementally; expand/shrink with two pointers |

> **Best solution:** `OptimizedSolution` (Sliding Window) — linear time by ensuring each character is processed at most twice (once added, once removed).

