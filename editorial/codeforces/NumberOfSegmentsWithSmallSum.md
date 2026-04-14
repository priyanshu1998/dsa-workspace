# Number of Segments with Small Sum — Editorial

## Problem Statement

Given an array `nums` of length `n` and a long integer `target`, count the
**total number** of contiguous segments (subarrays) whose element sum is
**at most `target`** (i.e. ≤ target).

A segment `[l, r]` is called **good** if:

```
nums[l] + nums[l+1] + ... + nums[r]  ≤  target
```

**Input:**
- `n` — the length of the array
- `nums` — array of **non-negative** long integers
- `target` — the maximum allowed sum for a segment to be "good"

**Output:**
- The total count of good (contiguous) segments

---

## Observations

### 1 — Non-negative elements unlock the sliding window

All elements are **non-negative**, so the window sum is *monotonically
non-decreasing* as we expand the right boundary:

```
sum(nums[l..r])  ≤  sum(nums[l..r+1])   for any l, r
```

This single fact is the foundation of the entire algorithm:

- If a window `[l, r]` is **too large** (sum > target), adding `nums[r+1]`
  will only make it worse → we must shrink from the left.
- If a window `[l, r]` is **valid** (sum ≤ target), removing `nums[l]` keeps
  it valid (or improves it) → we never need to move the left pointer backwards.

Both pointers therefore move **strictly rightward** — the classic
two-pointer / sliding-window invariant that guarantees O(n) time.

---

### 2 — How to count, not just find

The sister problem *Segment with Small Sum* asks for the **longest** good
segment; here we want the **count of all** good segments.

The key insight is:

> For a fixed right boundary `r`, let `l` be the leftmost index such that
> `sum(nums[l..r]) ≤ target`. Then **every** subarray ending at `r` with left
> endpoint ≥ `l` is also good, because shrinking a valid window can only
> decrease its sum.

So there are exactly `r - l + 1` good subarrays ending at `r`:

```
[l,   r],
[l+1, r],
...
[r,   r]
```

We accumulate these counts as `r` sweeps from `0` to `n-1`.

---

### 3 — Validity of the shrink step

After we add `nums[r]` to the window and the sum exceeds `target`, we
repeatedly remove `nums[l]` from the left.

Because every element is **non-negative**, each removal strictly decreases
(or keeps the same) the running total, so the inner loop always terminates
with `l ≤ r + 1` (at worst the window becomes empty).

---

### 4 — Comparison with "Number of Segments with Big Sum"

| Property | **Small Sum** (this problem) | **Big Sum** |
|---|---|---|
| Condition | sum **≤** target | sum **≥** target |
| Count per step | `r − l + 1` (subarrays ending at `r`) | `n − r + 1` (subarrays starting at `l`) |
| Outer loop drives | **right** pointer `r` | **left** pointer `l` |
| Inner loop | shrink while sum **>** target | expand while sum **<** target |

Both share the same O(n) skeleton because the monotonicity of non-negative
prefix sums is identical.

---

## Approach — Variable-Width Sliding Window

```
l = 0, r = 0, tot = 0, count = 0

while r < n:
    ┌─ EXPAND ──────────────────────────────────────────────────┐
    │  tot += nums[r]                                           │
    └───────────────────────────────────────────────────────────┘

    ┌─ SHRINK (restore invariant tot ≤ target) ─────────────────┐
    │  while tot > target:                                      │
    │      tot -= nums[l]                                       │
    │      l++                                                  │
    └───────────────────────────────────────────────────────────┘

    ┌─ COUNT ────────────────────────────────────────────────────┐
    │  count += r - l + 1                                       │
    └───────────────────────────────────────────────────────────┘

    r++

return count
```

### Step-by-step explanation

| Phase | Action | Reason |
|-------|--------|--------|
| **Expand** | `tot += nums[r]` | Tentatively include the next element |
| **Shrink** | Remove `nums[l]`, advance `l`, until `tot ≤ target` | Restore the "good window" invariant |
| **Count** | `count += r − l + 1` | All `r − l + 1` subarrays ending at `r` are good |
| **Advance** | `r++` | Move to the next right boundary |

Because `l` and `r` only ever move **rightward**, each element is added to the
window exactly once and removed at most once → **O(n) total operations**.

---

## Solution

```java
class NumberOfSegmentsWithSmallSumImpl implements NumberOfSegmentsWithSmallSum {

    @Override
    public long countOfGoodSegments(int n, long[] nums, long target) {
        long count = 0;   // total number of good segments
        long tot   = 0;   // running sum of the current window [l, r]
        int  l     = 0;   // left  boundary (inclusive)
        int  r     = 0;   // right boundary (inclusive)

        while (r < nums.length) {

            // EXPAND: include nums[r] in the window
            tot += nums[r];

            // SHRINK: restore invariant tot <= target
            while (tot > target) {
                tot -= nums[l];
                l++;
            }

            // COUNT: [l,r], [l+1,r], ..., [r,r] are all good
            count += r - l + 1;

            r++;
        }

        return count;
    }
}
```

---

## Dry Run

### Example 1 — mixed window sizes

```
nums   = [1, 2, 3, 1, 1]
target = 4
```

| r | Action | l | tot | r−l+1 | count |
|---|--------|---|-----|-------|-------|
| 0 | expand: tot=1 | 0 | 1 | 1 | 1 |
| 1 | expand: tot=3 | 0 | 3 | 2 | 3 |
| 2 | expand: tot=6>4 → shrink: remove 1 (tot=5>4) → remove 2 (tot=3) l=2 | 2 | 3 | 1 | 4 |
| 3 | expand: tot=4 | 2 | 4 | 2 | 6 |
| 4 | expand: tot=5>4 → shrink: remove 3 (tot=2) l=3 | 3 | 2 | 2 | 8 |

**Answer: 8**

Verification — all subarrays with sum ≤ 4:

| Subarray | Sum | Good? |
|----------|-----|-------|
| [1] | 1 | ✓ |
| [2] | 2 | ✓ |
| [3] | 3 | ✓ |
| [1] | 1 | ✓ |
| [1] | 1 | ✓ |
| [1,2] | 3 | ✓ |
| [1,1] | 2 | ✓ |
| [1,1] | 2 | ✓ |
| [2,3] | 5 | ✗ |
| [3,1] | 4 | ✓ — wait, counted? |

Let's re-enumerate systematically:

| [l,r] | sum | ≤ 4? |
|-------|-----|------|
| [0,0] | 1   | ✓ |
| [0,1] | 3   | ✓ |
| [0,2] | 6   | ✗ |
| [0,3] | 7   | ✗ |
| [0,4] | 8   | ✗ |
| [1,1] | 2   | ✓ |
| [1,2] | 5   | ✗ |
| [1,3] | 6   | ✗ |
| [1,4] | 7   | ✗ |
| [2,2] | 3   | ✓ |
| [2,3] | 4   | ✓ |
| [2,4] | 5   | ✗ |
| [3,3] | 1   | ✓ |
| [3,4] | 2   | ✓ |
| [4,4] | 1   | ✓ |

**Total: 8 ✓**

---

### Example 2 — entire array fits

```
nums   = [1, 1, 1, 1]
target = 10
```

The sum never exceeds 10, so `l` never moves.

| r | tot | r−l+1 | count |
|---|-----|-------|-------|
| 0 | 1   | 1     | 1     |
| 1 | 2   | 2     | 3     |
| 2 | 3   | 3     | 6     |
| 3 | 4   | 4     | **10** |

**Answer: 10** — exactly `n*(n+1)/2 = 4*5/2 = 10`. When all elements fit, every
subarray is good, and there are `n*(n+1)/2` subarrays in total.

---

### Example 3 — no subarray fits

```
nums   = [5, 6, 7]
target = 4
```

| r | Action | l | tot | r−l+1 | count |
|---|--------|---|-----|-------|-------|
| 0 | expand: tot=5>4 → shrink: remove 5 (tot=0) l=1 | 1 | 0 | 0 | 0 |
| 1 | expand: tot=6>4 → shrink: remove 6 (tot=0) l=2 | 2 | 0 | 0 | 0 |
| 2 | expand: tot=7>4 → shrink: remove 7 (tot=0) l=3 | 3 | 0 | 0 | 0 |

**Answer: 0**

---

## Complexity Analysis

| | Complexity |
|--|--|
| **Time** | **O(n)** — `l` and `r` each traverse the array at most once; every element is added to the window once and removed at most once |
| **Space** | **O(1)** — only scalar variables (`count`, `tot`, `l`, `r`) are used; no auxiliary data structures |

---

## Comparison with Related Problems

| Problem | Goal | Window movement | Answer update |
|---------|------|-----------------|---------------|
| *Segment with Small Sum* | **Longest** subarray with sum ≤ target | Expand right, shrink left | `maxLen = max(maxLen, r−l+1)` |
| **Number of Segments with Small Sum** (this) | **Count** of subarrays with sum ≤ target | Expand right, shrink left | `count += r−l+1` |
| *Number of Segments with Big Sum* | **Count** of subarrays with sum ≥ target | Expand right (inner), shrink left (outer) | `count += n−r+1` |

The only algorithmic difference between *Segment with Small Sum* and this
problem is a single character: `max` becomes `+=`. The sliding-window
structure is identical.

---

## Edge Cases

| Scenario | Behaviour |
|----------|-----------|
| Empty array (`n = 0`) | The outer loop never executes → returns **0** |
| Every element > target | Each element is added then immediately removed; `r − l + 1 = 0` at every step → returns **0** |
| Target ≥ total array sum | `l` never moves; final count = `n*(n+1)/2` |
| Single-element array | Returns **1** if `nums[0] ≤ target`, else **0** |
| Target < 0 | Since all elements are non-negative, `tot ≥ 0 > target` always → window always shrinks to empty → returns **0** |

---

## Potential Pitfalls

1. **Negative numbers**: The monotonicity assumption breaks if any element is
   negative. Adding more elements could *decrease* the sum, so the inner shrink
   loop might terminate prematurely. A different technique (e.g., sorted
   prefix-sum + binary search, or a Fenwick tree) is required in that case,
   typically O(n log n).

2. **Parameter `n` vs `nums.length`**: The parameter `n` is declared in the
   interface signature but the implementation uses `nums.length` for the loop
   bound. They should always agree; prefer `nums.length` to avoid an accidental
   off-by-one if the caller passes an incorrect `n`.

3. **Overflow**: Both `count` and `tot` are declared as `long`. For an array of
   length n ≈ 10⁵ with elements ≈ 10⁹ the worst-case count is n*(n+1)/2 ≈ 5×10⁹,
   which fits comfortably in a `long` (max ≈ 9.2×10¹⁸).

---

## Key Takeaways

- **Non-negative elements** are the prerequisite that validates the sliding-
  window technique for subarray-sum problems.
- **Counting good subarrays** with a sliding window is a one-line change from
  *finding the longest* good subarray: replace `max(maxLen, r−l+1)` with
  `count += r−l+1`.
- The **right pointer drives the outer loop** when we want to count subarrays
  ending at each position. The **left pointer drives the outer loop** for the
  symmetric "big sum" variant (counting subarrays starting at each position).
- Total-count problems of the form "how many subarrays satisfy property P" are
  often solvable with a two-pointer approach whenever P is monotone in the
  window size.

