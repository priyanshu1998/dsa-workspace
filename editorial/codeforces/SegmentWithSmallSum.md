# Segment With Small Sum — Editorial

## Problem Statement

Given an array `nums` of length `n` (all elements **non-negative**) and a long
integer `target`, find the **maximum length** of a contiguous subarray (segment)
whose element sum is **≤ target**.

Return `0` if every single element is already greater than `target`.

---

## Observations

1. **All elements are non-negative.**  
   Adding more elements to a window can only keep the sum the same or increase
   it — it can never decrease it. This monotonicity is the key property that
   unlocks a sliding-window approach:
   - If a window `[l, r]` is **invalid** (sum > target), adding `nums[r+1]`
     will only make it more invalid; we must shrink from the left.
   - If a window `[l, r]` is **valid** (sum ≤ target), removing `nums[l]` keeps
     it valid (or makes it more valid); we never need to go back.

2. **Monotone window sum.**  
   Fixing the left boundary `l`, the window sum increases as `r` moves right.
   Fixing the right boundary `r`, the window sum decreases as `l` moves right.
   This two-dimensional monotonicity means both pointers only ever move
   **rightward** — the classic two-pointer / sliding-window invariant.

3. **Longest vs. shortest.**  
   Unlike "minimum-length subarray with sum ≥ target", here we want the
   *maximum length* window that stays *below* the target.  
   The strategy is: always try to expand right; only shrink when the constraint
   is violated. The largest valid window seen at any point is the answer.

4. **Zero initialisation is safe.**  
   `maxLength = 0` is the correct sentinel because a segment of length 0 (empty)
   trivially has sum 0 ≤ target. If no single element satisfies the constraint
   the answer is genuinely 0 (or the problem guarantees at least one valid
   element — either way, 0 is safe).

---

## Approach — Variable-Width Sliding Window (Expand-then-Shrink)

```
l = 0, r = 0, sum = 0, maxLength = 0

while r < n:
    ┌─ EXPAND ──────────────────────────────────────────────────────────┐
    │  sum += nums[r]                                                   │
    └───────────────────────────────────────────────────────────────────┘

    ┌─ SHRINK (restore invariant) ──────────────────────────────────────┐
    │  while sum > target:                                              │
    │      sum -= nums[l]                                               │
    │      l++                                                          │
    └───────────────────────────────────────────────────────────────────┘

    maxLength = max(maxLength, r - l + 1)   // candidate answer
    r++

return maxLength
```

### Step-by-step explanation

| Step | Action | Why |
|------|--------|-----|
| **Expand** | `sum += nums[r]` | Tentatively include the next element |
| **Shrink** | Remove `nums[l]` and advance `l` until `sum ≤ target` | Restore the "good segment" invariant |
| **Record** | `maxLength = max(maxLength, r − l + 1)` | `[l, r]` is the longest valid window ending at `r` after shrinking |
| **Advance** | `r++` | Move to the next candidate right boundary |

Because neither `l` nor `r` ever moves backwards, each element is added to the
window exactly once and removed at most once → **O(n) total work**.

---

## Dry Run

```
nums   = [2, 1, 5, 2, 3, 2]
target = 7
```

| r  | Action            | l | sum | r−l+1 | maxLength |
|----|-------------------|---|-----|-------|-----------|
| 0  | expand: sum=2     | 0 | 2   | 1     | 1         |
| 1  | expand: sum=3     | 0 | 3   | 2     | 2         |
| 2  | expand: sum=8>7 → shrink l=1 sum=6 | 1 | 6 | 2 | 2 |
| 3  | expand: sum=8>7 → shrink l=2 sum=7 | 2 | 7 | 2 | 2 |
| 4  | expand: sum=10>7 → shrink l=3 sum=5 | 3 | 5 | 2 | 2 |
| 5  | expand: sum=7     | 3 | 7   | 3     | **3**     |

**Answer: 3** (the segment `[2, 3, 2]` sums to 7 ≤ 7).

---

## Second Dry Run — all elements fit

```
nums   = [1, 2, 1, 1]
target = 10
```

The sum never exceeds 10, so `l` never moves.

| r | sum | maxLength |
|---|-----|-----------|
| 0 | 1   | 1         |
| 1 | 3   | 2         |
| 2 | 4   | 3         |
| 3 | 5   | **4**     |

**Answer: 4** (the whole array).

---

## Complexity Analysis

|            | Complexity |
|------------|------------|
| **Time**   | O(n) — `l` and `r` each traverse the array at most once |
| **Space**  | O(1) — only a handful of scalar variables (`sum`, `l`, `r`, `maxLength`) |

---

## Comparison with the "Big Sum" Variant

| Property | Segment With **Small** Sum | Segment With **Big** Sum |
|----------|---------------------------|--------------------------|
| Goal | **Longest** segment with sum **≤** target | **Shortest** segment with sum **≥** target |
| Expand when | Always (one step per outer loop) | Sum is still below target |
| Shrink when | Sum **>** target (constraint violated) | After recording a valid window |
| Sentinel | `maxLength = 0` | `minLength = n + 1` |
| No-answer value | `0` | `-1` |

Both problems share the same O(n) two-pointer skeleton because the underlying
monotonicity of non-negative prefix sums is identical.

---

## Key Takeaways

- **Non-negative elements** are the prerequisite that validates the sliding-window
  technique for subarray-sum optimisation problems.
- **Expand-then-shrink** (used here) is natural for *maximum-length* windows:
  try to keep the window as wide as possible, only pulling the left pointer in
  when forced.
- **Shrink-then-expand** is natural for *minimum-length* windows: aggressively
  pull the left pointer in as soon as the target is met.
- For arrays with **negative numbers**, the monotonicity assumption breaks and a
  more complex data structure (e.g., a monotone deque over prefix sums) is needed,
  typically O(n log n).

