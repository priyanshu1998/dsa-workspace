# Number of Segments With Big Sum — Editorial

## Problem Statement

Given an integer array `nums` of length `n` and a long integer `target`, count the
**total number** of contiguous subarrays (segments) whose element sum is
**≥ target**.

---

## Observations

1. **All elements are non-negative (positive).**  
   Because every element is ≥ 0, adding more elements to a window can never
   decrease the running sum. Formally, if `sum([l, r]) ≥ target`, then for any
   `r' > r`, `sum([l, r']) ≥ target` as well. This monotonicity is the
   critical property that allows bulk-counting and makes a two-pointer approach
   correct.

2. **Extension monotonicity → bulk counting.**  
   Once the window `[l, r]` is "good" (sum ≥ target), every extension of that
   window to the right is also good. That means all segments
   `[l, r], [l, r+1], …, [l, n-1]` are simultaneously valid. Instead of
   checking each one individually, we can add all `n - r` valid right
   boundaries in a single O(1) step. This is the key optimisation that takes
   the naïve O(n²) solution to O(n).

3. **Right pointer never resets.**  
   When the left boundary `l` advances (window shrinks), the sum only decreases,
   so the right pointer `r` may need to advance further — but it can never go
   backwards. Each element enters and leaves the window at most once across the
   entire outer loop, giving O(n) total work.

4. **Contrast with "minimum length" variant.**  
   The companion problem (*Segment With Big Sum*) seeks the **shortest** good
   window and records `r - l` when a good window is found before shrinking.
   This problem seeks a **count** of all good windows and instead records
   `n - r + 1` (all valid right boundaries for the current left anchor).

5. **Parameter `n` vs `nums.length`.**  
   The loop bounds use `nums.length` for safety, but the bulk-count formula
   uses the parameter `n`. These must be equal for the result to be correct.
   If the caller passes a value of `n` that differs from the actual array
   length, the count will be wrong. It is safer to use `nums.length` throughout.

---

## Approach — Two-Pointer Sliding Window with Bulk Counting

```
l = 0, r = 0, tot = 0, count = 0

while l < n:
    ┌─ EXPAND ──────────────────────────────────────────────────────────┐
    │  while tot < target and r < n:                                    │
    │      tot += nums[r]                                               │
    │      r++                                                          │
    └───────────────────────────────────────────────────────────────────┘
    if tot >= target:
        count += n - r + 1     // bulk-count all valid right endings

    ┌─ SHRINK ──────────────────────────────────────────────────────────┐
    │  tot -= nums[l]                                                   │
    │  l++                                                              │
    └───────────────────────────────────────────────────────────────────┘

return count
```

### Step-by-step explanation

| Step | Action | Why |
|------|--------|-----|
| **Expand** | Advance `r` until `tot ≥ target` or array end | Find the *smallest* right boundary that makes the window starting at `l` good |
| **Bulk count** | `count += n − r + 1` | All segments `[l, r−1], [l, r], …, [l, n−1]` are valid; count them in O(1) |
| **Shrink** | `tot -= nums[l]; l++` | Move the left boundary to discover new valid windows starting further right |

> **Why `n − r + 1`?**  
> After the expand phase, `r` is the *exclusive* right end of the window
> (i.e. the last element added was `nums[r-1]`). Valid right endpoints are
> `r-1, r, r+1, …, n-1` — that is `(n-1) − (r-1) + 1 = n − r + 1` segments.

---

## Dry Run

```
nums   = [1, 2, 3, 4]
n      = 4
target = 5
```

| Iter | l | r (before expand) | Window after expand | tot | Good? | count += | count |
|------|---|-------------------|---------------------|-----|-------|----------|-------|
| 1    | 0 | 0 | [1, 2, 3] (r=3) | 6 | ✅ | 4−3+1 = **2** | 2 |
| 2    | 1 | 3 | [2, 3, 4] (r=4) | 9 | ✅ | 4−4+1 = **1** | 3 |
| 3    | 2 | 4 | [3, 4] (r=4)    | 7 | ✅ | 4−4+1 = **1** | 4 |
| 4    | 3 | 4 | [4] (r=4)       | 4 | ❌ | 0            | 4 |

**Answer: 4**

The 4 valid segments are: `[1,2,3]` (sum=6), `[1,2,3,4]` (sum=10), `[2,3,4]` (sum=9), `[3,4]` (sum=7).

> Notice that `[0,2]` (Iter 1) counts segments `[l=0, r−1=2]` **and** `[l=0, r=3]` in one step,
> then `[0,3]` is not double-counted because Iter 2 starts at l=1.

---

## Extended Dry Run

```
nums   = [3, 1, 4, 1, 5, 9, 2, 6]
n      = 8
target = 10
```

| Iter | l | r after expand | tot | n−r+1 | count |
|------|---|----------------|-----|--------|-------|
| 0    | 0 | 4              | 9   | ❌     | 0     |

Wait — let's trace more carefully:

| Iter | l | Window [l..r-1]  | tot | ≥ target? | Added  | count |
|------|---|------------------|-----|-----------|--------|-------|
| 1    | 0 | [3,1,4,1,5] r=5  | 14  | ✅        | 8−5+1=**4** | 4 |
| 2    | 1 | [1,4,1,5] r=5    | 11  | ✅        | 8−5+1=**4** | 8 |
| 3    | 2 | [4,1,5] r=5      | 10  | ✅        | 8−5+1=**4** | 12 |
| 4    | 3 | [1,5,9] r=6      | 15  | ✅        | 8−6+1=**3** | 15 |
| 5    | 4 | [5,9] r=6        | 14  | ✅        | 8−6+1=**3** | 18 |
| 6    | 5 | [9,2,6] r=8      | 17  | ✅        | 8−8+1=**1** | 19 |
| 7    | 6 | [2,6] r=8        | 8   | ❌        | 0      | 19 |
| 8    | 7 | [6] r=8          | 6   | ❌        | 0      | 19 |

**Answer: 19**

---

## Complexity Analysis

| | Complexity |
|--|--|
| **Time**  | O(n) — `l` and `r` each traverse the array at most once; the bulk-count step is O(1) per left anchor |
| **Space** | O(1) — only a handful of scalar variables (`l`, `r`, `tot`, `count`) |

---

## ⚠ Potential Bug: `n` Parameter vs `nums.length`

The bulk-count formula uses the **parameter** `n`, while loop bounds use `nums.length`:

```java
// Expand loop uses nums.length — safe, bounded by actual array
while (tot < target && r < nums.length) { ... }

// Count formula uses parameter n — could be wrong if n != nums.length
count += n - r + 1;   // ⚠ should be: count += nums.length - r + 1
```

If a caller passes `n` that does not match `nums.length`, the count will be
incorrect (either under-counting or over-counting, or even negative). The
correct, defensive implementation is:

```java
count += nums.length - r + 1;   // ✅ always consistent
```

---

## Full Implementation

```java
class NumberOfSegmentsWithBigSumImpl implements NumberOfSegmentsWithBigSum {

    @Override
    public long countOfGoodSegments(int n, long[] nums, long target) {
        long count = 0;   // total good segments
        int l = 0;        // left pointer (inclusive)
        int r = 0;        // right pointer (exclusive)
        long tot = 0;     // sum of window [l, r)

        while (l < nums.length) {
            // EXPAND: grow window until sum >= target or we hit the end
            while (tot < target && r < nums.length) {
                tot += nums[r];
                r++;
            }

            // BULK COUNT: every right endpoint from r-1 to n-1 is valid
            if (tot >= target) {
                count += nums.length - r + 1;   // ✅ use nums.length, not n
            }

            // SHRINK: remove leftmost element and advance left pointer
            tot -= nums[l];
            l++;
        }

        return count;
    }
}
```

---

## Key Takeaways

- **Monotonicity of sums** (non-negative elements) is the prerequisite that
  allows bulk-counting and makes the two-pointer approach valid.
- The **bulk-count step** (`count += n − r + 1`) is what distinguishes this
  problem from the "minimum-length" variant; it eliminates the need for an
  inner loop over all valid right boundaries.
- For arrays with **negative numbers**, neither this approach nor a simple
  sliding window works. A prefix-sum combined with a sorted/monotone structure
  would be required.
- Always prefer `nums.length` over a separately passed length parameter to
  avoid subtle off-by-one or mismatch bugs.

