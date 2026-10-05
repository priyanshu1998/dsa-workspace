# Search in Rotated Sorted Array II (With Duplicates)

## Problem Statement

Given a rotated sorted array `nums` (ascending order before rotation) that **may contain duplicates**, and a target value `x`, determine whether `x` exists in `nums`.

**Example:**
```
nums = [3,1,2,2,2], x = 1   → true
nums = [1,1,1,0,1], x = 0   → true
```

Duplicates break the usual "one half is always sorted" guarantee used in the no-duplicates version, so the algorithm must handle the ambiguous case `nums[l] == nums[r] == nums[mid]` separately.

---

## Solutions

### 1. Naive Solution — O(n) Time, O(1) Space

Simply scan the array linearly and compare each element to `x`.

```java
class NaiveSolution implements SearchInRotatedArray2 {
    @Override
    public boolean search(int[] nums, int x) {
        for (int num : nums) {
            if (num == x) return true;
        }
        return false;
    }
}
```

**Complexity:**
Time: O(n)
Space: O(1)

**Drawback:** Ignores the fact that the array is (rotated) sorted — no binary search speedup is used.

---

### 2. Optimized Solution (Modified Binary Search) — O(log n) average, O(n) worst case, O(log n) Space (recursion)

**Key Insight:** Removing a sorted half does not change the relative ordering `nums[i] ? nums[j]` for all `i, j` in the remaining half. So:

```
T(n) = T(n/2) + O(1),  n > 2
     = O(1),            n <= 2
```

The complication: when `nums[l] == nums[mid] == nums[r]`, we cannot tell which half is sorted. In that case, shrink the window from both ends (`l++`, `r--`) until the ambiguity is resolved — this is what makes the worst case O(n) (e.g. an array of all equal values).

```java
class SearchInRotatedArray2Impl implements SearchInRotatedArray2 {
    private boolean binarySearch(int[] nums, int x, int l, int r) {
        while (l <= r) {
            int mid = (l + r) / 2;
            if (nums[mid] == x) {
                return true;
            } else if (nums[mid] < x) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return false;
    }

    private boolean rotatedSearch(int[] nums, int x, int l, int r) {
        if (nums[l] == x || nums[r] == x) return true;

        // shrink the ambiguous region where both ends are equal
        while (r - l > 2 && nums[l] == nums[r]) {
            l++;
            r--;
        }

        if (r == l) return nums[l] == x;
        if (r - l == 1) return nums[l] == x || nums[r] == x;

        int mid = (l + r) / 2;
        if (nums[l] <= x && x <= nums[mid]) return binarySearch(nums, x, l, mid);       // first half sorted and x fits in it
        else if (nums[l] <= nums[mid]) return rotatedSearch(nums, x, mid + 1, r);        // first half sorted but x not in it
        else if (nums[mid] <= x && x <= nums[r]) return binarySearch(nums, x, mid, r);   // second half sorted and x fits in it
        else if (nums[mid] <= nums[r]) return rotatedSearch(nums, x, l, mid - 1);        // second half sorted but x not in it
        else return false;
    }

    @Override
    public boolean search(int[] nums, int x) {
        return rotatedSearch(nums, x, 0, nums.length - 1);
    }
}
```

**How it works:**
1. Check the boundary elements first (`nums[l]`, `nums[r]`).
2. While both ends are equal and more than 2 elements remain, shrink the window — this strips away the duplicate "noise" that hides which half is actually sorted.
3. Once the window is small (`r == l` or `r - l == 1`), answer directly.
4. Otherwise, find `mid` and determine which half (`[l, mid]` or `[mid, r]`) is sorted:
   - If `x` lies within the sorted half's range, binary search that half directly.
   - Otherwise, recurse into the other half (which must contain `x`, if present).

**Complexity:**
Time: O(log n) average; degrades to O(n) when many duplicates force the shrink loop to scan linearly (e.g. `[1,1,1,1,...,1,2,1,...,1]`)
Space: O(log n) — recursion stack depth

---

### 3. Optimized Solution — Single-Pointer Comparison (Iterative) — O(log n) average, O(n) worst case, O(1) Space

**Key Insight:** You don't need to inspect *both* ends (`nums[l]` and `nums[r]`) to decide which half is sorted — comparing `nums[mid]` against just `nums[l]` is enough:

```
nums[mid] == target        → found
nums[l] == nums[mid]       → ambiguous, can't tell which half is sorted → shrink by one (l++)
nums[l] <  nums[mid]       → left half [l, mid] is sorted
nums[l] >  nums[mid]       → right half [mid, r] is sorted
```

This removes the need for a separate `binarySearch` helper and the two-sided shrink loop, and turns the recursion into a single `while` loop — dropping the space cost from O(log n) (recursion stack) to O(1).

```java
class SinglePointerSolution implements SearchInRotatedArray2 {
    @Override
    public boolean search(int[] nums, int x) {
        int l = 0, r = nums.length - 1;

        while (l <= r) {
            int mid = (l + r) / 2;

            if (nums[mid] == x) return true;

            if (nums[l] == nums[mid]) {
                // ambiguous: can't tell which half is sorted, shrink by one
                l++;
            } else if (nums[l] < nums[mid]) {
                // left half [l, mid] is sorted
                if (nums[l] <= x && x < nums[mid]) {
                    r = mid - 1;
                } else {
                    l = mid + 1;
                }
            } else {
                // right half [mid, r] is sorted
                if (nums[mid] < x && x <= nums[r]) {
                    l = mid + 1;
                } else {
                    r = mid - 1;
                }
            }
        }
        return false;
    }
}
```

**How it works:**
1. Compute `mid`; if it matches `x`, done.
2. If `nums[l] == nums[mid]`, the duplicate hides which half is sorted — make guaranteed progress by discarding just `nums[l]` (`l++`) and re-evaluate.
3. Otherwise exactly one of `[l, mid]` / `[mid, r]` is sorted (strict inequality tells us which) — check if `x` lies in that half's range and narrow accordingly, else move into the other half.

**Complexity:**
Time: O(log n) average; O(n) worst case for the same adversarial duplicate-heavy inputs as before
Space: O(1) — iterative, no recursion stack

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Linear scan, ignores sortedness entirely.
       Time: O(n)    Space: O(1)

           ▼  Observation: removing a sorted half preserves order in the rest

Modified Binary Search (recursive, two-sided shrink) ──────────────────►
       Identify the sorted half via nums[l] vs nums[mid] vs nums[r].
       Binary search the half containing x, recurse into the other half otherwise.
       Duplicates create ambiguity (nums[l] == nums[mid] == nums[r]) — resolved by
       shrinking the window from both ends until the ambiguity clears.
       Time: O(log n) average, O(n) worst case    Space: O(log n)

           ▼  Observation: only nums[l] vs nums[mid] is needed to pick a side;
              ambiguity can be resolved one step at a time instead of two

Single-Pointer Comparison (iterative) ──────────────────────────────────►
       One while-loop, one comparison (nums[l] vs nums[mid]) per iteration.
       Ambiguous case advances l by exactly one instead of shrinking both ends.
       Time: O(log n) average, O(n) worst case    Space: O(1)   ← Best possible space
```

### Why the worst case stays O(n)

No comparison-based algorithm can beat O(n) on adversarial duplicate-heavy inputs. Example: `[1,1,1,...,1,2,1,...,1]` — the position of the lone `2` is indistinguishable from any other index using only boundary comparisons, so in the worst case every element may need to be inspected. This is a theoretical lower bound for this problem, not a weakness of either binary-search variant above.

---

## Complexity Summary

| Solution | Time (avg) | Time (worst) | Space | Notes |
|----------|-----------|--------------|-------|-------|
| Naive | O(n) | O(n) | O(1) | Doesn't exploit sortedness |
| Modified Binary Search (recursive) | O(log n) | O(n) | O(log n) | Two-sided shrink loop + recursion |
| Single-Pointer Comparison (iterative) | O(log n) | O(n) | O(1) | Same time bounds, optimal space |

> **Best solution:** `SinglePointerSolution` (Single-Pointer Comparison) — same average/worst-case time as the recursive version, but O(1) space since it's a plain iterative loop with no recursion stack and no need to track both boundaries.

---

## Formal Proof of Correctness

We prove correctness for `SinglePointerSolution.search`, reproduced here for reference:

```java
int l = 0, r = nums.length - 1;
while (l <= r) {
    int mid = (l + r) / 2;
    if (nums[mid] == x) return true;
    if (nums[l] == nums[mid]) {
        l++;
    } else if (nums[l] < nums[mid]) {
        if (nums[l] <= x && x < nums[mid]) r = mid - 1;
        else l = mid + 1;
    } else {
        if (nums[mid] < x && x <= nums[r]) l = mid + 1;
        else r = mid - 1;
    }
}
return false;
```

### 1. Definitions

Let `nums[0..n-1]` be an array that is a **rotation of a non-decreasing sequence**: there exists a pivot `p ∈ [0, n-1]` such that `nums[p..n-1] ++ nums[0..p-1]` is sorted in non-decreasing order (equivalently, `nums` is formed by right-rotating a sorted array by `p` positions). Duplicates are permitted.

Define the **loop invariant** `I`:

> **I(l, r):** If `x` occurs anywhere in `nums`, then it occurs in the subarray `nums[l..r]`.

Equivalently, the contrapositive: if `x ∉ nums[l..r]`, then `x ∉ nums` at all. This is the property we must preserve on every iteration, since the algorithm only ever decides membership by looking inside `[l, r]`.

### 1.1 Structural Lemma (Two-Run Property)

Since `nums` is a rotation of a sorted array `A[0..n-1]` by `p` positions (`nums[i] = A[(i+p) mod n]`), it splits into (at most) two contiguous, individually non-decreasing runs:

- **Run 1** `nums[0 .. n-p-1] = A[p .. n-1]`
- **Run 2** `nums[n-p .. n-1] = A[0 .. p-1]`

and every element of Run 1 is `≥` every element of Run 2 (Run 1 holds the *larger/later* part of `A`, Run 2 the *smaller/earlier* part). If `p = 0`, Run 2 is empty and `nums` is fully sorted.

Precisely, since `A` is non-decreasing, `min(Run 1) = A[p]` and `max(Run 2) = A[p-1]`, and `A[p-1] ≤ A[p]` (adjacent elements of `A`), so:

```
max(Run 2) ≤ min(Run 1)          (equality only possible if A[p-1] == A[p], a duplicate straddling the rotation boundary)
```

This single inequality is what makes the boundary-tie edge case in Cases C1/D1 below both rare and harmless.

**Lemma:** For any `l ≤ mid`, if `nums[l] < nums[mid]`, then `nums[l..mid]` is entirely non-decreasing (contains no rotation boundary).

*Proof:* Suppose for contradiction that the rotation boundary lies strictly inside `(l, mid]`, i.e., some index `k` with `l < k ≤ mid` is the start of Run 2 (`nums[k]` is the first element of Run 2 appearing after index `l`). Then `nums[l]` belongs to Run 1 and `nums[mid]` belongs to Run 2 (since `mid ≥ k`). By the two-run property, every Run 1 element is `≥` every Run 2 element, so `nums[l] ≥ nums[mid]`, contradicting the hypothesis `nums[l] < nums[mid]`. Hence no boundary lies in `(l, mid]`, so `nums[l..mid]` lies entirely within a single run and is non-decreasing. ∎

By the symmetric argument, `nums[l] > nums[mid]` implies the boundary *does* lie in `(l, mid]`, which forces `nums[mid..r]` (everything from `mid` to the end of the search window, up to the next boundary at or before `r`) to be non-decreasing instead — because `mid` is then necessarily at or past the start of Run 2, and `nums[mid..r]` cannot contain a second boundary (a rotation has exactly one boundary).

### 2. Base Case (Initialization)

Before the first iteration, `l = 0` and `r = n - 1`, so `nums[l..r] = nums[0..n-1]` is the entire array. `I(0, n-1)` holds trivially: if `x` occurs in `nums`, it occurs in all of `nums`.

### 3. Inductive Step (Preservation)

Assume `I(l, r)` holds at the start of an iteration with `l ≤ r`. Let `mid = ⌊(l+r)/2⌋`. We show that whichever branch executes, the invariant holds for the new `(l, r)`.

**Case A — `nums[mid] == x`.**
The algorithm returns `true` immediately. Correct, since `x` is directly observed in the array.

**Case B — `nums[l] == nums[mid]` (ambiguous case), and `nums[mid] != x`.**
The algorithm sets `l ← l + 1`, i.e., discards only index `l` (the old value of `l`, call it `l₀`). We must show `nums[l₀] ≠ x` is already known, so dropping it cannot lose `x`.
- We have `nums[mid] ≠ x` (this case excludes `x`) and `nums[l₀] == nums[mid]`.
- Therefore `nums[l₀] = nums[mid] ≠ x`, so `nums[l₀] ≠ x`.
- Hence if `x` occurs in `nums[l₀..r]`, it must occur in `nums[l₀+1..r]` (the only element removed, `nums[l₀]`, is provably not `x`).
- Combined with `I(l₀, r)` (inductive hypothesis), this gives `I(l₀+1, r)`, i.e., `I(l, r)` for the new `l`. ∎ (Case B)

**Case C — `nums[l] < nums[mid]`.** By the Structural Lemma (§1.1), `nums[l..mid]` lies entirely within one run and is non-decreasing, with `min = nums[l]`, `max = nums[mid]`.

  - **Sub-case C2 (handled first — it's the simple direction) — `x < nums[l]` or `x > nums[mid]`:** `x` lies strictly outside `[nums[l], nums[mid]]`, so by sortedness `x ∉ nums[l..mid]`. Since `I(l, r)` restricts any occurrence of `x` to `nums[l..mid] ∪ nums[mid+1..r]`, and the first part is ruled out, `x` (if present) must lie in `nums[mid+1..r]`. Setting `l ← mid + 1` preserves `I(mid+1, r)`. This needs only sortedness of one half plus set-complement logic — no global run property required.

  - **Sub-case C1 — `nums[l] ≤ x ≤ nums[mid]` (so, combined with Case A already excluding `x = nums[mid]`, effectively `nums[l] ≤ x < nums[mid]`):** We must justify discarding `nums[mid+1..r]` entirely. Let `b` be the global rotation boundary (start index of Run 2; if `nums` has no rotation, treat `b` as nonexistent and this sub-case reduces to plain sorted-array binary search).
    - If `b ∉ (mid, r]` (i.e., `nums[mid+1..r]` contains no boundary and lies wholly within the **same** run as `nums[l..mid]`): then `nums[l..r]` up to `r` is entirely one non-decreasing run, so every index `j > mid` satisfies `nums[j] ≥ nums[mid] > x` (since `x < nums[mid]`, strict because `nums[mid]` already excluded) — `x` cannot appear there. Safe to discard.
    - If `b ∈ (mid, r]` (the boundary falls inside `nums[mid+1..r]`): then `nums[mid+1..r]` splits into a Run-1 continuation `nums[mid+1..b-1]` (if any) and a Run-2 part `nums[b..r]`. Every element of the Run-1 continuation is `≥ nums[mid] > x` (same ascending-run argument), so `x` is not there. Every element of the Run-2 part is `≤ max(Run 2) ≤ min(Run 1) ≤ nums[l] ≤ x`. The only way a Run-2 element could equal `x` is the boundary-tie case `max(Run 2) = min(Run 1) = nums[l] = x`. But in that exact scenario, `x = nums[l]`, which is already contained in the **kept** range `nums[l..mid-1]` (since we retain index `l` itself) — so even though a duplicate of `x` exists in the discarded tail, an occurrence of `x` also survives in the kept range, and `I(l, mid-1)` is not violated.
    - In both branches, `nums[mid+1..r]` can be safely discarded: either `x` provably isn't there, or if it is (only via the boundary-tie duplicate), an equal witness remains at index `l`. Setting `r ← mid - 1` preserves `I(l, mid-1)`.

**Case D — `nums[l] > nums[mid]` (mirror of Case C).** By the symmetric form of the Structural Lemma, `nums[mid..r]` is the non-decreasing half (min `nums[mid]`, max `nums[r]`), and the boundary (if any) lies in `[l, mid)`.

  - **Sub-case D2 (simple direction) — `x < nums[mid]` or `x > nums[r]`:** `x ∉ nums[mid..r]` by sortedness; by the complement argument, `x` (if present) is in `nums[l..mid-1]`. Setting `r ← mid - 1` preserves `I(l, mid-1)`.
  - **Sub-case D1 — `nums[mid] ≤ x ≤ nums[r]` (effectively `nums[mid] < x ≤ nums[r]`, since `x = nums[mid]` was excluded in Case A):** Mirroring C1 — elements of `nums[l..mid-1]` are either part of the same run as `nums[mid..r]` and hence `≤ nums[mid] < x` (ruled out), or part of the other run with values `≥ max(other run) ≥ min(current run) ≥ nums[r] ≥ x`, with the boundary-tie equality case `x = nums[r]` always retained at index `r` itself (which stays in the kept range `nums[mid+1..r]`). Setting `l ← mid + 1` preserves `I(mid+1, r)`.

> Both "value-in-range" sub-cases (C1, D1) rely on the same pattern: the discarded half's elements are either strictly on the wrong side of `x` (safe to drop), or — in the one boundary-tie edge case — equal to `x` but duplicated at an index that is *also* present in the kept half. This is the same duplicate-safety argument as Case B, applied at the run boundary instead of at `l` directly.

In every case, the new `(l, r)` satisfies the invariant, given the inductive hypothesis. ∎ (Inductive step)

### 4. Termination

Each iteration either:
- returns `true` directly (Case A), or
- strictly shrinks the window: `r ← mid - 1 < mid ≤ r` (decreases `r`), `l ← mid + 1 > mid ≥ l` (increases `l`), or `l ← l + 1` (Case B, strictly increases `l`).

Since `mid = ⌊(l+r)/2⌋` satisfies `l ≤ mid ≤ r` whenever `l ≤ r`, every branch other than Case A strictly reduces `r - l`, a non-negative integer that decreases by at least 1 each iteration. By well-ordering of the naturals, the loop terminates after at most `O(n)` iterations, exiting when `l > r`.

### 5. Final Correctness

- **If the loop returns `true`:** it did so in Case A, having directly observed `nums[mid] == x`. Sound.
- **If the loop exits with `l > r`:** the window `nums[l..r]` is empty. By the invariant `I(l, r)` (preserved through every iteration per Step 3, and vacuously true for the empty range since there is nothing to find there and the invariant's conclusion trivially holds), *if* `x` existed anywhere in the original array, it would have to exist in this now-empty window — a contradiction. Hence `x ∉ nums`, and returning `false` is correct.

Therefore, by induction on the number of iterations (invariant preserved at every step, base case holds, and termination guaranteed), the algorithm returns `true` if and only if `x ∈ nums`. **∎**

### 6. Note on the recursive two-sided-shrink version

The recursive `SearchInRotatedArray2Impl` solution satisfies the identical invariant `I(l, r)`, with one additional preliminary step per call: the `while (r - l > 2 && nums[l] == nums[r])` loop. Each iteration of that inner loop discards `nums[l]` and `nums[r]` only after separately checking `nums[l] == x` / `nums[r] == x` beforehand (so no potential match is lost), making it a direct generalization of Case B above (shrinking from both ends instead of one). The rest of the proof (Cases C/D and termination) transfers unchanged, since once the ambiguous equal-boundary prefix/suffix is stripped, the same sorted-half argument applies to the remaining `[l, r]`.
