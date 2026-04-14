# Max Ones With a Single Flip

## Problem Statement

Given a binary string `s` consisting of `'0'`s and `'1'`s, return the maximum number of consecutive `1`s you can achieve by flipping **at most one** `'0'` to `'1'`.

**Example:**
```
s = "11011"
The only '0' is at index 2. Flipping it gives "11111"  →  return 5

s = "110100"
Best flip: index 2 → window "1101" becomes "1111"  →  return 4

s = "0000"
Flipping any one '0' gives a window of exactly one '1'  →  return 1
```

---

## Core Insight: Reframe as a Substring Problem

Instead of simulating every possible flip, observe that flipping one `'0'` in a contiguous window is only useful if that `'0'` is **inside the window**. The resulting run of `1`s is exactly the window length.

> **The answer equals the length of the longest substring that contains at most one `'0'`.**

This is because:
- A window with **zero** `'0'`s is already all `1`s — no flip needed, just count them.
- A window with **exactly one** `'0'` becomes all `1`s after one flip.
- A window with **two or more** `'0'`s cannot be made all `1`s with a single flip.

---

## Solutions

### 1. Naive Solution — O(n³) Time, O(1) Space

The simplest approach: enumerate every substring, check whether it has at most one `'0'`, and track the maximum length.

```java
class NaiveSolution implements MaxOnesWithSingleFlip {

    private boolean containsAtMostOne0(String s) {
        long ones = s.chars().filter(c -> c == '1').count();
        long zeros = s.length() - ones;
        return zeros == 0 || zeros == 1;
    }

    private boolean check(String substring) {
        return containsAtMostOne0(substring);
    }

    @Override
    public int maxOnesWithSingleFlip(String s) {
        int maxLength = 0;

        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                if (check(s.substring(i, j))) {
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
1. For every starting index `i`, try extending the window to every ending index `j`.
2. For each candidate substring `s[i..j)`, call `check()` which counts zeros by scanning the entire substring.
3. If the substring contains ≥ 2 zeros, `break` — extending the window further from the same `i` can only add more zeros, never remove them.
4. Track the maximum length seen so far.

**The `break` optimization:**
The inner loop breaks as soon as a second `'0'` is encountered. This is correct because if `s[i..j)` has two zeros, then every longer substring `s[i..j+1)`, `s[i..j+2)`, ... will also have at least two zeros. This prunes many branches but does **not** change the worst-case complexity.

**Complexity:**
- Time: **O(n³)** — O(n²) substrings (O(n) starting positions × O(n) ending positions) × O(n) scan inside each `check()` call
- Space: **O(1)** — only counters are used (ignoring the substring allocation)

**Drawback:** Each call to `check()` re-scans the substring from scratch. When we extend `s[i..j)` to `s[i..j+1)`, we already know the prefix had ≤ 1 zero — we only need to examine the **new character** `s[j]`. The naive approach discards this knowledge every time `j` advances. This redundant re-scanning is exactly what the optimized solution eliminates.

---

### 2. Optimized Solution (Sliding Window) — O(n) Time, O(1) Space

**Key Insight:** Instead of re-scanning the window on each extension, maintain **live counters** `count0` and `count1` that track the number of zeros and ones currently inside the window `[l, r]`.

- **Expand** the window rightward by adding `s[r]` to the counters (`addLeadingBit`).
- **Shrink** the window leftward by removing `s[l]` from the counters (`removeTrailingBit`) whenever `count0` reaches 2.

The window invariant — **at most one `'0'` inside `[l, r]`** — is maintained at every step.

```java
class OptimizedSolution implements MaxOnesWithSingleFlip {

    int count0 = 0;
    int count1 = 0;

    void addLeadingBit(char c) {
        if (c == '0') count0++;
        else          count1++;
    }

    void removeTrailingBit(char c) {
        if (c == '0') count0--;
        else          count1--;
    }

    @Override
    public int maxOnesWithSingleFlip(String s) {
        int maxLength = 0;

        int l = 0;
        int r = 0;

        while (r < s.length()) {
            addLeadingBit(s.charAt(r));

            while (count0 == 2) {
                removeTrailingBit(s.charAt(l));
                l++;
            }

            maxLength = Math.max(maxLength, r - l + 1);
            r++;
        }

        return maxLength;
    }
}
```

**How it works:**
1. Start with an empty window: `l = 0`, `r = 0`, `count0 = 0`, `count1 = 0`.
2. At each step, add `s[r]` into the window (increment the appropriate counter).
3. If `count0` reaches 2, the window is invalid — shrink from the left until `count0` drops back to 1.
4. The current window `[l, r]` is always valid. Record its length `r - l + 1` as a candidate answer.
5. Advance `r` and repeat.

**Why the shrink condition is `count0 == 2` (not `> 1`):**
The inner `while` loop removes characters from the left one at a time. Each removal decrements either `count0` or `count1` by exactly 1. As soon as `count0` falls to 1, the invariant is restored and shrinking stops. We could never overshoot to `count0 == 0` without first passing through `count0 == 1`, so checking `== 2` is precise and correct.

**Why this is O(n):**
Each pointer `l` and `r` only ever moves **forward**, and each moves **at most `n` times** total across all iterations. Every character is added to the window exactly once (when `r` visits it) and removed at most once (when `l` passes over it). Total work: **O(2n) = O(n)**.

```
Pointer movement for s = "110100":

r:  0 → 1 → 2 → 3 → 4* → 5*     (* = shrink triggered)
l:  0 → 0 → 0 → 0 → 3  → 5

Each character is touched by r once and by l at most once → O(n) total.
```

**Complexity:**
- Time: **O(n)** — each character is added and removed from the window at most once
- Space: **O(1)** — only two integer counters and two pointer variables are used

---

## Optimization Journey

```
Naive  ──────────────────────────────────────────────────────────────────────►
       For every starting index i, try all ending indices j.
       For each substring s[i..j), count its zeros by scanning it entirely.
       Redundant work: re-counting characters already seen in the window.
       Time: O(n³)   Space: O(1)

           ▼  Observation: when extending s[i..j) to s[i..j+1), the prefix
              s[i..j) already had a known zero-count. We only need to check
              whether the NEW character s[j] is a '0' — O(1) per extension.

           ▼  Observation: when a second '0' enters at position j, we don't
              restart from i+1. We can shrink from the left just enough to
              push one '0' out of the window, preserving the rest of our work.

Sliding Window ───────────────────────────────────────────────────────────────►
       Maintain count0 and count1 for the current window [l, r].
       Expand r:  addLeadingBit  → update one counter in O(1).
       Shrink l: removeTrailingBit → update one counter in O(1) until count0 < 2.
       Each character enters and leaves the window at most once.
       Time: O(n)   Space: O(1)   ← Optimal
```

### Step-by-step derivation from Naive to Sliding Window

| Step | Naive | Sliding Window |
|------|-------|----------------|
| Start at `i` | Scan `s[i..1)`, `s[i..2)`, ... re-counting zeros from scratch | Expand `r` rightward, incrementing one counter per step |
| Hit second `'0'` at `j` | `break`, move to `i+1`, re-scan from the new start | Shrink `l` rightward until `count0` drops to 1, then continue |
| Key difference | Discards all knowledge when `i` advances | Preserves the valid portion of the window; only the left edge changes |

The sliding window eliminates **two sources of redundancy**:
1. **Re-scanning the window:** The naive `check()` traverses the full substring every call. The sliding window updates counters incrementally — one `+1` or `-1` per character touched.
2. **Restarting from scratch:** The naive approach resets completely when moving to the next `i`. The sliding window retains the valid window content, just advancing `l` past the excess `'0'`.

---

## Worked Example

```
s = "1101011"
        indices: 0='1'  1='1'  2='0'  3='1'  4='0'  5='1'  6='1'

Step 1: l=0, r=0 → add '1' → count1=1, count0=0 | window="1"     | maxLen=1
Step 2: l=0, r=1 → add '1' → count1=2, count0=0 | window="11"    | maxLen=2
Step 3: l=0, r=2 → add '0' → count1=2, count0=1 | window="110"   | maxLen=3
Step 4: l=0, r=3 → add '1' → count1=3, count0=1 | window="1101"  | maxLen=4
Step 5: l=0, r=4 → add '0' → count0=2  ← SHRINK
          remove '1' at l=0 → count1=2, l=1  | count0 still 2
          remove '1' at l=1 → count1=1, l=2  | count0 still 2
          remove '0' at l=2 → count0=1, l=3  | invariant restored ✓
        window="10" (s[3..4])  | maxLen=max(4,2)=4
Step 6: l=3, r=5 → add '1' → count1=2, count0=1 | window="101"   | maxLen=4
Step 7: l=3, r=6 → add '1' → count1=3, count0=1 | window="1011"  | maxLen=4

Done. Answer: 4
Winning windows: "1101" (s[0..3]) and "1011" (s[3..6])
  → flip the '0' in either window to get "1111"  →  4 consecutive 1s ✓
```

---

## Complexity Summary

| Solution       | Time    | Space  | Key Idea                                                          |
|----------------|---------|--------|-------------------------------------------------------------------|
| Naive          | O(n³)   | O(1)   | Enumerate all substrings, scan each to count zeros               |
| Sliding Window | **O(n)**| **O(1)**| Maintain live counters; expand right, shrink left when count0=2 |

> **Best solution:** `OptimizedSolution` (Sliding Window) — linear time and constant space by processing each character at most twice (once added via `r`, once removed via `l`).





