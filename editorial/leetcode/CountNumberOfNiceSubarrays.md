# Count Number of Nice Subarrays

## Problem Statement

Given an array of integers `nums` and an integer `k`, a **nice** subarray is a
contiguous subarray containing **exactly `k` odd numbers**. Return the
number of nice subarrays.

**Example:**
```
nums = [1, 1, 2, 1, 1], k = 3
Nice subarrays: [1,1,2,1], [1,2,1,1]
Answer = 2

nums = [2, 4, 6], k = 1
No odd numbers at all, so no subarray can contain exactly 1
Answer = 0

nums = [2, 2, 2, 1, 2, 2, 1, 2, 2, 2], k = 2
Answer = 16
```

**Constraints:** `1 ≤ nums.length ≤ 5 · 10^4`, `1 ≤ nums[i] ≤ 10^5`,
`1 ≤ k ≤ nums.length`.

---

## Core Insight: Only Parity Matters

Whether `nums[i]` is `1`, `3`, `99999`, or `2`, `4`, `6` is irrelevant — only
its **parity** decides whether it counts toward `k`. Map every element to
`b(i) = nums[i] mod 2 ∈ {0, 1}`; "exactly `k` odd numbers in a subarray" is
then exactly "the `b` values in that subarray sum to `k`". This is the same
shape as *Subarray Sum Equals K*, specialized to a `0/1` array, which is what
makes a prefix-count or two-pointer approach possible.

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For every pair of bounds `(l, r)`, count the odd numbers directly.

```java
class NaiveSolution implements CountNumberOfNiceSubarrays {
    @Override
    public int numberOfSubarrays(int[] nums, int k) {
        int n = nums.length;
        int count = 0;
        for (int l = 0; l < n; l++) {
            int odd = 0;
            for (int r = l; r < n; r++) {
                if (nums[r] % 2 == 1) odd++;
                if (odd == k) count++;
                if (odd > k) break; // odd only grows, no point continuing
            }
        }
        return count;
    }
}
```

**Complexity:** Time `O(n²)` worst case, Space `O(1)`.

**Drawback:** Every left bound `l` re-walks the array from scratch, even
though shifting `l` by one only removes a single element's contribution
from an otherwise-known odd count.

---

### 2. Prefix Count of Odds (HashMap) — O(n) Time, O(n) Space

Let `P[i]` = number of odd numbers in `nums[0..i-1]` (`P[0] = 0`). A subarray
`nums[l..r-1]` is nice iff `P[r] − P[l] == k`, i.e. `P[l] == P[r] − k`. Scan
left to right, and for each `r` look up how many earlier prefixes equal
`P[r] − k` in a frequency map.

```java
class PrefixCountSolution implements CountNumberOfNiceSubarrays {
    @Override
    public int numberOfSubarrays(int[] nums, int k) {
        var freq = new HashMap<Integer, Integer>();
        freq.put(0, 1); // P[0] = 0, seen once (the empty prefix)

        int oddCount = 0;
        int result = 0;
        for (int num : nums) {
            if (num % 2 == 1) oddCount++;
            result += freq.getOrDefault(oddCount - k, 0);
            freq.merge(oddCount, 1, Integer::sum);
        }
        return result;
    }
}
```

**Complexity:** Time `O(n)`, Space `O(n)` for the prefix-count map.

**Drawback:** Correct and simple, but `oddCount` only ever takes values in
`[0, n]`, so a dense array indexed by `oddCount` would do the same job
without hashing overhead — and, since the values are monotonically
non-decreasing, a sliding window can avoid the extra memory altogether.

---

### 3. "At Most K" Sliding Window — O(n) Time, O(1) Space

**Key Insight:** `exactly(k) = atMost(k) − atMost(k − 1)`, where `atMost(k)`
counts subarrays with **at most** `k` odd numbers. `atMost` is monotonic in
the window bound, which is exactly what a classic shrinking sliding window
needs.

```java
class AtMostKSolution implements CountNumberOfNiceSubarrays {

    private int atMost(int[] nums, int k) {
        if (k < 0) return 0;
        int l = 0, odd = 0, count = 0;
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] % 2 == 1) odd++;
            while (odd > k) {
                if (nums[l] % 2 == 1) odd--;
                l++;
            }
            count += r - l + 1; // every start in [l, r] keeps odd <= k
        }
        return count;
    }

    @Override
    public int numberOfSubarrays(int[] nums, int k) {
        return atMost(nums, k) - atMost(nums, k - 1);
    }
}
```

**Complexity:** Time `O(n)` (two linear passes), Space `O(1)`.

This is the standard competitive-programming solution. The implementation in
this repository instead counts "exactly `k`" **directly**, in a single pass,
by reasoning about runs of even numbers — see below.

---

### 4. Optimized Solution (Single-Pass Two Pointers + Gap Counting) — O(n) Amortized Time, O(n) Space

**Key Insight.** Grow a window `nums[l..r-1]` by moving `r` forward one odd
number at a time. The moment its odd count first reaches `k`, `nums[r-1]` is
the *k*-th odd number counted from `l`. Every subarray `nums[l..r'-1]` with
`r' ∈ [r, p]` — where `p` is the index of the **next** odd number after `r`
(or `n` if there is none) — also has exactly `k` odds, because the elements
`nums[r..p-1]` are all even and appending them doesn't change the odd count.
So, for this `l`, there are exactly `p − r + 1` nice subarrays. Once that
contribution is recorded, shrink from the left (`l++`) and repeat: `oddCount`
only drops when the removed element was odd, which is exactly when the
window can validly re-expand.

```java
public interface CountNumberOfNiceSubarrays {
    int numberOfSubarrays(int[] nums, int k);
}

class CountNumberOfNiceSubarraysImpl implements CountNumberOfNiceSubarrays {

    private final Map<Integer, Integer> lastEvenCache = new HashMap<>();

    private int lastEven(int[] nums, int r){
        Integer cached = lastEvenCache.get(r);
        if(cached != null){
            return cached;
        }
        int result = r;
        while(result < nums.length && nums[result] % 2 == 0){
            result++;
        }
        lastEvenCache.put(r, result);
        return result;
    }

    @Override
    public int numberOfSubarrays(int[] nums, int k) {
        int l = 0;
        int r = 0;
        int result = 0;
        int oddCount = 0;

        while (l < nums.length) {
            if (r < nums.length && oddCount < k) {
                if (nums[r] % 2 == 1) {
                    oddCount += 1;
                }
                r++;
            } else {
                if (oddCount == k) {
                    int p = lastEven(nums, r);
                    result += (p - 1) - (r - 1) + 1; // = p - r + 1
                }
                if (nums[l] % 2 == 1) {
                    oddCount -= 1;
                }
                l++;
            }
        }
        return result;
    }
}
```

**How it works:**
1. **Expand** (`if` branch): while the window `[l, r)` has fewer than `k`
   odds and `r` hasn't run off the array, fold `nums[r]` into `oddCount` and
   advance `r`. This stops the instant `oddCount` reaches `k`, or `r` hits
   `n` with `oddCount` still short of `k`.
2. **Contribute** (`else` branch, `oddCount == k`): `r` is frozen one past the
   `k`-th odd number. `lastEven(nums, r)` scans forward through the run of
   even numbers starting at `r` and returns the index `p` of the next odd
   number (or `n`). Every ending in `[r − 1, p − 1]` keeps the window
   exactly `k`-odd, contributing `p − r + 1` nice subarrays that start at
   `l`.
3. **Shrink** (also in the `else` branch): drop `nums[l]` from the window
   and advance `l`. If it was odd, `oddCount` falls to `k − 1` and expansion
   resumes next iteration with a fresh `r`; if it was even, `oddCount` stays
   `k` and the *same* `r` is queried again for the next `l`.
4. **Memoization matters here.** As long as the dropped elements keep being
   even, step 3 re-queries `lastEven` with the **same** `r` on every
   iteration. Without caching, each of those repeated calls would rescan the
   entire trailing run of evens from scratch — see the amortized analysis
   below for why that would blow up to `O(n²)` in the worst case.

> **Equivalence to gap counting.** If the odd-number positions are
> `o_1 < o_2 < … < o_m` (with virtual sentinels `o_0 = −1` and
> `o_{m+1} = n`), the classic closed-form answer is
> `Σ_i (o_i − o_{i−1}) · (o_{i+k} − o_{i+k−1})` over all valid `i`: the left
> factor counts how many starts `l` keep `o_i` as the window's first odd, and
> the right factor is exactly this solution's `p − r + 1` for that group.
> The two-pointer version computes the same product incrementally, one `l`
> at a time, instead of as a single multiplication.

---

## Step-by-step Trace

### Two-pointer trace: `nums = [1, 1, 2, 1, 1]`, `k = 3`

```
indices:   0  1  2  3  4
values:    1  1  2  1  1   (odd, odd, even, odd, odd)

l=0 r=0 odd=0
  expand r=0: odd 1 -> odd=1, r=1
  expand r=1: odd 1 -> odd=2, r=2
  expand r=2: even   -> odd=2, r=3
  expand r=3: odd 1 -> odd=3, r=4        (odd == k, stop expanding)

  contribute: lastEven(nums,4) -> nums[4]=1 is odd -> p=4
              result += 4-4+1 = 1                         total=1
  shrink: nums[0]=1 odd -> odd=2, l=1

  expand r=4: odd 1 -> odd=3, r=5                          (odd == k, r==n)
  contribute: lastEven(nums,5) -> r>=n -> p=5
              result += 5-5+1 = 1                          total=2
  shrink: nums[1]=1 odd -> odd=2, l=2

  (odd=2 != k) shrink: nums[2]=2 even -> odd=2, l=3
  (odd=2 != k) shrink: nums[3]=1 odd  -> odd=1, l=4
  (odd=1 != k) shrink: nums[4]=1 odd  -> odd=0, l=5         loop ends

Answer: 2 ✓   ( [1,1,2,1] and [1,2,1,1] )
```

### Gap-counting view: `nums = [2,2,2,1,2,2,1,2,2,2]`, `k = 2`

```
indices:        0  1  2  3  4  5  6  7  8  9
values:         2  2  2  1  2  2  1  2  2  2
odd positions:              3        6

Extended odd-position list (sentinels -1 and n=10): [-1, 3, 6, 10]

Only one group of k=2 consecutive odds: {idx 3, idx 6}
  left  = o_1 - o_0  = 3 - (-1) = 4   (starts: 0,1,2,3)
  right = o_3 - o_2  = 10 - 6  = 4   (ends: 6,7,8,9)

  contribution = left * right = 4 * 4 = 16

Answer: 16 ✓
```

The two-pointer solution reaches the same `16` incrementally: for each of
the 4 valid starts `l ∈ {0,1,2,3}`, once the window's odd count first hits
`2` (right after index `6`), `lastEven(nums, 7)` finds `p = 10` and adds
`10 - 7 + 1 = 4`; summing over the 4 starts gives `4 × 4 = 16`, and the first
of those four `lastEven(nums, 7)` calls is the only cache miss.

---

## Correctness Proof

### Setup

Let `n = nums.length`. For indices `0 ≤ l ≤ r ≤ n`, let `Odd(l, r)` be the
number of odd values in `nums[l..r-1]` (the empty window has `Odd(l, l) = 0`).
At the top of each outer-loop iteration, let `(l, r, oddCount)` denote the
current pointer values.

### Invariant (maintained by the `if` branch)

**Claim.** At every point in the algorithm, `oddCount = Odd(l, r)`, and
`oddCount ≤ k`.

- **Initialization.** `l = r = 0`, `oddCount = 0 = Odd(0, 0)`.
- **Maintenance (`if` branch).** This branch only runs while `oddCount < k`.
  It inspects `nums[r]`, increments `oddCount` iff `nums[r]` is odd, then
  increments `r`. By definition of `Odd`, `Odd(l, r_{new}) = Odd(l, r_{old}) + [\text{nums}[r_{old}] \text{ odd}]`,
  which is exactly the new `oddCount`. Since the branch only fires when the
  old `oddCount < k`, the new value is at most `k`.
- **Maintenance (`else` branch, shrink step).** It inspects `nums[l]`,
  decrements `oddCount` iff `nums[l]` is odd, then increments `l`. By the
  same counting argument, `Odd(l_{new}, r) = Odd(l_{old}, r) - [\text{nums}[l_{old}] \text{ odd}]`,
  matching the new `oddCount`. `oddCount` cannot exceed `k`, since it only
  decreases or stays equal here. ∎

### Lemma 1 (The `else` branch is reached exactly when expansion must stop)

The `else` branch runs in an iteration iff `r == n` or `oddCount == k`
(given the invariant `oddCount ≤ k` above, these are precisely the
negations of `r < n && oddCount < k`).

*Proof.* Immediate from the `if`/`else` condition and the invariant. ∎

### Lemma 2 (Gap formula is exact)

Suppose at some iteration `oddCount == k` with current pointers `(l, r)`.
Then `nums[r-1]` is odd (the `k`-th odd number of the window), `nums[r..p-1]`
are all even where `p = lastEven(nums, r)`, and for every
`r' ∈ [r-1, p-1]`, `Odd(l, r'+1) = k`, while `Odd(l, p+1) = k+1` if `p < n`.

*Proof.* `oddCount` only ever reaches exactly `k` by an increment in the
`if` branch (Invariant), which happens precisely when `nums[r-1]` is odd —
so `nums[r-1]` is odd. By definition, `lastEven` scans from `r` while
elements are even, so `nums[r..p-1]` are all even and (if `p < n`)
`nums[p]` is odd. For `r' ∈ [r-1, p-1]`: `Odd(l, r'+1) = Odd(l, r) = k`
because the only elements added beyond `nums[l..r-1]` are even. For
`r' = p` (if `p < n`): `Odd(l, p+1) = k + 1` since `nums[p]` is odd. ∎

By Lemma 2, the number of endings `r' + 1 \in [r, p]` keeping the window
exactly `k`-odd is `p - (r-1) = p - r + 1`, which is exactly what the code
adds: `(p-1)-(r-1)+1 = p - r + 1`.

### Theorem (No double counting, no omission)

Every nice subarray `nums[l..r'-1]` is counted **exactly once**, at the
unique outer-loop iteration where the left bound equals `l` and
`oddCount == k` for the first time with that `l`.

*Proof.* Fix a nice subarray starting at `l`. By the Invariant, as the
algorithm progresses, `l` only increases and, for each value of `l`, the
window first reaches `oddCount == k` the moment `r` passes the `k`-th odd
number at or after `l` — a specific, unique `r` for that `l` (the odd
positions are fixed regardless of pointer history). Once `oddCount == k` is
reached for this `l`, Lemma 2 shows the resulting contribution `p - r + 1`
accounts for **every** ending `r'` that keeps `nums[l..r'-1]` nice — no more,
no fewer. The subsequent shrink step (`l++`) moves to the next `l` without
revisiting this one, so this contribution happens exactly once. If `l`'s
window never reaches exactly `k` odds (either it caps at `oddCount < k`
with `r == n`, or shoots past at the moment of evaluation — ruled out by the
Invariant since the loop evaluates right when `oddCount` first hits `k`), no
subarray starting at `l` is nice, and correctly nothing is added. ∎

### Termination

Each outer-loop iteration increments either `r` or `l`. `r` is capped at `n`
and `l` is capped at `n`; the loop exits once `l == n`. Since `r ≤ n` always
and `l` strictly increases every time the `else` branch runs (which it must,
once `r` can no longer grow), the loop terminates after at most `2n`
iterations.

---

## Amortized Analysis of the `lastEven` Cache

Without the cache, `lastEven(nums, r)` is called once per outer-loop
iteration where `oddCount == k`, and the *same* `r` can be queried for many
consecutive values of `l` (every iteration where the dropped left element is
even). Each such call would redo the full scan over `nums[r..p-1]`. In the
worst case — e.g. `k = 1` and roughly `n/2` leading even numbers followed by
one odd number and `n/2` trailing even numbers — the window stalls at the
same `r` for `Θ(n)` consecutive shrink steps, each rescanning a `Θ(n)`-long
run: `Θ(n²)` total.

**Potential function.** Let `p_t` be the largest value ever returned by
`lastEven` up to (and including) time `t` (`p_0 = 0`), and define
`Φ(D_t) = n - p_t ≥ 0`.

**Claim.** The amortized cost of each `lastEven` call, charging `O(1)` for
the `HashMap` lookup/insert plus the cost of any scanning, is `O(1)`.

*Proof.* A **cache hit** costs `O(1)` actual work and does not change `p_t`,
so its amortized cost (actual cost `+ ΔΦ = 0`) is `O(1)`.

A **cache miss** at call `i` queries some `r_i` and scans to `p_i`, at real
cost `c_i = p_i - r_i + O(1)`. Because `r` (the frozen window bound) only
increases over the run, and a new (uncached) `r_i` is only ever queried once
`r` has advanced past every index resolved by the previous miss — i.e.
`r_i ≥ p_{i-1}` — the amortized cost is:

```
ĉ_i = c_i + Φ(D_i) - Φ(D_{i-1})
    = (p_i - r_i + O(1)) + (n - p_i) - (n - p_{i-1})
    = (p_{i-1} - r_i) + O(1)
    ≤ O(1)          since r_i ≥ p_{i-1}
```

So every call — hit or miss — has `O(1)` amortized cost. Summing over at
most `O(n)` calls to `lastEven` gives `O(n)` total actual work, i.e.
`Σ c_i ≤ Σ ĉ_i + Φ(D_0) - Φ(D_final) = O(n)`. ∎

Combined with the `O(n)` pointer-movement bound from Termination above, the
whole algorithm runs in `O(n)` amortized time.

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(1) | Recount odds from scratch for every start |
| Prefix Count (HashMap) | O(n) | O(n) | `exactly(k)` via `P[r] - P[l] = k` lookups |
| "At Most K" Sliding Window | **O(n)** | **O(1)** | `exactly(k) = atMost(k) - atMost(k-1)` |
| Two Pointers + Gap Counting (this repo) | O(n) amortized | O(n) | Count trailing-even extensions per start; cache avoids rescans |

> The "At Most K" window is the simplest `O(1)`-space optimal solution. The
> single-pass two-pointer version trades `O(n)` cache space for computing
> the exact answer directly, without a second pass — but **requires**
> memoizing `lastEven`, or repeated shrink steps against a frozen `r` make
> it quadratic in the worst case.

---

## Related Problems

- [Count the Number of Good Subarrays](CountTheNumberOfGoodSubarrays.md) —
  another "count subarrays by a per-window property" problem using an
  incrementally maintained window statistic and a monotonic left boundary.
- [Length of Longest Subarray With at Most K Frequency](LengthOfLongestSubarrayWithAtMostKFrequency.md) —
  the same `atMost`-style monotonic window shrinking used in Solution 3 here.
