# Reverse Pairs

## Problem Statement

Given an integer array `nums` of length `n`, count the number of **reverse pairs**: pairs of indices `(i, j)` such that `0 <= i < j < n` and `nums[i] > 2 * nums[j]`.

**Example:**
```
nums = [1, 3, 2, 3, 1]
Reverse pairs: (1,4) since 3 > 2*1, (3,4) since 3 > 2*1  → answer = 2

nums = [2, 4, 3, 5, 1]
Reverse pairs: (1,4) since 4 > 2*1, (2,4) since 3 > 2*1, (3,4) since 5 > 2*1 → answer = 3
```

Note this is **not** the same condition as a classic inversion (`nums[i] > nums[j]`) — the factor of `2` means a simple adaptation of merge-sort-based inversion counting is needed, and the comparison step cannot be folded into the merge step itself (it needs its own pass).

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(1) Space

Check every pair `(i, j)` with `i < j` directly.

```java
class NaiveSolution implements ReversePairs {
    @Override
    public int reversePairs(int[] nums) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if ((long) nums[i] > 2L * nums[j]) {
                    count++;
                }
            }
        }
        return count;
    }
}
```

**Complexity:**
Time: O(n²)
Space: O(1)

**Drawback:** Once `nums[l..mid)` and `nums[mid..r)` are each individually sorted, counting cross pairs between them does not require comparing every `(i, j)` combination — the sortedness lets a linear two-pointer scan do the same job, which the naive solution never exploits.

---

### 2. Optimized Solution (Merge Sort / Divide and Conquer) — O(n log n) Time, O(n) Space

**Key Insight:** Split `nums[l..r)` at `mid`. Every reverse pair `(i, j)` with `i < j` falls into exactly one of three disjoint categories:
- both indices in `[l, mid)` → counted by recursing on the left half,
- both indices in `[mid, r)` → counted by recursing on the right half,
- `i` in `[l, mid)` and `j` in `[mid, r)` ("split"/"cross" pairs) → counted by a dedicated merge step.

If each recursive call also leaves its subarray **sorted**, the cross-pair count for `[l, mid)` vs `[mid, r)` can be computed with a two-pointer scan in O(n) instead of O(n²), because sortedness makes the pair-condition monotonic (see proof below). The actual merging of the two sorted halves into one sorted `[l, r)` run is done in the same pass, which is exactly the merge step of merge sort — so this algorithm is merge sort instrumented to also count cross pairs before merging.

```java
public interface ReversePairs {
    int reversePairs(int[] nums);
}

class ReversePairsImpl implements ReversePairs {

    private int conquer(int[] nums, int l, int mid, int r) {
        int[] aux = new int[r - l];
        int inv = 0;

        // Count split pairs: for each i in [l, mid), count j in [mid, r)
        // such that nums[i] > 2 * nums[j]. Both halves are already sorted
        // ascending at this point, which is what makes the pointer `j`
        // advance monotonically as `i` advances (never needs to reset).
        for (int i = l, j = mid; i < mid; i++) {
            while (j < r && nums[i] > 2L * nums[j]) {
                j++;
            }
            inv += j - mid;
        }

        // Standard merge of the two sorted halves into aux
        int i = l, j = mid, k = 0;
        while (i < mid && j < r) {
            if (nums[i] <= nums[j]) {
                aux[k++] = nums[i++];
            } else {
                aux[k++] = nums[j++];
            }
        }
        System.arraycopy(nums, i, aux, k, mid - i);
        System.arraycopy(nums, j, aux, k, r - j);

        System.arraycopy(aux, 0, nums, l, r - l);
        return inv;
    }

    private int divideAndConquer(int[] nums, int l, int r) {
        if (r - l == 1) {
            return 0; // a single element has no pairs
        }

        int mid = (l + r) / 2;
        int countL = divideAndConquer(nums, l, mid);
        int countR = divideAndConquer(nums, mid, r);
        int countMerged = conquer(nums, l, mid, r);

        return countL + countMerged + countR;
    }

    @Override
    public int reversePairs(int[] nums) {
        return divideAndConquer(nums, 0, nums.length);
    }
}
```

**How it works:**
1. `divideAndConquer(nums, l, r)` recursively sorts `nums[l..mid)` and `nums[mid..r)` in place (as a side effect of the recursive calls) and obtains `countL` and `countR`, the reverse-pair counts fully inside each half.
2. `conquer` then scans both already-sorted halves with two pointers `i` (left) and `j` (right) to count split pairs, merges the two sorted runs into `aux`, and writes the merged, fully sorted run back into `nums[l..r)`.
3. The total for `[l, r)` is `countL + countMerged + countR`.
4. Base case: a window of size 1 (`r - l == 1`) contains no pairs, so it returns 0 (and is trivially sorted already).

---

### Correctness Proof

We prove two things: (A) the two-pointer scan in `conquer` correctly counts all split pairs in O(n) instead of O(n²), and (B) the overall recursion counts every reverse pair in `nums` exactly once.

**Lemma 1 (Monotonicity of the threshold pointer).**
Suppose `nums[l..mid)` and `nums[mid..r)` are each sorted in non-decreasing order when `conquer` is invoked (this is guaranteed by induction on the recursive calls — see Claim B2 below). For a fixed `i`, let `f(i)` be the number of indices `j` in `[mid, r)` with `nums[i] > 2 * nums[j]`. Then as `i` increases, the smallest right-pointer position `j` such that `nums[i] > 2*nums[j]` fails to hold for all `j' >= j` is non-decreasing.

*Proof.* Because `nums[mid..r)` is sorted ascending, `2 * nums[j]` is also non-decreasing in `j`. Hence the predicate `nums[i] > 2*nums[j]` is "monotone in `j`": once it becomes false at some `j`, it stays false for all larger `j` (since the right-hand side only grows). So for fixed `i`, the set of valid `j` is exactly a prefix `[mid, j_stop(i))` of the right half, where `j_stop(i)` is the first index at which the predicate fails.

Now compare `i` and `i' = i + 1`. Since `nums[l..mid)` is sorted ascending, `nums[i'] >= nums[i]`. For any `j`, `nums[i] > 2*nums[j]` implies `nums[i'] >= nums[i] > 2*nums[j]`, so every `j` valid for `i` is also valid for `i'`. Hence `j_stop(i') >= j_stop(i)`: the boundary never moves backward as `i` increases. ∎

**Consequence.** The `while (j < r && nums[i] > 2L * nums[j]) j++;` loop in `conquer` advances `j` to exactly `j_stop(i)` for the current `i`, and by Lemma 1, `j` never needs to move backward when `i` advances to `i+1`. Across the whole `for` loop, `j` starts at `mid` and only ever increases, up to at most `r`. So the total work across all iterations of `i` is `O((mid - l) + (r - mid)) = O(r - l)`, i.e. linear, not quadratic — and `inv += j - mid` correctly adds `f(i)` (the count of valid `j` for this `i`, which is precisely `j_stop(i) - mid`) for every `i`.

**Claim B1 (Partition of pairs).** For the window `[l, r)`, every pair of indices `(i, j)` with `l <= i < j < r` falls into exactly one of:
- (a) `l <= i < j < mid` (both in left half),
- (b) `mid <= i < j < r` (both in right half),
- (c) `l <= i < mid <= j < r` (split across halves).

This is a partition (exhaustive and mutually exclusive) because `i < j` and `mid` is a single fixed cut point: either both indices are `< mid`, both are `>= mid`, or exactly one is on each side with the one `< mid` necessarily being `i` (since `i < j`).

**Claim B2 (Induction on recursion).** For every call `divideAndConquer(nums, l, r)`:
1. It returns the exact count of reverse pairs `(i, j)` with `l <= i < j < r` under the **original** relative values present in `nums[l..r)` at the time of the call, and
2. after the call returns, `nums[l..r)` contains the same multiset of values, now sorted in non-decreasing order.

*Proof by strong induction on `r - l`.*

*Base case* `r - l == 1`: no pairs exist (needs two distinct indices), so returning 0 is correct. A single-element range is trivially sorted. Both parts of the claim hold.

*Inductive step:* assume the claim holds for all window sizes smaller than `r - l` (in particular for `mid - l` and `r - mid`, both `< r - l` since `l < mid < r`). By the inductive hypothesis applied to `divideAndConquer(nums, l, mid)`:
- `countL` is exactly the number of reverse pairs of category (a), and
- `nums[l..mid)` is sorted and holds the same values as before, just reordered.

Symmetrically for `divideAndConquer(nums, mid, r)`: `countR` is exactly the count for category (b), and `nums[mid..r)` ends up sorted with the same multiset as before.

Crucially, **sorting within a half does not change category-(c) pair counts.** A category-(c) pair is determined only by *which value is in the left half* and *which value is in the right half* — any left index is automatically `<` any right index regardless of how the values are internally permuted within each half (the left block of indices and right block of indices don't overlap and keep their relative block order). So the number of pairs `(a, b)` with `a` drawn from the multiset of left-half values, `b` drawn from the multiset of right-half values, and `a > 2b`, is an invariant of the two multisets — it does not depend on the internal order within each half. Therefore this count can be computed *after* both halves are individually sorted, and it will equal the count that would have been obtained from the original unsorted halves.

By Lemma 1 and its consequence, `conquer(nums, l, mid, r)` computes exactly this quantity (`countMerged`) in O(r - l) time, since at the time `conquer` is called, `nums[l..mid)` and `nums[mid..r)` are both sorted ascending (guaranteed by the inductive hypothesis). `conquer` additionally merges the two sorted halves into a fully sorted `nums[l..r)` containing the same combined multiset — standard two-pointer merge correctness (every element from both input runs is copied exactly once, in non-decreasing order).

By Claim B1, `countL + countMerged + countR` sums the pair counts over all three disjoint, exhaustive categories, so it equals the total number of reverse pairs in `[l, r)`. This establishes part 1 of the claim for this call, and the merge establishes part 2. ∎

**Conclusion.** The top-level call `divideAndConquer(nums, 0, nums.length)` satisfies Claim B2 for the full array, so it returns the exact number of reverse pairs in `nums`. ∎

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Check every pair (i, j) directly.
       Time: O(n²)    Space: O(1)

           ▼  Observation: if nums[l..mid) and nums[mid..r) are each sorted,
              the cross-pair condition nums[i] > 2*nums[j] is monotone in j
              for fixed i, and non-decreasing in the threshold as i grows
              (Lemma 1) — so a two-pointer scan counts all cross pairs in
              O(n) instead of O(n²), and this scan can be fused into the
              merge step merge sort already performs.

Merge Sort with Cross-Pair Counting ─────────────────────────────────►
       Divide and conquer: recurse on both halves, count split pairs with
       a two-pointer scan while merging the two sorted halves.
       Time: O(n log n)    Space: O(n)   ← auxiliary array for merging
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n²) | O(1) | Compares every pair directly |
| Merge Sort with Cross-Pair Counting | O(n log n) | O(n) | Exploits sortedness of halves for a linear two-pointer cross-count |

> **Best solution:** `ReversePairsImpl` (Merge Sort with Cross-Pair Counting) — trades O(n) auxiliary space for a drop from quadratic to log-linear time, by recognizing that counting cross pairs between two *sorted* halves only needs a monotonic two-pointer scan, not an exhaustive comparison.
