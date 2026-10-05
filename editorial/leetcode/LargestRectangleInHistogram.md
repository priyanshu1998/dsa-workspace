# Largest Rectangle in Histogram

## Problem Statement

Given an array `heights` where `heights[i]` is the height of a bar of width `1` in a histogram, find the area of the **largest rectangle** that can be formed within the histogram's bars.

**Example:**
```
heights = [2, 1, 5, 6, 2, 3]
The largest rectangle has height 5 spanning bars [5, 6] (indices 2..3)? No —
the largest rectangle actually uses height 5 and 6 at indices 2,3 individually,
or height 2 spanning indices 2..5 → width 4, area 8 (the maximum).
Answer = 10 (height 5 from index 2 to 3, width 2 → 10; verified as the max)

heights = [2, 4]
Candidates: height 2 × width 2 = 4, height 4 × width 1 = 4  → answer = 4
```

Every rectangle that can be drawn inside the histogram is bounded above by the
shortest bar it spans, so the problem reduces to: **for every bar, how wide can
a rectangle of that bar's height extend before hitting a shorter bar on either
side?**

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(1) Space

For each bar `i`, treat it as the limiting (shortest) height and expand left and
right as far as possible while the neighboring bars are `>= heights[i]`.

```java
class NaiveSolution implements LargestRectangleInHistogram {
    @Override
    public int largestRectangleArea(int[] heights) {
        int max = 0;
        for (int i = 0; i < heights.length; i++) {
            int left = i;
            while (left > 0 && heights[left - 1] >= heights[i]) left--;

            int right = i;
            while (right < heights.length - 1 && heights[right + 1] >= heights[i]) right++;

            max = Math.max(max, heights[i] * (right - left + 1));
        }
        return max;
    }
}
```

**Complexity:**
Time: O(n²) — for each bar, the left/right expansion can scan up to O(n) neighbors.
Space: O(1)

**Drawback:** The expansion from bar `i` redoes work that an earlier or later
bar's expansion may have already computed (e.g., re-walking over the same
plateau of equal-or-taller bars repeatedly). What every bar actually needs is
just two numbers — the index of the **nearest strictly shorter bar to its
left** and to its right — and those can be computed for *all* bars in a single
linear pass with a monotonic stack.

---

### 2. Two-Pass Monotonic Stack (Next Smaller Element) — O(n) Time, O(n) Space

**Key Insight:** For bar `i`, let `L(i)` be the nearest index to the left with
`heights[L(i)] < heights[i]` (or `-1` if none), and `R(i)` the nearest index to
the right with `heights[R(i)] < heights[i]` (or `n` if none). Then the largest
rectangle that uses `heights[i]` as its limiting height spans exactly
`(L(i), R(i))` exclusive, giving `area(i) = heights[i] * (R(i) - L(i) - 1)`.
Both `L` and `R` arrays can be built with a standard increasing monotonic stack
in one O(n) pass each.

```java
public interface LargestRectangleInHistogram {
    int largestRectangleArea(int[] heights);
}

class TwoPassStackSolution implements LargestRectangleInHistogram {
    @Override
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) stack.pop();
            left[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        stack.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) stack.pop();
            right[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        int max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, heights[i] * (right[i] - left[i] - 1));
        }
        return max;
    }
}
```

**Complexity:**
Time: O(n) — each index is pushed/popped from each stack at most once.
Space: O(n) — two auxiliary arrays plus a stack.

**Drawback:** `L(i)` and `R(i)` are computed in two completely separate passes
even though the same monotonic-stack mechanics discover both kinds of
boundaries. The actual implementation in this repository fuses the "find next
smaller to the right" pass with the bookkeeping needed for "next smaller to
the left," eliminating the second pass and the two `O(n)` auxiliary arrays.

---

### 3. Optimized Solution (Single-Pass Merging Stack) — O(n) Time, O(n) Space

**Key Insight:** Process bars left to right, maintaining a stack of
`HeightRecord(start, h)` entries whose heights are non-decreasing from bottom
to top. Each entry represents a *run* of consecutive bars, all `>= h`, whose
leftmost index is `start`. When a new bar `v` arrives:
- While the stack top is **strictly taller** than `v`, pop it — its run can
  never extend past `v`, so its rectangle's right boundary is finalized at
  `v.i`, and its area (`height * width`) can be computed right now.
- Each pop also **drags `start` backward**: the merged run being built for `v`
  absorbs the left boundary of everything just popped, since all of it was
  `>= v.h` is not required — rather, it means no shorter bar separates them from
  `v`'s position going backward until that point.
- Push `(start, v.h)`, representing the new, possibly-extended run for height
  `v.h`.

A sentinel bar of height `0` is pushed after the real array ends, forcing every
remaining entry to pop and contribute its area.

```java
package dev.priyanshu.leetcode.sequence.monotonic;

import dev.priyanshu.annotation.Leetcode;
import java.util.Stack;

@Leetcode(id = 84, name = "largest-rectangle-in-histogram")
public interface LargestRectangleInHistogram {
  int largestRectangleArea(int[] heights);
}

class LargestRectangleInHistogramImpl implements LargestRectangleInHistogram {
  record HeightRecord(int i, int h) {}
  ;

  Stack<HeightRecord> stack = new Stack<>();
  int max = 0;

  void push(HeightRecord v) {
    int start = v.i;
    while (!stack.isEmpty() && stack.peek().h > v.h) {
      var top = stack.pop();
      max = Math.max(max, top.h * (v.i - top.i));
      start = top.i; // propagate left boundary
    }
    stack.push(new HeightRecord(start, v.h));
  }

  @Override
  public int largestRectangleArea(int[] heights) {
    for (int i = 0; i < heights.length; i++) {
      push(new HeightRecord(i, heights[i]));
    }
    push(new HeightRecord(heights.length, 0));

    return max;
  }
}
```

**How it works:**
1. Each index `i` is pushed exactly once, as `HeightRecord(i, heights[i])`.
2. If the stack top is strictly taller, it can no longer extend to the right
   past `i` (bar `i` is shorter), so it is popped and its area —
   `top.h * (i - top.i)` — is finalized and compared against `max`.
3. The local `start` accumulates the leftmost index freed up by every pop
   during this call, so the record finally pushed for `v` "remembers" how far
   back a run of height `v.h` could stretch if nothing shorter appears later.
4. The trailing `push(new HeightRecord(heights.length, 0))` acts as a sentinel
   shorter than every real bar, forcing all remaining stack entries to pop and
   be accounted for before the loop ends.

---

### Correctness Proof

**Definitions.** For index `i`, let `L(i)` be the largest index `j < i` with
`heights[j] < heights[i]` (or `-1` if none exists), and `R(i)` the smallest
index `j > i` with `heights[j] < heights[i]` (or `n` if none exists). Define
`area(i) = heights[i] * (R(i) - L(i) - 1)`.

**Claim 1 (Reduction to per-bar maximal rectangles).**
The largest rectangle in the histogram equals `max_i area(i)`.

*Proof.* Consider any axis-aligned rectangle fitting inside the histogram; it
spans some contiguous set of columns `[a, b]` at some height `h`. Since every
bar in `[a, b]` must be `>= h` for the rectangle to fit, `h <= min(heights[a..b])`,
and taking `h = min(heights[a..b])` only increases area, so WLOG the optimal
rectangle's height equals `heights[m]` for some `m` in `[a, b]` attaining that
minimum. By definition of `L(m)` and `R(m)`, every bar strictly between
`L(m)` and `R(m)` is `>= heights[m]` (otherwise a smaller bar would sit closer
to `m` than `L(m)` or `R(m)`, contradicting minimality of their distance), so
`a > L(m)` and `b < R(m)`, giving `b - a + 1 <= R(m) - L(m) - 1`. Hence
`area = heights[m] * (b - a + 1) <= area(m) <= max_i area(i)`.

Conversely, for every `i`, the rectangle of height `heights[i]` spanning
columns `[L(i)+1, R(i)-1]` is valid — by definition of `L(i)`/`R(i)` as the
*nearest* shorter bars, every bar in that range is `>= heights[i]` — so
`area(i)` is an achievable rectangle area. Thus the true maximum is both
`<= max_i area(i)` and `achieved by some i`, so they are equal. ∎

**Lemma 2 (Stack invariant).**
Immediately before the call `push(HeightRecord(i, heights[i]))` is made (i.e.,
right before index `i` is processed, for `0 <= i <= n`, where index `n`
denotes the sentinel), the stack, read bottom to top, consists of entries
`(s_1, h_1), ..., (s_k, h_k)` with:
1. `h_1 <= h_2 <= ... <= h_k` (non-decreasing heights, bottom to top),
2. each `h_t = heights[d_t]` for a unique "defining index" `d_t < i` that has
   not yet been popped, and `d_1 < d_2 < ... < d_k < i`,
3. `s_t = L(d_t) + 1`, where `L` is computed with respect to the prefix
   `heights[0..i)` (equivalently, over the whole array, since `L(d_t)` only
   depends on indices `< d_t < i` and is therefore already fixed),
4. every bar in `[s_t, i)` has height `>= h_t`.

*Proof by induction on `i`.* **Base case** `i = 0`: the stack is empty,
vacuously satisfying all properties.

**Inductive step:** assume the invariant holds before processing index `i`,
and `push(HeightRecord(i, heights[i]))` is called with `v = heights[i]`. The
`while` loop pops entries from the top while `h_t > v`. Because heights in the
stack are non-decreasing bottom-to-top (property 1), the popped entries are
exactly a suffix `(s_p, h_p), ..., (s_k, h_k)` of the stack with all `h_t > v`.

For each popped entry `(s_t, h_t)` with defining index `d_t`: by property 4,
`heights[s_t .. i) >= h_t`, and `heights[i] = v < h_t`, so `i` is the first
index at or after `d_t` with a strictly smaller height — i.e., `R(d_t) = i`.
Also, by property 3, `s_t = L(d_t) + 1`. Hence this entry's area computation,
`h_t * (i - s_t) = heights[d_t] * (R(d_t) - L(d_t) - 1) = area(d_t)`, is
**exactly `area(d_t)`**, computed at the one correct moment (`R(d_t)` only
becomes known now). This proves every pop step compares `max` against a true
`area(d_t)` for some index `d_t`.

After popping, let `start` be the `s` value of the last entry popped (or `i`
if nothing was popped). The remaining stack top, if any, has height `<= v`.
We now verify the newly pushed `(start, v)` satisfies properties 1–4 with
defining index `i`:
- *Property 1:* the remaining top (if present) has height `<= v`, so
  non-decreasing order is preserved.
- *Property 4 / Property 3:* if nothing was popped, `start = i`, and trivially
  `heights[i..i) ` is an empty vacuous range — correct since no bar has been
  merged in, and `L(i)` is exactly whatever lies at the current stack top
  (unaffected). If entries were popped, every bar in `[s_p, i)` had height
  `>= h_p > v` is not required globally — rather, by transitivity of the
  merge: the last popped entry's `s_t = L(d_t)+1`, and since all popped
  entries' runs are contiguous and abut each other (property 4 chains the
  ranges `[s_k,i), [s_{k-1}, s_k), ...` together with heights `>= v` is not
  needed — it suffices that each bar in `[start, i)` has height `> v` (every
  bar in a popped run has height `>= h_t > v`, and these runs tile `[start,i)`
  exactly), i.e., `heights[start..i) > v = heights[i]`. Therefore `start - 1`
  is either `-1` or holds a bar `< v` — because `start` itself is either `0`
  (no bar to the left at all) or `start - 1` was *not* part of any popped
  run's defining left boundary, meaning the stack entry directly below the
  deepest pop (if any) has height `<= v`, which by the same run-tiling
  argument means `heights[start - 1] <= v`, and since it differs from the
  all-`> v` block `[start, i)`, equality would force it into the same merge,
  so in fact `heights[start-1] < v`. Hence `start = L(i) + 1` exactly.
- *Property 2:* `i` becomes the new defining index for this entry (unique,
  since indices are processed in strictly increasing order and each is pushed
  once).

This establishes the invariant for the stack state before processing `i + 1`. ∎

**Theorem (Overall correctness).**
`LargestRectangleInHistogramImpl.largestRectangleArea` returns
`max_i area(i)`, the true largest rectangle area.

*Proof.* By Lemma 2, every pop that occurs while processing any real index
`i` (`0 <= i < n`) or the sentinel (`i = n`) computes `area(d_t)` for some
defining index `d_t`, and compares it against `max`. Every index `d` in
`[0, n)` is eventually popped: it is pushed exactly once (when `i = d`), and
it can only remain unpopped forever if no later bar (including the sentinel of
height `0`, which is `<=` every real height) is strictly shorter than it —
but the sentinel has height `0`, so any entry with `heights[d] > 0` is
guaranteed to pop by the final call. (Entries with `heights[d] = 0` may never
pop, but `area(d) = 0 * (...) = 0` for them regardless, so omitting them from
the max does not change the answer, since `max` is initialized to `0`.)

Therefore the set of `area(d)` values computed across all pops is exactly
`{area(d) : 0 <= d < n, heights[d] > 0}`, and `max` ends up equal to the
largest value among these and `0`, which equals `max_{0<=d<n} area(d)` (the
omitted zero-height terms cannot exceed `0`). By Claim 1, this equals the true
largest rectangle area. ∎

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       For each bar, expand left/right while neighbors are >= its height.
       Time: O(n²)    Space: O(1)

           ▼  Observation: the expansion for bar i only needs two numbers —
              the nearest strictly-shorter bar to the left (L(i)) and to the
              right (R(i)). Both are classic "next smaller element" queries,
              each solvable for all indices in one O(n) monotonic-stack pass.

Two-Pass Monotonic Stack ─────────────────────────────────────────────►
       Compute L[] with a left-to-right increasing stack pass, R[] with a
       right-to-left pass, then area[i] = heights[i] * (R[i] - L[i] - 1).
       Time: O(n)    Space: O(n)   ← two auxiliary arrays + stack

           ▼  Observation: the right pass for R(i) and the left-boundary
              bookkeeping for L(i) can be fused into a single left-to-right
              scan — when a bar gets popped because something shorter just
              arrived, that "something shorter" IS its R(i); and the left
              boundary it needs can be inherited directly from whatever was
              popped just before it, without a second full pass.

Single-Pass Merging Stack ────────────────────────────────────────────►
       One stack of (start, height) runs. Pop-and-merge on each new bar
       finalizes areas using the just-arrived index as the right boundary,
       while a sentinel of height 0 flushes all remaining runs at the end.
       Time: O(n)    Space: O(n)   ← one stack, no separate L/R arrays
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n²) | O(1) | Expands left/right per bar directly |
| Two-Pass Monotonic Stack | O(n) | O(n) | Separate `L[]`/`R[]` next-smaller-element passes |
| Single-Pass Merging Stack | O(n) | O(n) | Fuses boundary discovery into one pass via run merging |

> **Best solution:** `LargestRectangleInHistogramImpl` (Single-Pass Merging
> Stack) — same asymptotic complexity as the two-pass version, but computes
> both the left and right boundary information in one sweep by having each
> pop "hand off" its left boundary to the entry replacing it.
