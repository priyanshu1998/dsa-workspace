# 4Sum

## Problem Statement

Given an integer array `nums` of length `n` and a target value `target`,
return **all unique quadruplets** `[nums[a], nums[b], nums[c], nums[d]]` such
that:

- `0 <= a, b, c, d < n`, and `a`, `b`, `c`, `d` are **distinct indices**,
- `nums[a] + nums[b] + nums[c] + nums[d] == target`.

The answer must not contain duplicate quadruplets (duplicates are judged by
**value**, not by index — e.g. `[0, 0, 0, 0]` found via two different sets of
indices is still just one answer).

**Example:**
```
nums = [1, 0, -1, 0, -2, 2], target = 0
Answer = [[-2, -1, 1, 2], [-2, 0, 0, 2], [-1, 0, 0, 1]]
```

**Overflow edge case:**
```
nums = [1000000000, 1000000000, 1000000000, 1000000000], target = 4000000000
Answer = [[1000000000, 1000000000, 1000000000, 1000000000]]
```
Four values near `10^9` sum to `4×10^9`, which overflows a 32-bit `int`
(max `~2.147×10^9`). This is why this repository's `FourSum` interface
accepts `target` as a `long`, and why `Quadruplet.sum()` widens its first
operand to `long` before adding (`((long) nums_a) + nums_b + nums_c + nums_d`)
— every partial sum must be carried in 64-bit arithmetic. Note that a
**two-element** partial sum such as `nums[a] + nums[b]` is still safe to
compute as a plain `int`, because LeetCode's constraints
(`-10^9 <= nums[i] <= 10^9`) guarantee any two-element sum stays within
`int` range; only sums of three or more elements (or the `target` itself)
require `long`.

This is the natural generalization of *2Sum* / *3Sum*: fixing two indices
reduces the problem to a 2Sum search on the remaining subarray, which (once
sorted) can be solved with two converging pointers in linear time instead of
a nested scan.

---

## Solutions

### 1. Brute Force (Four Nested Loops) — O(n⁴) Time, O(1) extra Space

Try every combination of four indices directly, deduplicating by value with
a set.

```java
class BruteForceSolution implements FourSum {
    @Override
    public List<List<Integer>> fourSum(int[] nums, long target) {
        Arrays.sort(nums);
        Set<Quadruplet> unique = new HashSet<>();
        int n = nums.length;
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                for (int c = b + 1; c < n; c++) {
                    for (int d = c + 1; d < n; d++) {
                        long sum = ((long) nums[a]) + nums[b] + nums[c] + nums[d];
                        if (sum == target) {
                            unique.add(new Quadruplet(nums[a], nums[b], nums[c], nums[d]));
                        }
                    }
                }
            }
        }
        return unique.stream()
            .map(q -> List.of(q.nums_a(), q.nums_b(), q.nums_c(), q.nums_d()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
```

**Complexity:** Time `O(n⁴)`, Space `O(1)` extra (ignoring the output/dedup set).

**Drawback:** Every `(a, b)` pair re-scans *all* remaining `(c, d)` pairs from
scratch with no structure exploited. Sorting first (as the solution already
does) turns the inner two loops into a classic sorted-array 2Sum, which can
be solved far faster than a nested scan.

---

### 2. Sort + Two Nested Loops + HashSet 2Sum — O(n³) Time, O(n) Space

Fix `a` and `b` with two loops (`O(n²)` pairs), then solve "find `c, d` with
`nums[c] + nums[d] == target - nums[a] - nums[b]`" with a single pass using a
hash set, exactly like the classic unsorted 2Sum.

```java
class HashSetSolution implements FourSum {
    @Override
    public List<List<Integer>> fourSum(int[] nums, long target) {
        Arrays.sort(nums);
        Set<Quadruplet> unique = new HashSet<>();
        int n = nums.length;
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                long need = target - nums[a] - nums[b];
                Set<Integer> seen = new HashSet<>();
                for (int c = b + 1; c < n; c++) {
                    long complement = need - nums[c];
                    if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE
                        && seen.contains((int) complement)) {
                        unique.add(new Quadruplet(nums[a], nums[b], (int) complement, nums[c]));
                    }
                    seen.add(nums[c]);
                }
            }
        }
        return unique.stream()
            .map(q -> List.of(q.nums_a(), q.nums_b(), q.nums_c(), q.nums_d()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
```

**Complexity:** Time `O(n³)` (two loops × one linear hash-set pass), Space
`O(n)` for the `seen` set (rebuilt for every `(a, b)` pair).

**Drawback:** Correct and already `O(n³)`, but allocates and repopulates a
fresh hash set `O(n²)` times. Since the array is already sorted for
duplicate handling, the inner 2Sum search on a **sorted** subarray can be
done with two converging pointers instead of hashing — same asymptotic time,
but `O(1)` extra space and no hashing overhead.

---

### 3. Sort + Two Nested Loops + Two Pointers — O(n³) Time, O(1) extra Space

**This is `FourSumImpl`, the solution under test.** Sort once up front. Fix
`a` and `b` with two nested loops, then solve the remaining 2Sum on the
*sorted* suffix `nums[b+1 .. n-1]` with two pointers `c` (from the left) and
`d` (from the right) that converge toward each other, exploiting sortedness
to decide, in `O(1)` per step, which pointer must move.

```java
class FourSumImpl implements FourSum {
    @Override
    public List<List<Integer>> fourSum(int[] nums, long target) {
        Arrays.sort(nums);
        Set<Quadruplet> unique = new HashSet<>();
        int n = nums.length;
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                int c = b + 1;
                int d = n - 1;

                while (c < d) {
                    if (nums[a] + nums[b] == target - nums[c] - nums[d]) {
                        unique.add(new Quadruplet(nums[a], nums[b], nums[c], nums[d]));
                        c++;
                        d--;
                    } else if (nums[c] + nums[d] < target - nums[a] - nums[b]) {
                        c++;
                    } else { // nums[c] + nums[d] > target - nums[a] - nums[b]
                        d--;
                    }
                }
            }
        }

        return unique.stream()
            .map(q -> List.of(q.nums_a(), q.nums_b(), q.nums_c(), q.nums_d()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
```

**Complexity:** Time `O(n³)` (`O(n²)` pairs `(a, b)`, each driving an `O(n)`
two-pointer sweep), Space `O(1)` extra (the `HashSet<Quadruplet>` is for
value-based output deduplication, bounded by the output size, not auxiliary
working memory).

**Why not `O(n²)` or better?** Any correct 4Sum solution must, in the worst
case, be able to *output* `Θ(n²)` quadruplets (e.g. many pairs `(a, b)` each
matching many pairs `(c, d)`), so `O(n²)` output size is unavoidable in
general, and producing it requires visiting at least `Θ(n²)` candidate pairs
`(a, b)`; combined with an `O(n)` inner sweep per pair, `O(n³)` is the
standard optimal bound for this approach (sorting-based 4Sum does not admit
a known sub-`O(n³)` general algorithm).

---

### Bonus: Generalized `kSum` Recursion (see `GenericSumSolution` in the test file)

The two outer loops plus the two-pointer 2Sum base case generalizes cleanly
to arbitrary `k`: recursively peel off one index at a time (skipping
duplicate values at each recursion level) until `k == 2`, then solve with
two pointers. An **average-value pruning** check
(`nums[start] > target / k || target / k > nums[n-1]`) additionally allows
early termination of branches that cannot possibly sum to `target`, which is
a practical (not asymptotic) speed-up over the fixed `k = 4` version above.
Asymptotically this is the same `O(n^(k-1))` family, specializing to `O(n³)`
at `k = 4`.

---

### Correctness Proof (of `FourSumImpl`, the two-pointer solution)

**Outer loops.** The two `for` loops `a = 0..n-1`, `b = a+1..n-1` exhaustively
enumerate every one of the `C(n, 2)` index pairs `(a, b)` with `a < b`
exactly once; this is immediate from the loop bounds and requires no further
proof. Correctness therefore reduces to proving that, **for each fixed pair
`(a, b)`**, the inner `while (c < d)` loop emits (into `unique`) every
distinct **value**-pair `(x, y)` with `x <= y` such that some indices
`b < i < j <= n-1` have `nums[i] = x`, `nums[j] = y`, and
`nums[a] + nums[b] + x + y == target` — and emits nothing else.

Fix `a`, `b`. Let `S = target - nums[a] - nums[b]` (as a mathematical
integer; the implementation computes the equivalent comparison
`nums[a] + nums[b] == target - nums[c] - nums[d]` to stay within safe integer
ranges, which is algebraically identical). Because the array is sorted,
`nums[b+1], ..., nums[n-1]` is non-decreasing.

**Loop invariant.** At the start of every iteration of the `while (c < d)`
loop, with current pointer values `c`, `d`:

> **I(c, d):** Every pair of indices `(i, j)` with `b < i < j <= n-1` and
> `nums[i] + nums[j] == S` satisfies `c <= i` and `j <= d` (i.e. every
> as-yet-unemitted valid index pair lies inside the current window
> `[c, d]`), **and** every distinct value-pair achievable by some
> `(i, j)` with `b < i < j <= n-1`, `nums[i] + nums[j] == S`, that lies
> **outside** `[c, d]` (i.e. `i < c` or `j > d`) has already been added to
> `unique`.

- **Initialization.** Before the first iteration, `c = b + 1`, `d = n - 1`,
  so `[c, d]` is the *entire* remaining suffix. There is no pair `(i, j)`
  with `b < i < j <= n-1` lying outside `[b+1, n-1]`, so the second clause
  is vacuously true, and the first clause holds trivially since every valid
  `i, j` already satisfies `b+1 <= i` and `j <= n-1`.

- **Maintenance.** Assume `I(c, d)` holds with `c < d`. Let
  `t = nums[c] + nums[d]`. Exactly one of three cases applies:

  - **Case `t == S`:** The pair `(c, d)` is itself valid, and its value-pair
    `(nums[c], nums[d])` is added to `unique`. Consider any other valid pair
    `(i, j)` (`b < i < j <= n-1`, `nums[i] + nums[j] == S`) with `c <= i`,
    `j <= d` (guaranteed to lie here by `I(c,d)`). If `i = c` and `j < d`:
    since `nums` is non-decreasing and `j < d`, `nums[j] <= nums[d]`; but
    `nums[c] + nums[j] = S = nums[c] + nums[d]` forces `nums[j] = nums[d]`,
    so the value-pair `(nums[i], nums[j]) = (nums[c], nums[d])` is the
    *same value-pair* just added — not a new one. Symmetrically, if `j = d`
    and `i > c`, `nums[i] = nums[c]`, again the same value-pair. Otherwise
    `c < i < j < d`, strictly inside the shrunk window `[c+1, d-1]`. Hence
    after `c++; d--`, every valid pair outside the new window `[c+1, d-1]`
    either was already outside `[c, d]` (covered by `I(c,d)`'s second
    clause) or equals `(c, d)` / reproduces its value-pair (just emitted).
    So `I(c+1, d-1)` holds.

  - **Case `t < S`:** For any `i` with `c <= i` and any `j <= d`,
    non-decreasing `nums` on `[c, n-1]` gives `nums[i] <= nums[d']` is not
    directly needed; instead fix `i = c`: for every `j <= d`,
    `nums[j] <= nums[d]` (sortedness, `j <= d`), so
    `nums[c] + nums[j] <= nums[c] + nums[d] = t < S`. Hence **no** valid
    pair `(c, j)` with `j <= d` exists — index `c` can never again pair
    with anything in the current window to reach `S`. So every valid pair
    with `c <= i <= d`, `j <= d` (per `I(c,d)`) in fact has `i > c`, i.e.
    `c + 1 <= i`. Thus `I(c+1, d)` holds after `c++` (no new value-pairs
    were skipped, since none existed at `i = c`).

  - **Case `t > S`:** Symmetrically, fixing `j = d`: for every `i >= c`,
    `nums[i] >= nums[c]`, so `nums[i] + nums[d] >= nums[c] + nums[d] = t > S`
    — no valid pair `(i, d)` exists. Every valid pair from `I(c,d)` must
    then have `j < d`, i.e. `j <= d - 1`, so `I(c, d-1)` holds after `d--`.

  In all three cases the invariant is re-established for the updated
  `(c, d)`.

- **Termination.** The loop ends when `c >= d`. At that point the window
  `[c, d]` contains no pair `(i, j)` with `i < j`, so by `I(c, d)`'s second
  clause (now covering *every* valid pair, since none remain inside the
  empty window), **every** value-pair summing to `S` has been added to
  `unique`. Soundness is immediate: `unique` only ever receives a
  value-quadruplet when the exact equality `nums[a] + nums[b] == target -
  nums[c] - nums[d]` holds, computed without overflow (the LHS is a safe
  two-element `int` sum per the constraints noted above; the RHS is
  computed against the `long target`). Combined with the outer loops'
  exhaustive enumeration of `(a, b)`, `unique` ends up equal to exactly the
  set of distinct-by-value quadruplets summing to `target`, which the final
  `.stream().map(...)` converts to the required `List<List<Integer>>`. ∎

**Termination & decreasing variant.** The measure `f(c, d) = d - c` is a
non-negative integer while `c < d`. Each branch strictly decreases it: the
match branch (`c++; d--`) decreases it by `2`, the other two branches
(`c++` or `d--` alone) decrease it by `1`. Since `f` strictly decreases and
is bounded below by `0` (loop exit condition `c >= d`), the inner loop
terminates after at most `d_initial - c_initial = n - 1 - b - 1` iterations,
i.e. `O(n)` steps.

---

## Optimization Journey

```
Brute Force (4 nested loops) ───────────────────────────────────────────►
       Try every combination of four indices directly.
       Time: O(n⁴)    Space: O(1) extra

           ▼  Observation: fixing (a, b) reduces the remaining search to a
              classic 2Sum on the suffix nums[b+1..n-1], which a single
              hash-set pass solves in O(n) instead of an O(n²) nested scan.

HashSet 2Sum (fix a, b; hash-set inner pass) ───────────────────────────►
       Two nested loops over (a, b), one O(n) hash-set 2Sum pass each.
       Time: O(n³)    Space: O(n) for the per-pair hash set

           ▼  Observation: the array is already sorted (needed for value
              dedup anyway), so the inner 2Sum can be solved with two
              converging pointers exploiting monotonicity, eliminating the
              hash set and its repeated O(n) allocations entirely.

Sort + Two Pointers (FourSumImpl) ──────────────────────────────────────►
       Two nested loops over (a, b), one O(n) two-pointer sweep each.
       Time: O(n³)    Space: O(1) extra (ignoring the output dedup set)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Brute Force (4 nested loops) | O(n⁴) | O(1) | No structure exploited; rescans all `(c, d)` per `(a, b)` |
| HashSet 2Sum | O(n³) | O(n) | Per-`(a,b)` hash set rebuilt `O(n²)` times |
| Sort + Two Pointers | O(n³) | O(1) | `FourSumImpl` — exploits sortedness, no hashing overhead |
| Generalized `kSum` recursion | O(n^(k-1)) | O(k) recursion depth | `GenericSumSolution`; adds average-value pruning, generalizes to any `k` |

> **Best solution:** `FourSumImpl` (Sort + Two Pointers) — `O(n³)` time,
> `O(1)` extra space, with every distinct-value quadruplet summing to
> `target` provably emitted exactly once via the converging-pointer
> invariant proved above, and no false positives since every insertion is
> guarded by an exact equality check in overflow-safe arithmetic.
