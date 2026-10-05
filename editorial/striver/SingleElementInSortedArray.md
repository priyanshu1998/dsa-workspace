# Single Element in a Sorted Array

## Problem Statement

You are given a sorted array `nums` of integers where **every element appears exactly twice, except for one element which appears exactly once**. Find and return that single element.

**Example:**
```
nums = [1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6]   → 4
nums = [1, 1, 3, 5, 5]                     → 3
nums = [1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7] → 7
```

Because the array is sorted, duplicate values are always adjacent. The array always has **odd length** (an even number of "doubled" elements plus the one singleton), and the singleton index `s` can be anywhere from `0` to `n - 1`.

---

## Solutions

### 1. Naive Solution — O(n) Time, O(1) Space

XOR every element together. Since `x ^ x = 0` and `x ^ 0 = x`, every duplicated pair cancels out, leaving only the singleton.

```java
class NaiveSolution implements SingleElementInSortedArray {
    @Override
    public int singleNonDuplicate(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }
}
```

**Complexity:**
Time: O(n)
Space: O(1)

**Drawback:** It touches every element even though the array is sorted — sortedness lets us detect, without scanning linearly, which half of the array the singleton must be in, enabling a binary search.

---

### 2. Optimized Solution (Binary Search on Pairing Parity) — O(log n) Time, O(1) Space

**Key Insight.** Because the array is sorted, every duplicated value occupies two *adjacent* positions. Before the singleton's index `s`, these adjacent pairs are "aligned" and start at **even** indices: `(0,1), (2,3), (4,5), ...`. After `s`, one slot has been "used up" by the singleton, so the remaining pairs shift by one and start at **odd** indices instead. This parity flip is exactly what lets us binary search:

- Force `mid` to always be **even** (decrement it if it's odd).
- If `nums[mid] == nums[mid + 1]`, the pair at `mid` is still aligned, so the singleton has not been reached yet — it must lie strictly after this pair: `l = mid + 2`.
- Otherwise, the pair at `mid` is already "shifted" (or `mid` *is* the singleton), so the singleton is at `mid` or to its left: `r = mid`.

```java
class SingleElementInSortedArrayImpl implements SingleElementInSortedArray {

    @Override
    public int singleNonDuplicate(int[] nums) {
        int l = 0;
        int r = nums.length - 1;

        while (l < r) {
            int mid = (l + r) / 2;

            if (mid % 2 == 1) mid--;

            if (nums[mid] == nums[mid + 1]) {
                l = mid + 2;
            } else {
                r = mid;
            }
        }

        return nums[l];
    }
}
```

**How it works:**
1. `n = nums.length` is always odd, so `r = n - 1` starts out even, and `l = 0` is even too.
2. At each step, `mid` is forced to be even before comparing `nums[mid]` with `nums[mid + 1]`.
3. A matching pair means the singleton is further right, so the search window jumps past the pair (`l = mid + 2`, preserving evenness of `l`).
4. A mismatched pair means the singleton is at or before `mid`, so the window shrinks to `[l, mid]` (preserving evenness of `r`).
5. The loop ends when `l == r`; that index holds the singleton.

---

## Correctness Proof

### Lemma (Pairing Parity): for every even index `e` with `0 <= e <= n - 2`, `nums[e] == nums[e + 1]` **iff** `s > e + 1`, where `s` is the index of the singleton.

*Proof by strong induction on `e`, taken over even values `0, 2, 4, ...` in increasing order.*

**Base case (`e = 0`):** Suppose `s > 1`, i.e., the singleton is not at index `0` or `1`. In a sorted array, every value's two occurrences are adjacent. The smallest value in the array occupies the first position(s); since the singleton is not among the first two positions, the value at index `0` must be the doubled smallest value, whose second copy is forced (by sortedness — no smaller or equal value can appear elsewhere) to sit at index `1`. Hence `nums[0] == nums[1]`. Conversely, if `s <= 1`, index `0` or `1` is the singleton itself, so the pair is broken: `nums[0] != nums[1]`.

**Inductive step:** Assume the lemma holds for all even indices `0, 2, ..., e` (i.e., assume `nums[2i] == nums[2i+1]` whenever `s > 2i + 1`, for all `i <= e/2`). Show it holds for `e' = e + 2`.

Suppose `s > e' + 1 = e + 3`. Since `s > e + 1` as well (because `e + 3 > e + 1`), the inductive hypothesis gives `nums[2i] == nums[2i+1]` for every `i <= e/2`, so positions `0` through `e + 1` form `(e+2)/2` complete, adjacent, sorted pairs — i.e., every value up to and including `nums[e+1]` has already been fully accounted for. The next unprocessed position is `e + 2`. Because `s > e + 3`, neither `e + 2` nor `e + 3` is the singleton, so by the same sortedness argument as the base case, `nums[e+2]` must pair with `nums[e+3]`: `nums[e+2] == nums[e+3]`.

Conversely, if `s <= e' + 1 = e + 3`, then either `s <= e + 1` (in which case, by the contrapositive of the inductive hypothesis, the pairing is already broken no later than position `e`, which propagates: once a pair is broken, sortedness forces every subsequent adjacent "pair" at even offsets to be a mismatch until the singleton is passed) or `s \in \{e+2, e+3\}`, which directly breaks the pair `(e+2, e+3)`. Either way `nums[e+2] != nums[e+3]`.

By induction, the lemma holds for all even `e`. ∎

### Loop Invariant

Define the invariant `I`: `l` and `r` are both even, `0 <= l <= r <= n - 1`, and `l <= s <= r`.

**Initialization:** `l = 0` (even), `r = n - 1`. Since `n` is odd, `n - 1` is even. The singleton index `s` satisfies `0 <= s <= n - 1` trivially. So `I` holds before the first iteration.

**Maintenance:** Assume `I` holds and the loop guard `l < r` is true. Since `l, r` are even and `l < r`, we have `l <= r - 2`. Let `mid0 = (l + r) / 2`; after the parity fix, `mid` is the largest even number `<= mid0`, so `mid` is even and `l <= mid <= r - 1 < r`, giving `l <= mid < r` (in fact `mid <= r - 1`, and since both are even, `mid <= r - 2`).

- **Case `nums[mid] == nums[mid+1]`:** By the Lemma, `s > mid + 1`, i.e., `s >= mid + 2`. Set `l' = mid + 2`. Then `l' <= r` (since `mid <= r - 2`), `l'` is even, and `l' <= s` by the above, while `s <= r` is unchanged. So `I` holds for `(l', r)`.
- **Case `nums[mid] != nums[mid+1]`:** By the Lemma, `s <= mid`. Set `r' = mid`. Then `r' >= l` (since `mid >= l`), `r'` is even, and `s <= r'` by the above, while `l <= s` is unchanged. So `I` holds for `(l, r')`.

In both cases `I` holds after the update. By induction, `I` holds before every iteration. ∎

### Termination

Define the variant `V = r - l`. By `I`, `V` is a non-negative even integer (difference of two evens). From the maintenance proof above, each branch produces a new window with `V' <= V - 2` (since `mid <= r - 2` and `mid >= l`, both updates shrink the window by at least `2`). A strictly decreasing sequence of non-negative integers must reach `0` in finitely many steps (well-ordering of ℕ), so the loop terminates with `l == r`. By `I`, at that point `l <= s <= r = l`, forcing `s = l = r`. Hence `nums[l]` is exactly the singleton, and the algorithm returns the correct value. ∎

**Complexity:**
Time: O(log n) — the window shrinks by at least `2` every iteration.
Space: O(1) — iterative, two pointers only.

---

## Optimization Journey

```
Naive (XOR) ──────────────────────────────────────────────────────────►
       Cancels every duplicate pair via XOR, scanning once.
       Time: O(n)    Space: O(1)

           ▼  Observation: sortedness forces duplicate pairs to start at even
              indices before the singleton, and at odd indices after it — this
              parity flip is detectable at a single even "mid" per step, so
              half the array can be discarded without inspecting it.

Binary Search on Pairing Parity ───────────────────────────────────────►
       One comparison per iteration (nums[mid] vs nums[mid + 1], mid forced even).
       Matched pair → singleton is further right, jump past it (l = mid + 2).
       Mismatched pair → singleton is at or before mid, shrink right (r = mid).
       Time: O(log n)    Space: O(1)   ← Same space, far fewer comparisons
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (XOR) | O(n) | O(1) | Cancels duplicate pairs via XOR |
| Binary Search on Pairing Parity | O(log n) | O(1) | Exploits the even/odd pairing-parity flip around the singleton |

> **Best solution:** `SingleElementInSortedArrayImpl` (Binary Search on Pairing Parity) — same O(1) space as the XOR scan, but logarithmic time by exploiting the sorted structure to discard half the array each step.
