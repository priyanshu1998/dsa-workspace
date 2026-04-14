# Count Subarrays with Product Less Than Target

## Problem Statement

Given an integer array `nums` and a long integer `target`, count the number of **contiguous subarrays** whose product of all elements is **strictly less than** `target`.

**Example:**
```
nums = [1, 2, 3, 4], target = 10

All subarrays and their products:
[1]=1, [2]=2, [3]=3, [4]=4          → all < 10 ✓
[1,2]=2, [2,3]=6, [3,4]=12          → 12 ✗
[1,2,3]=6, [2,3,4]=24               → 24 ✗
[1,2,3,4]=24                        → ✗

Valid subarrays: 7  →  answer = 7
```

---

## Solutions

### 1. Naive Solution — O(n³) Time, O(1) Space

The most straightforward approach: enumerate **every possible subarray** using two indices `i` (start, inclusive) and `j` (end, exclusive), then recompute the product from scratch for each one using a helper `check` method.

```java
class NaiveSolutions implements CountSubarrayProduct {

    private boolean check(int[] nums, int l, int r, long target) {
        long product = 1;
        for (int i = l; i < r; i++) {
            product *= nums[i];
            if (product >= target) {
                return false;   // early exit — already exceeded target
            }
        }
        return true;
    }

    @Override
    public long countSubarrayProduct(int[] nums, long target) {
        long count = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j <= nums.length; j++) {
                if (check(nums, i, j, target)) {
                    count++;
                }
            }
        }
        return count;
    }
}
```

**How it works:**
- The outer two loops generate every `(i, j)` pair → `O(n²)` pairs.
- For each pair, `check` iterates the subarray `[i, j)` and multiplies elements one by one → `O(j − i)` work per call.
- Summing over all pairs gives `O(n³)` total work.

**Complexity:**
- Time: O(n³)
- Space: O(1)

**Drawback:** The product for subarray `[i, j+1)` is simply `product([i, j)) * nums[j]`, but `check` recomputes the entire product from scratch every time. This repeated work is the root cause of the cubic time.

---

### 2. Optimized Solution (Variable Sliding Window) — O(n) Time, O(1) Space

**Key Insight:** Instead of recomputing the product for every subarray independently, maintain a **running product** over a sliding window `[l, r]`. When the product exceeds the target, **shrink the window from the left** until it is valid again.

```java
class OptimizedSolution implements CountSubarrayProduct {

    @Override
    public long countSubarrayProduct(int[] nums, long target) {
        long product = 1;
        int count = 0;

        int l = 0;
        int r = 0;

        while (r < nums.length) {
            product *= nums[r];                       // expand window to the right

            while (l < nums.length && product >= target) {
                product /= nums[l];                   // shrink window from the left
                l++;
            }

            count += r - l + 1;                       // all subarrays ending at r are valid
            r++;
        }

        return count;
    }
}
```

**How it works:**

1. Expand `r` one element at a time, multiplying it into `product`.
2. If `product >= target`, divide out the leftmost element (`nums[l]`) and advance `l` until `product < target` again.
3. At this point, **every subarray ending at `r` with a left boundary between `l` and `r` is valid**:
   ```
   [l..r],  [l+1..r],  [l+2..r],  ...,  [r..r]
   ```
   That is exactly `r − l + 1` subarrays, all with product `< target`.
4. Accumulate this count before advancing `r`.

**Why the inner `while` doesn't make this O(n²):** `l` only ever moves **forward**. Across the entire run of the algorithm, `l` advances at most `n` times in total — regardless of `r`. Both pointers together traverse the array once, giving `O(n)`.

**Complexity:**
- Time: O(n)
- Space: O(1) — only two pointers and a running product

---

## Optimization Journey

```
Naive (O(n³))  ──────────────────────────────────────────────────────────────►
       Enumerate all O(n²) subarrays; recompute each product from scratch.
       Every product overlaps heavily with neighbours — pure wasted work.
       Time: O(n³)   Space: O(1)

           ▼  Observation: product([i, j+1)) = product([i, j)) * nums[j]
              → no need to recompute from scratch; maintain a running product

Naive with running product (O(n²))  ─────────────────────────────────────────►
       Fix left boundary i; expand j one step at a time, keeping a running
       product. Reset product to 1 each time i advances.
       Eliminates the inner recomputation, but still O(n²) outer iterations.
       Time: O(n²)   Space: O(1)

           ▼  Observation: when product >= target, advancing i (the left
              boundary) shrinks the product. We don't need to restart from i+1 —
              we can reuse the current window and just divide out nums[i].

Variable Sliding Window (O(n))  ─────────────────────────────────────────────►
       Two pointers l and r share a single running product.
       r always moves forward (expand); l only moves forward to restore validity.
       Each element is multiplied in once (when r reaches it) and divided out
       once (when l passes it) — O(2n) = O(n) total operations.
       Time: O(n)    Space: O(1)   ← Best possible
```

### Counting valid subarrays at each step

The non-obvious part of the sliding window is **how the count is updated**. At the moment we're about to increment `r`, the window `[l, r]` has `product < target`. Consider all subarrays that **end at index `r`**:

| Subarray | Product compared to `[l..r]` |
|----------|------------------------------|
| `[r..r]` | `≤ product([l..r])` ✓ |
| `[r-1..r]` | `≤ product([l..r])` ✓ |
| `...` | ... |
| `[l..r]` | `= product` ✓ |
| `[l-1..r]` | `≥ target` ✗ (that is why `l` stopped here) |

So exactly `r − l + 1` subarrays ending at `r` are valid. Adding this to `count` at each `r` guarantees we count each valid subarray **exactly once** (once for each right endpoint `r`).

---

## Step-by-step Trace

```
nums = [1, 2, 3, 4],  target = 10

r=0: product = 1*1 = 1  | l=0 | window [0,0] | count += 0-0+1 = 1  | total=1
r=1: product = 1*2 = 2  | l=0 | window [0,1] | count += 1-0+1 = 2  | total=3
r=2: product = 2*3 = 6  | l=0 | window [0,2] | count += 2-0+1 = 3  | total=6
r=3: product = 6*4 = 24 ≥ 10
       → divide nums[0]=1: product=24   l=1
       → divide nums[1]=2: product=12   l=2
       → divide nums[2]=3: product=4    l=3  (< 10, stop)
     window [3,3] | count += 3-3+1 = 1 | total=7

Answer: 7 ✓
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(n³) | O(1) | Recomputes every product from scratch |
| Naive + running product | O(n²) | O(1) | Avoids inner recomputation but still O(n²) outer loops |
| Variable Sliding Window | O(n) | O(1) | Each element touched at most twice (once by `r`, once by `l`) |

> **Best solution:** `OptimizedSolution` (Variable Sliding Window) — linear time and constant space.

