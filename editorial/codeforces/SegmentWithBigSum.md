# Segment With Big Sum — Editorial

## Problem Statement

Given an integer array `nums` of length `n` and a long integer `target`, find the
**minimum length** of a contiguous subarray (segment) whose element sum is
**≥ target**. Return `-1` if no such segment exists.

---

## Observations

1. **All elements are non-negative.**  
   Because every element is ≥ 0, adding more elements to a window can never
   decrease the running sum. This monotonicity is the key property that makes
   a sliding-window approach valid — once a window is "good" (sum ≥ target),
   extending it further stays good, but we want the *shortest* such window.

2. **Monotone prefix sums.**  
   Since elements are non-negative the prefix-sum array is non-decreasing.
   This means: if a segment `[l, r]` is good, then any segment `[l, r']` with
   `r' > r` is also good. Therefore, for every left boundary `l` we only need
   to find the *smallest* valid right boundary `r`.

3. **Two-pointer feasibility.**  
   Observations 1 & 2 guarantee that both pointers only ever move rightward,
   giving us an O(n) algorithm.

4. **Sentinel for "not found".**  
   Initialising `minLength = nums.length + 1` is a safe upper-bound sentinel:
   any real answer is in `[1, nums.length]`, so after the loop a value of
   `nums.length + 1` means no valid segment was found.

---

## Approach — Variable-Width Sliding Window

```
l = 0, r = 0, tot = 0, minLength = n + 1

while l < n:
    ┌─ EXPAND ──────────────────────────────────────────────────────────┐
    │  while tot < target and r < n:                                    │
    │      tot += nums[r]                                               │
    │      r++                                                          │
    └───────────────────────────────────────────────────────────────────┘
    if tot >= target:
        minLength = min(minLength, r - l)   // candidate answer

    ┌─ SHRINK ──────────────────────────────────────────────────────────┐
    │  tot -= nums[l]                                                   │
    │  l++                                                              │
    └───────────────────────────────────────────────────────────────────┘

return (minLength > n) ? -1 : minLength
```

### Step-by-step explanation

| Step | Action | Why |
|------|--------|-----|
| **Expand** | Advance `r` until `tot ≥ target` or array end | Find the rightmost bound needed for the current left anchor `l` |
| **Record** | `minLength = min(minLength, r − l)` | `r − l` is the smallest window ending *at or before* `r` that starts at `l` and is good |
| **Shrink** | `tot -= nums[l]; l++` | Move the left boundary rightward to look for a shorter good window |

Because `r` never resets, each element enters and leaves the window at most once →
**O(n) total work**.

---

## Dry Run

```
nums   = [2, 6, 4, 3, 6, 8, 9]
target = 20
```

| Iteration | l | r (before expand) | r (after expand) | tot | r−l | minLength |
|-----------|---|-------------------|------------------|-----|-----|-----------|
| 1         | 0 | 0                 | 5                | 21  | 5   | 5         |
| 2         | 1 | 5                 | 5                | 19  | —   | 5         |
| 3         | 2 | 5                 | 6                | 26  | 4   | 4         |
| 4         | 3 | 6                 | 7                | 26  | 4   | 4         |
| 5         | 4 | 7                 | 7                | 23  | 3   | **3**     |
| 6         | 5 | 7                 | 7                | 17  | —   | 3         |
| 7         | 6 | 7                 | 7                | 9   | —   | 3         |

**Answer: 3** (the segment `[6, 8, 9]` sums to 23 ≥ 20).

---

## Complexity Analysis

| | Complexity |
|--|--|
| **Time**  | O(n) — `l` and `r` each traverse the array at most once |
| **Space** | O(1) — only a handful of scalar variables |

---

## ⚠ Bug in the Current Implementation

The return statement has its ternary branches **swapped**:

```java
// ❌ Current (buggy) — returns sentinel when no segment found,
//    and -1 when a valid segment IS found
return minLength > nums.length ? minLength : -1L;
```

```java
// ✅ Correct
return minLength > nums.length ? -1L : minLength;
```

**Impact:** With the buggy code, every test case that *has* a valid answer will
receive `-1`, and every test case with *no* valid answer will receive
`nums.length + 1` instead of `-1`.

---

## Corrected Implementation

```java
@Override
public long shortestGoodSegment(int n, long[] nums, long target) {
    long minLength = nums.length + 1; // sentinel for "not found"
    int l = 0;
    int r = 0;
    long tot = 0;

    while (l < nums.length) {
        // Expand: grow the window until the sum meets the target
        while (tot < target && r < nums.length) {
            tot += nums[r];
            r++;
        }

        // Record candidate if window is good
        if (tot >= target) {
            minLength = Math.min(minLength, r - l);
        }

        // Shrink: remove the leftmost element and advance
        tot -= nums[l];
        l++;
    }

    // ✅ Corrected ternary
    return minLength > nums.length ? -1L : minLength;
}
```

---

## Key Takeaways

- **Non-negative elements** are the prerequisite that makes a single-pass
  sliding window correct for "minimum-length subarray with sum ≥ k" problems.
- For arrays that may contain **negative numbers**, a deque-based prefix-sum
  approach (monotone deque) is required, giving O(n log n).
- Always double-check the sentinel/guard in the return statement — an inverted
  ternary silently produces wrong answers for every input.

