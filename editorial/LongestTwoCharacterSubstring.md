# Longest Two Character Substring

## Problem Statement

Given a string `s`, return the length of the **longest substring** that contains **exactly 2 distinct characters**.  
If no such substring exists, return `0`.

### Examples

| Input | Output | Explanation |
|---|---|---|
| `"xyzyyx"` | `4` | `"yzyy"` → chars `{y, z}` |
| `"qrss"` | `3` | `"rss"` → chars `{r, s}` |
| `"ababba"` | `6` | whole string → chars `{a, b}` |
| `"ttttttt"` | `0` | only 1 distinct char, no valid window |
| `"abacbbcbxadd"` | `5` | `"cbbcb"` → chars `{b, c}` |

---

## Approach 1 — Naive (Brute Force)

### Intuition

The simplest idea: enumerate **every possible substring** and check whether it contains exactly 2 distinct characters.

### Algorithm

1. Iterate over all starting indices `i`.
2. For every `i`, iterate over all ending indices `j > i`.
3. Extract the substring `s[i..j)` and count distinct characters.
4. If the count equals exactly `2`, update `maxLength`.

### Code

```java
class NaiveSolution implements LongestTwoCharacterSubstring {

  private boolean check(String s) {
    return s.chars().distinct().count() == 2;
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    int maxLength = 0;
    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (check(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        }
      }
    }
    return maxLength;
  }
}
```

### Complexity

| | |
|---|---|
| **Time** | O(n³) — O(n²) substrings × O(n) per `distinct()` scan |
| **Space** | O(n) — substring allocation + stream internals |

### Problems

- Scanning every character of every substring is extremely wasteful.
- No early exit: even after a third distinct character is found, the inner loop keeps going.
- The whole string is rescanned from scratch on every `(i, j)` pair.

---

## Approach 2 — Intermediate Optimization

### Intuition

Two key observations let us cut work:

1. **Early exit on the inner loop**: once a third distinct character enters the window `s[i..j)`, no extension of that window can ever be valid. We can `break` immediately.
2. **Whole-string early exit**: if the entire string has only one distinct character, the answer is `0` — no need to scan at all.

### Algorithm

1. If the whole string has one distinct character, return `0`.
2. Iterate over starting indices `i`.
3. Maintain a `HashSet` while extending `j`. Stop the inner loop as soon as the set grows beyond `2`.
4. Track the maximum length seen when the set size is ≤ 2 (and ≥ 2 for exactly-2 semantics — handled by the outer caller checking against `0`).

```java
class IntermediateOptimization implements LongestTwoCharacterSubstring {

  private boolean substringContainsAtMostTwoDistinctCharacter(String s) {
    var set = new HashSet<Character>();
    for (char c : s.toCharArray()) {
      if (!set.contains(c) && set.size() == 2) return false;
      set.add(c);
    }
    return true;
  }

  boolean substringContainsOneDistinctCharacter(String s) {
    return s.chars().distinct().count() == 1;
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    if (substringContainsOneDistinctCharacter(s)) return 0;

    int maxLength = 0;
    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (substringContainsAtMostTwoDistinctCharacter(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        } else {
          break; // ← key improvement
        }
      }
    }
    return maxLength;
  }
}
```

### Complexity

| | |
|---|---|
| **Time** | O(n²) — outer loop O(n), inner loop amortized O(n) with early break |
| **Space** | O(1) — HashSet holds at most 3 characters |

### Remaining Problem

We still restart from scratch for every new starting index `i`. A lot of work is repeated:  
when we slide `i` forward by one, the whole suffix has to be re-examined.

---

## Approach 3 — Optimized: Sliding Window ✅

### Key Insight

Instead of fixing a left boundary and scanning right, use **two pointers** `l` and `r` that both move **only to the right**.

- Expand the window by advancing `r`.
- When the window becomes **invalid** (more than 2 distinct characters), shrink it from the left by advancing `l` until it is valid again.
- At every valid window of size exactly 2, update the answer.

Because both pointers only move right, each character is visited at most twice — once when `r` adds it and once when `l` removes it.

### Frequency Map

A `HashMap<Character, Integer>` tracks how many times each character appears in the current window `[l, r]`.

- **`put(c)`** — increment count; if absent, a new entry is created, growing the map.
- **`remove(c)`** — decrement count; if it hits `0`, delete the entry so `map.size()` accurately reflects the number of **distinct** characters in the window.

```
map.size() == number of distinct characters in s[l..r]
```

### Step-by-step Walkthrough

Using `s = "xyzyyx"`:

```
l=0, r=0: add 'x' → {x:1}           size=1  window="x"
l=0, r=1: add 'y' → {x:1, y:1}      size=2  window="xy"    max=2
l=0, r=2: add 'z' → {x:1, y:1, z:1} size=3  SHRINK:
           remove 'x' → {y:1, z:1}   size=2  l=1
           window="yz"                        max=2
l=1, r=3: add 'y' → {y:2, z:1}      size=2  window="yzy"   max=3
l=1, r=4: add 'y' → {y:3, z:1}      size=2  window="yzyy"  max=4
l=1, r=5: add 'x' → {y:3, z:1, x:1} size=3  SHRINK:
           remove 'y' → {y:2, z:1, x:1} l=2
           remove 'z' → {y:2, x:1}   size=2  l=3
           window="yyx"                       max=4
Answer: 4 ✓
```

### Code

```java
class OptimizedSolution implements LongestTwoCharacterSubstring {

  private void put(Map<Character, Integer> map, char c) {
    map.put(c, map.getOrDefault(c, 0) + 1);
  }

  private void remove(Map<Character, Integer> map, char c) {
    map.put(c, map.get(c) - 1);
    if (map.get(c).equals(0)) {
      map.remove(c);
    }
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    int maxLength = 0;
    var map = new HashMap<Character, Integer>();

    int l = 0;
    int r = 0;

    while (r < s.length()) {
      put(map, s.charAt(r));          // expand right

      while (map.size() > 2) {       // shrink left until valid
        remove(map, s.charAt(l));
        l++;
      }

      if (map.size() == 2) {         // exactly 2 distinct chars
        maxLength = Math.max(maxLength, r - l + 1);
      }

      r++;
    }

    return maxLength;
  }
}
```

### Complexity

| | |
|---|---|
| **Time** | **O(n)** — each character is added once and removed at most once |
| **Space** | **O(1)** — map holds at most 3 entries at any point |

---

## Complexity Comparison

| Approach | Time | Space | Key Idea |
|---|---|---|---|
| Naive | O(n³) | O(n) | Check all substrings from scratch |
| Intermediate | O(n²) | O(1) | Break inner loop on 3rd distinct char |
| **Optimized** | **O(n)** | **O(1)** | Sliding window + frequency map |

---

## Evolution Summary

```
Naive O(n³)
  ↓  Observation 1: once a 3rd char appears, extending further is useless → break
Intermediate O(n²)
  ↓  Observation 2: restarting left pointer from scratch is wasteful;
     left pointer only ever needs to move right → two-pointer / sliding window
Optimized O(n)
```

The sliding window pattern works here because the **validity of a window is monotone**: extending a valid window might break it, but shrinking it from the left can only restore or maintain validity — never make it worse. This monotone property is the prerequisite for any sliding-window solution.

