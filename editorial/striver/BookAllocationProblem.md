# Book Allocation Problem

## Problem Statement

Given an array `nums` of `n` books (each `nums[i]` is the page count of book `i`, books must be assigned in contiguous order) and an integer `m` (number of students), allocate **all** books to `m` students such that:
- Each student gets at least one contiguous block of books.
- Every book is assigned to exactly one student.
- The **maximum number of pages assigned to any single student is minimized**.

Return that minimized maximum, or `-1` if allocation is impossible (`m > n`).

**Example:**
```
nums = [12, 34, 67, 90], m = 2         → 113
nums = [25, 46, 28, 49, 24], m = 4     → 71
```

---

## Solution (Binary Search on Answer) — O(n · log(sum − max)) Time, O(1) Extra Space

**Key Insight:** The answer (minimum possible "maximum pages per student") lies somewhere in the range `[max(nums), sum(nums)]`:
- It can never be less than `max(nums)`, since the single student who gets the book with the most pages must carry at least that many.
- It can never need to exceed `sum(nums)`, since giving everything to one student (`m = 1`) always works with that total.

Define `check(nums, m, pages)` = "can all books be allocated to `<= m` students such that no student carries more than `pages` pages, using a greedy left-to-right assignment (keep adding the next book to the current student's running total unless it would exceed `pages`, in which case start a new student)?" As `pages` increases, this predicate only gets easier to satisfy (monotone), so binary search the answer space `[max(nums), sum(nums)]` for the smallest feasible `pages`.

```java
class BookAllocationProblemImpl implements BookAllocationProblem {

    private int max(int[] nums) {
        int m = nums[0];
        for (int num : nums) m = Math.max(m, num);
        return m;
    }

    private int sum(int[] nums) {
        int tot = 0;
        for (int num : nums) tot += num;
        return tot;
    }

    private boolean check(int[] nums, int m, int pages) {
        int students = 1;
        int runningSum = 0;
        for (int num : nums) {
            if (runningSum + num > pages) {
                students++;
                runningSum = num;
            } else {
                runningSum += num;
            }
        }
        return students <= m;
    }

    @Override
    public int findPages(int[] nums, int m) {
        if (m > nums.length) {
            return -1;
        }

        int l = max(nums);
        int r = sum(nums);

        while (l < r) {
            int mid = l + (r - l) / 2; // lower mid, to search for the min
            if (check(nums, m, mid)) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }

        return r;
    }
}
```

**How it works:**
1. The search space is not the array itself — it is the space of candidate page-limits `[max(nums), sum(nums)]`.
2. `check(nums, m, pages)` evaluates the monotone predicate in O(n) via a single greedy left-to-right pass: a book is appended to the current student's running total whenever doing so would not exceed `pages`; otherwise a new student starts, carrying that book as their first.
3. Binary search converges on the **smallest** `pages` for which the predicate is `true`: `mid = l + (r - l) / 2` is the standard lower mid (safe here since the search is for the *minimum* feasible value — `l = mid + 1` strictly advances when infeasible, and `r = mid` never discards `mid` when feasible).
4. The loop ends when `l == r`, which is exactly the minimum feasible `pages`.

**Complexity:**
Time: O(n · log(sum(nums) − max(nums))) — O(log(sum − max)) binary-search iterations, each doing an O(n) greedy feasibility check (plus O(n) upfront for `max`/`sum`).
Space: O(1) extra.

---

## Formal Proof of Correctness

**Setup.** Let `nums` have `n` books with `m <= n` (otherwise the method short-circuits to `-1`, which is clearly correct: with more students than books, at least one student would get zero books, violating "every student gets at least one contiguous block"). For an integer page-limit `p >= 0`, define the **greedy student count**:

```
s(p) = number of students produced by scanning nums left to right,
       starting a new student whenever adding the next book to the
       current student's running total would exceed p.
```

(If some `nums[i] > p`, the greedy scan can "overflow" a single book past `p` — this only happens when `p < max(nums)`, a region the algorithm never searches, since `l` starts at `max(nums)`.)

Let `p* = min { p >= max(nums) : s(p) <= m }`. This is well-defined because `s(sum(nums)) = 1 <= m` always holds (one student can always take every book when `p = sum(nums)`). We must show the algorithm returns `p*`.

**Lemma 1 (Greedy optimality).** For a fixed `p >= max(nums)`, the greedy scan computes the *minimum* number of students needed to allocate all books (in order) such that no student's total exceeds `p`.
*Proof (exchange argument).* Any valid allocation partitions `nums` into contiguous blocks `B_1, B_2, ..., B_k` (in order) each with sum `<= p`. Let the greedy scan produce blocks `G_1, G_2, ..., G_t`. We show by induction that greedy's `i`-th block ends at or after any valid allocation's `i`-th block, i.e., greedy never "falls behind." Base case: both start scanning from `nums[0]`; greedy extends `G_1` as far as possible while staying `<= p`, so `G_1`'s end index `>= B_1`'s end index (greedy is maximal by construction — it only stops when the *next* book would overflow). Inductive step: assume `G_i` ends at or after `B_i` ends. Then `G_{i+1}` starts at or before `B_{i+1}` starts (or at the same point), and since greedy again extends maximally while `<= p`, `G_{i+1}` ends at or after `B_{i+1}` ends. Since greedy's prefix always covers at least as much of `nums` after `i` blocks as any valid allocation, greedy never needs more blocks to finish than any valid allocation does — hence `t <= k` for every valid `k`, so `s(p) = t` is the minimum achievable student count at limit `p`. ∎

**Lemma 2 (Monotonicity).** For all `p >= max(nums)`, `s(p + 1) <= s(p)`.
*Proof.* Any contiguous partition valid at limit `p` (every block sum `<= p`) is also valid at limit `p + 1` (every block sum `<= p < p + 1`). So the set of valid partitions at `p` is a subset of those valid at `p + 1`. Taking the minimum block count (Lemma 1) over a superset of valid partitions can only be `<=` the minimum over the subset, giving `s(p+1) <= s(p)`. ∎

This monotonicity makes `check(nums, m, p) ≡ (s(p) <= m)` monotone: once `true` for some `p`, it remains `true` for every `p' > p` (by Lemma 2, if `s(p) <= m` then `s(p') <= s(p) <= m` for `p' > p`). So the set of feasible page-limits `{p >= max(nums) : s(p) <= m}` is exactly the suffix `[p*, sum(nums)]`, and binary search for the left boundary of this suffix is well-defined.

**Loop Invariant.** At the start (and end) of every iteration of the `while (l < r)` loop:
> `max(nums) <= l <= p* <= r <= sum(nums)`

i.e., the window `[l, r]` always contains `p*`.

**Initialization.** Before the first iteration, `l = max(nums)` and `r = sum(nums)`. Since `p*` is the minimum feasible page-limit found by searching from `max(nums)` upward, and `s(sum(nums)) = 1 <= m` is always feasible, `p* <= sum(nums) = r`. Also `p* >= max(nums) = l` by construction of the search domain (no `p < max(nums)` is ever considered feasible by definition of `p*`). Thus the invariant holds initially.

**Maintenance.** Assume the invariant holds at the start of an iteration with `l < r`. Let `mid = l + (r - l) / 2` (lower mid), so `l <= mid < r`.

*Case A: `check(nums, m, mid)` is `true`, i.e., `s(mid) <= m`.*
By definition, `mid` is a feasible page-limit, so by minimality of `p*`, `p* <= mid`. Combined with the invariant `l <= p*`, updating `r = mid` preserves `l <= p* <= r` (since `p* <= mid` directly gives the new `r >= p*`).

*Case B: `check(nums, m, mid)` is `false`, i.e., `s(mid) > m`.*
By Lemma 2 (monotonicity), `mid` infeasible implies every `p <= mid` is also infeasible (since `s` is non-increasing, `s(p) >= s(mid) > m` for `p <= mid`). Since `p*` is feasible by definition, `p*` cannot be `<= mid`, so `p* > mid`, i.e., `p* >= mid + 1`. Combined with the invariant `p* <= r`, updating `l = mid + 1` preserves `l <= p* <= r`.

In both cases the invariant holds after the update, and the window strictly shrinks: either `r` moves down to `mid < r`, or `l` moves up to `mid + 1 > l` — either way, `r - l` strictly decreases. (Using the *lower* mid is essential here: it guarantees `l = mid + 1` always makes forward progress even when `r = l + 1`.)

**Termination (decreasing variant).** Let the measure be `h = r - l >= 0`. Each iteration strictly decreases `h` (shown above), and `h` is a non-negative integer bounded below by `0` (the loop condition `l < r` fails exactly when `h = 0`). By well-foundedness of the non-negative integers under `<`, the loop terminates after finitely many iterations — specifically after at most `⌈log2(sum(nums) − max(nums) + 1)⌉` iterations, since the window at least halves each time.

**Conclusion.** At termination, `l == r`. By the invariant `l <= p* <= r`, this forces `l = r = p*`. The algorithm returns `r` (equivalently `l`), which equals `p*` — the minimum possible value for the maximum pages assigned to any single student across all valid allocations into `<= m` (and, since every student must get at least one book, exactly `m`, as the greedy allocator never produces more blocks than needed) contiguous groups. ∎

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Binary Search on Answer | O(n · log(sum(nums) − max(nums))) | O(1) extra | Exploits monotonicity of minimum-required-students in the page limit |

> **Best solution:** `BookAllocationProblemImpl` (Binary Search on Answer) — O(n · log(sum − max)) time with O(1) extra space, far faster than a brute-force scan over every candidate page-limit from `max(nums)` to `sum(nums)`.
