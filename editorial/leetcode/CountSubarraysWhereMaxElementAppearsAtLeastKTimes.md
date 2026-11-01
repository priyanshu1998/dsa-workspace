# Count Subarrays Where Max Element Appears at Least K Times

## Problem Statement

You are given an integer array `nums` and a positive integer `k`. Return the
number of **subarrays** in which the **maximum element of `nums`** appears
**at least `k` times** in that subarray.

A subarray is a contiguous, non-empty sequence of elements within an array.

**Example:**
```
nums = [1, 3, 2, 3, 3], k = 2
Max element M = 3
Valid subarrays: [1,3,2,3], [1,3,2,3,3], [3,2,3], [3,2,3,3], [2,3,3], [3,3]
Answer = 6

nums = [1, 4, 2, 1], k = 3
Max element M = 4 appears only once in the whole array
Answer = 0
```

**Constraints:** `1 ≤ nums.length ≤ 10^5`, `1 ≤ nums[i] ≤ 10^6`, `1 ≤ k ≤ 10^5`.

Note that "the maximum element" means the maximum of the **whole array**,
not the maximum of each subarray. So the problem reduces to: *count
subarrays containing at least `k` copies of a fixed value `M`.*

---

## Solution — Sliding Window ("at least k" counting)

**Key Insight.** "At least `k`" is a **monotonic** property with respect to
extending a subarray to the right: if `nums[l..e]` contains at least `k`
copies of `M`, then so does every `nums[l..e']` with `e' ≥ e`, because adding
elements never removes a copy of `M`.

So for each left boundary `l`, we only need the **smallest** right end `e`
such that `nums[l..e]` contains exactly `k` copies of `M`. Every end from `e`
to `n − 1` is then valid, contributing `n − e` subarrays at once.

And as `l` moves right, that smallest `e` can never move left (dropping an
element from the left can only remove copies of `M`). This is exactly the
shape where a **two-pointer sliding window** works: both pointers move only
forward.

```java
public interface CountSubarraysWhereMaxElementAppearsAtLeastKTimes {
    long countSubarrays(int[] nums, int k);
}

class CountSubarraysWhereMaxElementAppearsAtLeastKTimesImpl implements CountSubarraysWhereMaxElementAppearsAtLeastKTimes {

    private int max(int[] nums){
        int M = nums[0];
        for(int num: nums){
            M = Math.max(M, num);
        }

        return M;
    }

    @Override
    public long countSubarrays(int[] nums, int k) {
        int M = max(nums);

        int r = 0;
        int count = 0;

        long result = 0;

        for(int l=0; l < nums.length; l++) {
            while (r < nums.length && count < k) {
                if (nums[r] == M) count++;
                r++;
            }

            if (count == k) {
                result += (nums.length - r + 1);
            }

            if (nums[l] == M) {
                count--;
            }
        }

        return result;
    }
}
```

**How it works:**
1. Compute `M = max(nums)` in one pass.
2. Maintain a half-open window `[l, r)` and `count` = number of `M`s inside it.
3. For each `l`:
   - **Expand:** advance `r` while `count < k` and elements remain. The loop
     stops as soon as `count` reaches `k`, so the `k`-th copy of `M` sits at
     index `r − 1` — the smallest valid end for this `l`.
   - **Count:** if `count == k`, the valid ends are `r − 1, r, …, n − 1`,
     which is `n − (r − 1) = n − r + 1` subarrays starting at `l`.
   - **Shrink:** before moving to `l + 1`, remove `nums[l]` from the window;
     if it was an `M`, decrement `count`.
4. If `r` hits the end of the array with `count < k`, no subarray starting at
   this `l` (or any later `l`) can be valid, so nothing more is added.

> **Why `long`?** With `n = 10^5`, the number of subarrays can reach
> `n(n+1)/2 ≈ 5 · 10^9`, which overflows `int`.

---

## Step-by-step Trace

```
nums = [1, 3, 2, 3, 3], k = 2, M = 3, n = 5
(window is [l, r), r is exclusive)

l=0: expand r: 1(no) 3(c=1) 2(no) 3(c=2) → r=4, count=2
     valid ends 3..4 → += 5-4+1 = 2          total=2   ([1,3,2,3], [1,3,2,3,3])
     nums[0]=1 ≠ M, count stays 2

l=1: count already 2, no expansion, r=4
     += 5-4+1 = 2                            total=4   ([3,2,3], [3,2,3,3])
     nums[1]=3 = M, count → 1

l=2: expand r: 3(c=2) → r=5, count=2
     += 5-5+1 = 1                            total=5   ([2,3,3])
     nums[2]=2 ≠ M

l=3: count=2, r=5
     += 5-5+1 = 1                            total=6   ([3,3])
     nums[3]=3 = M, count → 1

l=4: r=5 already at end, count=1 < 2 → add nothing

Answer: 6 ✓
```

```
nums = [1, 4, 2, 1], k = 3, M = 4

l=0: r runs to 4, count=1 < 3 → nothing
l=1..3: count only drops, r stays at end → nothing

Answer: 0 ✓
```

---

## Correctness Proof

Let `n = nums.length` and, for a left boundary `l`, let
`e(l)` = the smallest index `e ≥ l` such that `nums[l..e]` contains at least
`k` copies of `M` (or `∞` if none exists).

### Lemma 1 (Valid ends form a suffix)

For a fixed `l`, `nums[l..e]` is valid **iff** `e ≥ e(l)`.

*Proof.* The number of `M`s in `nums[l..e]` is non-decreasing in `e`. So once
it reaches `k` at `e(l)` it stays `≥ k`, and for `e < e(l)` it is `< k` by
minimality. ∎

Hence the number of valid subarrays starting at `l` is `n − e(l)` (or `0` if
`e(l) = ∞`), and the answer is `Σₗ (n − e(l))`.

### Lemma 2 (`e(l)` is non-decreasing)

If `l < l'` then `e(l) ≤ e(l')`.

*Proof.* `nums[l'..e]` is a suffix of `nums[l..e]`, so it contains no more
`M`s. If `nums[l'..e(l')]` has `k` copies, so does `nums[l..e(l')]`, giving
`e(l) ≤ e(l')`. ∎

### Theorem

**Invariant.** At the start of iteration `l`, `count` equals the number of
`M`s in `nums[l..r−1]`, and `r ≤ e(l) + 1` (the window has not overshot the
smallest valid end).

- **Initialization.** `l = 0`, `r = 0`, `count = 0`: the window is empty and
  `r = 0 ≤ e(0) + 1`.
- **Expansion.** The `while` loop adds `nums[r]` and increments `count` when it
  equals `M`, preserving the meaning of `count`. It stops at the first moment
  `count = k` (so `r − 1 = e(l)` by minimality, since `r` never exceeded
  `e(l) + 1`) or when `r = n` with `count < k` (so `e(l) = ∞`). Note `count`
  can never exceed `k`: it only grows inside the loop guarded by `count < k`.
- **Counting.** If `count == k`, the code adds `n − r + 1 = n − e(l)`, which is
  exactly the count from Lemma 1. Otherwise `e(l) = ∞` and it correctly adds `0`.
- **Maintenance.** Removing `nums[l]` (and decrementing `count` if it is `M`)
  makes `count` the number of `M`s in `nums[l+1..r−1]`. By Lemma 2,
  `e(l+1) ≥ e(l) = r − 1`, so `r ≤ e(l+1) + 1` still holds. In the `e(l) = ∞`
  case, `r = n ≤ e(l+1) + 1` trivially.

Each `l` therefore contributes exactly `n − e(l)` (or `0`), and the sum is the
answer. ∎

---

## Alternative — Record Positions of `M`

Store the indices of every `M` in a list `pos`. For a right end `r`, once at
least `k` copies have been seen, any left boundary `≤ pos[size − k]` works, so
add `pos[size − k] + 1`. Same `O(n)` time but `O(n)` extra space — the
two-pointer version above is preferred.

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `max(nums)` | O(n) | O(1) |
| Sliding window (`l` and `r` each move forward at most `n` times) | O(n) | O(1) |
| **Overall** | **O(n)** | **O(1)** |

> The inner `while` does not make this O(n²): `r` is never reset, so across
> the whole run it advances at most `n` times in total.
