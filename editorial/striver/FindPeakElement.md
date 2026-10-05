# Find Peak Element

## Problem Statement

Given an array `arr` of integers, find a **peak element** and return its index. An element is a peak if it is strictly greater than its neighbors. For the first and last elements, only one neighbor needs to be considered — the array is conceptually bordered by `-∞` on both sides. If the array has multiple peaks, returning the index of **any** one of them is acceptable. You may assume `arr[i] != arr[i+1]` for all valid `i` (no adjacent duplicates), and the array has at least one element.

**Example:**
```
arr = [1, 2, 3, 4, 5, 6, 7, 8, 5, 1]   → index 7 (value 8) is a peak
arr = [1, 2, 1, 3, 5, 6, 4]           → index 1 (value 2) or index 5 (value 6) are peaks
arr = [-2, -1, 3, 4, 5]               → index 4 (value 5) is a peak (last element, only left neighbor matters)
```

Because the array borders are treated as `-∞`, **a peak is guaranteed to exist** for any non-empty array — this guarantee is exactly what makes a binary search approach possible.

---

## Solutions

### 1. Naive Solution — O(n) Time, O(1) Space

Scan the array once and check each element against its neighbors (treating out-of-bounds neighbors as `-∞`).

```java
class NaiveSolution implements FindPeakElement {
    @Override
    public int findPeakElement(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int left = (i == 0) ? Integer.MIN_VALUE : arr[i - 1];
            int right = (i == arr.length - 1) ? Integer.MIN_VALUE : arr[i + 1];
            if (arr[i] > left && arr[i] > right) return i;
        }
        return -1; // unreachable given the problem's guarantees
    }
}
```

**Complexity:**
Time: O(n)
Space: O(1)

**Drawback:** Checks every element even though the "slope" of the array around any point already tells us which direction leads to a peak — no binary search speedup is used.

---

### 2. Optimized Solution (Binary Search on the Slope) — O(log n) Time, O(1) Space

**Key Insight (the observation that unlocks binary search):** At any index `mid` that is not the last index, compare `arr[mid]` with `arr[mid + 1]`:
- If `arr[mid] < arr[mid + 1]`, the array is "climbing uphill" at `mid` — there must be a peak somewhere to the right (worst case, the last element is a peak because the border is `-∞`). Discard the left half, including `mid`: `l = mid + 1`.
- Otherwise (`arr[mid] > arr[mid + 1]`), the array is "going downhill" at `mid` — `mid` itself could be the peak, or the peak lies further left. It can never be safely discarded, so: `r = mid`.

This works because **a peak is guaranteed to exist on whichever side the slope points toward**, regardless of what the rest of the array looks like — we never need to "see" both halves to know one of them contains an answer.

```java
class FindPeakElementImpl implements FindPeakElement {
    @Override
    public int findPeakElement(int[] arr) {
        int l = 0;
        int r = arr.length - 1;

        while (l < r) {
            int mid = (l + r) / 2;

            if (mid != arr.length - 1 && arr[mid] < arr[mid + 1]) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }

        return r;
    }
}
```

**How it works:**
1. Maintain `[l, r]` as the search window that is guaranteed to contain a peak (initially the whole array — guaranteed because of the `-∞` borders).
2. At each step, compute `mid` and look at the slope between `arr[mid]` and `arr[mid + 1]`.
3. An uphill slope means the right side `(mid, r]` still must contain a peak, so move `l` past `mid`.
4. A downhill slope (or `mid` being the last index) means `mid` is a candidate peak or the peak is to its left, so shrink to `[l, mid]`.
5. The loop ends when `l == r`, which must be a peak. Either variable can be returned at that point — `r` is used here, but see below for why they're interchangeable.

**Formal proof that the loop terminates with `l == r`:**

Define the *variant* `V = r - l` (an integer-valued function of the loop state) and the *invariant* `I: 0 <= l <= r <= arr.length - 1`.

*Claim 1 — `I` holds before every iteration (loop invariant).*

Base case: before the first iteration, `l = 0` and `r = arr.length - 1`, so `0 <= l <= r <= arr.length - 1` holds trivially (the array is non-empty).

Inductive step: assume `I` holds at the start of an iteration in which the guard `l < r` is true, so `l < r` combined with `I` gives `0 <= l < r <= arr.length - 1`. Let `mid = ⌊(l + r) / 2⌋`. Since `l < r`:
```
l = (l + l) / 2 <= (l + r) / 2 = mid        (so l <= mid)
mid = (l + r) / 2 <= (r + r - 1) / 2 < r    (so mid < r, using l <= r - 1)
```
Hence `l <= mid < r` strictly. Two cases:
- **Uphill** (`mid != arr.length-1 ∧ arr[mid] < arr[mid+1]`): new `l' = mid + 1`. Since `mid < r`, `l' = mid + 1 <= r`. Since `mid >= l`, `l' >= l + 1 > l`. New state: `l <= l' <= r = r' <= arr.length - 1`, so `I` holds for `(l', r')`.
- **Downhill** (otherwise): new `r' = mid`. Since `mid >= l`, `r' >= l = l'`. Since `mid < r`, `r' < r`. New state: `0 <= l' = l <= r' <= r <= arr.length - 1`, so `I` holds for `(l', r')`.

In both cases `I` holds after the update, completing the induction. By induction, `I` holds before every iteration. ∎

*Claim 2 — `V` strictly decreases on every iteration that runs.*

From the case analysis above:
- Uphill: `r' - l' = r - (mid + 1) <= r - l - 1` (since `mid >= l`), i.e. `V' <= V - 1`.
- Downhill: `r' - l' = mid - l <= r - 1 - l` (since `mid < r`, so `mid <= r - 1`), i.e. `V' <= V - 1`.

So in either branch, `V' <= V - 1 < V`. ∎

*Claim 3 — the loop terminates with `l == r`.*

By `I`, `V = r - l >= 0` is always a non-negative integer. By Claim 2, `V` strictly decreases by at least `1` every time the loop body executes. A strictly decreasing sequence of non-negative integers cannot run forever — it must reach `0` after at most `V_0 = (arr.length - 1) - 0` iterations (well-ordering of ℕ). When `V = r - l = 0`, we have `l == r`, the guard `l < r` is false, and the loop exits. Since `V` cannot decrease below `0` (by `I`) and cannot skip over `0` (it decreases by at least 1 and is integer-valued, so it hits every value on the way down, including 0, eventually), the loop is guaranteed to terminate, and it can only terminate via the guard becoming false, i.e. with `l == r`. ∎

Since `l == r` always holds at termination, `l` and `r` are interchangeable as the return value — both name the same index.

**Complexity:**
Time: O(log n) — window halves each iteration
Space: O(1) — iterative, two pointers only

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Linear scan comparing every element to both neighbors.
       Time: O(n)    Space: O(1)

           ▼  Observation: the local slope (arr[mid] vs arr[mid+1]) always points
              toward a side that is guaranteed to contain a peak, because the
              array borders act as -∞ — so half the array can be discarded
              without ever inspecting it.

Binary Search on the Slope (iterative) ─────────────────────────────────►
       One comparison per iteration (arr[mid] vs arr[mid + 1]).
       Uphill → discard left half including mid. Downhill → keep mid, discard right half.
       Time: O(log n)    Space: O(1)   ← Same space, far fewer comparisons
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n) | O(1) | Checks every element's neighbors |
| Binary Search on the Slope | O(log n) | O(1) | Exploits the guaranteed-peak-via-slope observation |

> **Best solution:** `FindPeakElementImpl` (Binary Search on the Slope) — same O(1) space as the naive scan, but logarithmic time by discarding the half of the array that the local slope proves cannot be missed.
