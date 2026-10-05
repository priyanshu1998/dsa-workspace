# Find Minimum in Rotated Sorted Array

## Problem Statement

Given a rotated sorted array `nums` (ascending order before rotation, **no duplicates**), find the minimum element.

**Example:**
```
nums = [4,5,6,7,0,1,2]   → 0
nums = [11,13,15,17]     → 11   (0 rotations — array is already sorted)
```

The array is sorted but "broken" at one pivot point. The minimum element is exactly the element at that pivot (the one place where `nums[i] > nums[i+1]`), or `nums[0]` if there is no rotation at all.

---

## Solutions

### 1. Naive Solution — O(n) Time, O(1) Space

Scan the array and track the smallest value seen.

```java
class NaiveSolution implements FindMinimumInRotatedArray {
    @Override
    public int findMinimum(int[] nums) {
        int min = nums[0];
        for (int num : nums) {
            min = Math.min(min, num);
        }
        return min;
    }
}
```

**Complexity:**
Time: O(n)
Space: O(1)

**Drawback:** Ignores the fact that the array is (rotated) sorted — no binary search speedup is used.

---

### 2. Optimized Solution (Binary Search) — O(log n) Time, O(1) Space

**Key Insight:** In a rotated sorted array split into `[l, mid]` and `[mid, r]`, at least one half is always genuinely sorted (ascending). Compare `nums[mid]` with `nums[r]`:

```
nums[mid] <= nums[r]  → the right half [mid, r] is sorted, so the minimum
                         is either nums[mid] itself or lies in [l, mid]
                         → the minimum cannot be to the right of mid, so r = mid

nums[mid] >  nums[r]  → the right half is NOT sorted, meaning the pivot
                         (and therefore the minimum) lies strictly after mid
                         → l = mid + 1
```

Notice `mid` is never discarded in the first case (`r = mid`, not `mid - 1`) because `nums[mid]` could itself be the answer. In the second case `mid` is safely discarded (`l = mid + 1`) because `nums[mid] > nums[r]` proves `nums[mid]` cannot be the minimum.

```java
class FindMinimumInRotatedArrayImpl implements FindMinimumInRotatedArray {
    @Override
    public int findMinimum(int[] nums) {
        int l = 0;
        int r = nums.length - 1;

        while (l < r) {
            int mid = (l + r) / 2;

            if (nums[mid] <= nums[r]) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return nums[l];
    }
}
```

**How it works:**
1. Maintain a window `[l, r]` that is guaranteed to contain the minimum.
2. Compare `nums[mid]` to `nums[r]` (the right boundary, not the left) — this single comparison is enough to decide which side the pivot is on, because at least one of the two halves is always sorted.
3. If `nums[mid] <= nums[r]`, the right half is sorted (no drop within it), so the pivot/minimum must be at `mid` or to its left → shrink to `r = mid`.
4. If `nums[mid] > nums[r]`, the right half contains a drop, so the pivot/minimum lies strictly right of `mid` → shrink to `l = mid + 1`.
5. The loop ends when `l == r`, which converges exactly on the minimum — no duplicates means the comparison is never ambiguous, so the loop invariant always shrinks the window by at least half.

**Complexity:**
Time: O(log n)
Space: O(1)

---

## Formal Proof of Correctness

**Setup.** Let `nums` be an array of `n` distinct integers that is sorted in ascending order and then rotated by some `k` (`0 <= k < n`) positions. Let `p` be the index of the minimum element (the *pivot*): if `k = 0`, `p = 0`; otherwise `p` is the unique index such that `nums[p-1] > nums[p]` (indices mod `n`). We must show that the algorithm's return value `nums[l]` at termination equals `nums[p]`.

**Loop Invariant.** At the start (and end) of every iteration of the `while (l < r)` loop:
> `0 <= l <= p <= r <= n - 1`

i.e., the window `[l, r]` always contains index `p`.

**Base case.** Initially `l = 0` and `r = n - 1`, and `0 <= p <= n - 1` by definition of `p`. The invariant holds.

**Inductive step.** Assume the invariant holds at the start of an iteration, i.e. `l <= p <= r` and `l < r` (so the loop body executes). Let `mid = (l + r) / 2`, so `l <= mid < r`. We show the invariant is preserved whichever branch is taken.

*Case A: `nums[mid] <= nums[r]`.*
Claim: `p <= mid`, so updating `r = mid` preserves `l <= p <= r`.
- Suppose for contradiction `p > mid`. Then the sub-array `nums[mid..r]` contains no rotation point (the pivot `p` lies outside this range), so `nums[mid..r]` is a contiguous, un-rotated segment of the original ascending order, hence sorted ascending: `nums[mid] <= nums[mid+1] <= ... <= nums[r]`. This is consistent with the branch condition and doesn't yet give a contradiction — so instead we show the stronger fact: if `p > mid`, `p` is still inside `[mid, r]` only if `[mid, r]` contains the drop, but we just showed `[mid, r]` is monotonically non-decreasing. Since all elements are distinct and `p` is defined as the *only* index with `nums[p-1] > nums[p]`, a monotonically non-decreasing range `[mid, r]` cannot contain `p` in its interior (`mid < p <= r` is impossible, as that would require a descent inside the range). Therefore `p <= mid`.
- Hence `r = mid` keeps `p` inside `[l, r]`.

*Case B: `nums[mid] > nums[r]`.*
Claim: `p > mid`, so updating `l = mid + 1` preserves `l <= p <= r`.
- `nums[mid] > nums[r]` means the sub-array `nums[mid..r]` is *not* sorted ascending (it decreases somewhere from `mid` to `r`). Since there is exactly one descent in the whole (rotated) array — precisely at index `p` (i.e., `nums[p-1] > nums[p]`) — this descent must occur strictly inside `[mid, r]`, i.e. `mid < p <= r`.
- Hence `p >= mid + 1`, so `l = mid + 1` keeps `p` inside `[l, r]`.

In both cases the invariant `l <= p <= r` holds after the update, and the window strictly shrinks (`r - l` decreases by at least 1 each iteration, since either `r` moves down to `mid < r` or `l` moves up to `mid + 1 > l`), so the loop terminates.

**Termination.** Since `[l, r]` is a bounded integer range that strictly shrinks every iteration and the loop condition is `l < r`, the loop must terminate, and it can only terminate when `l == r`.

**Conclusion.** At termination, `l == r`, and by the invariant `l <= p <= r`, this forces `l = r = p`. The algorithm returns `nums[l] = nums[p]`, which is exactly the minimum of the array by definition of `p`. ∎

**Why distinctness matters:** The proof of Case A relies on `p` being the *unique* descent point. With duplicates, `nums[mid] <= nums[r]` no longer guarantees `[mid, r]` is free of the pivot (e.g. `nums = [3,3,1,3]`), which is exactly why this simple two-way comparison is insufficient for the "with duplicates" variant of this problem (see `SearchInRotatedArray2`, which needs an extra shrink step to handle that ambiguity).

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Linear scan, ignores sortedness entirely.
       Time: O(n)    Space: O(1)

           ▼  Observation: in any rotated sorted array, comparing nums[mid]
              with nums[r] always reveals which half is genuinely sorted —
              no need to touch nums[l] at all, and no ambiguity arises
              since there are no duplicates.

Binary Search ───────────────────────────────────────────────────────►
       One while-loop, one comparison (nums[mid] vs nums[r]) per iteration.
       r = mid when the right half is sorted (mid could be the answer),
       l = mid + 1 when it isn't (mid is proven not to be the answer).
       Time: O(log n)    Space: O(1)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n) | O(1) | Doesn't exploit sortedness |
| Binary Search | O(log n) | O(1) | Exploits the "one half is always sorted" property |

> **Best solution:** `FindMinimumInRotatedArrayImpl` (Binary Search) — O(log n) time with O(1) space, the optimal bound for this problem since no duplicates means every comparison is decisive.
