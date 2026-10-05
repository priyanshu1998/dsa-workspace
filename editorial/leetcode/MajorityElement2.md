# Majority Element II

## Problem Statement

Given an integer array `nums` of size `n`, return **all** elements that appear
**more than `⌊n/3⌋` times**. You may assume the array is non-empty.

**Example:**
```
nums = [3, 2, 3]
Answer = [3]                 // 3 appears 2 times, ⌊3/3⌋ = 1, 2 > 1

nums = [1]
Answer = [1]                 // 1 appears 1 time, ⌊1/3⌋ = 0, 1 > 0

nums = [1, 2]
Answer = [1, 2]              // both appear once, ⌊2/3⌋ = 0, 1 > 0
```

The key structural fact that drives every efficient solution: **at most two**
distinct values can appear more than `n/3` times in an array of length `n`.
If three distinct values each appeared more than `n/3` times, their counts
would sum to more than `n`, which is impossible since there are only `n`
elements total. This bounds the output size to at most 2 and hints that a
generalized Boyer-Moore voting scheme (tracking two running candidates
instead of one) can solve the problem in a single linear pass.

The constraints push toward an `O(n)` time, `O(1)` extra-space solution
(besides the output list).

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For each distinct value, scan the whole array and count its occurrences.

```java
class NaiveSolution implements MajorityElement2 {
    @Override
    public List<Integer> majorityElement(int[] nums) {
        var result = new ArrayList<Integer>();
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            // Skip values we've already confirmed/rejected.
            if (result.contains(nums[i])) continue;
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count > n / 3) result.add(nums[i]);
        }
        return result;
    }
}
```

**Complexity:** Time `O(n²)`, Space `O(1)` (ignoring the output list).

**Drawback:** Every candidate value triggers a full linear rescan of the
array. We're recomputing frequency information from scratch for each
distinct value instead of accumulating counts in one pass.

---

### 2. HashMap Counting — O(n) Time, O(n) Space

Count every value's frequency in one pass with a hash map, then filter.

```java
class HashMapSolution implements MajorityElement2 {
    @Override
    public List<Integer> majorityElement(int[] nums) {
        var result = new ArrayList<Integer>();
        var freq = new HashMap<Integer, Integer>();
        for (int num : nums) {
            freq.merge(num, 1, Integer::sum);
        }
        for (var entry : freq.entrySet()) {
            if (entry.getValue() > nums.length / 3) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
```

**Complexity:** Time `O(n)`, Space `O(n)` for the frequency map.

**Drawback:** Correct and simple, but the `O(n)` auxiliary map is avoidable.
Since at most 2 values can ever qualify, we don't actually need to remember
the frequency of *every* distinct value — only enough information to narrow
the search down to at most two "serious" candidates.

---

### 3. Sorting — O(n log n) Time, O(1) Space

Sort the array; any value with frequency `> n/3` must occupy at least one of
the positions `i`, `i + n/3`, or `i + 2*n/3` relative to its own run (because
a run longer than `n/3` can't "hide" between these evenly spaced checkpoints).
A simpler equivalent: after sorting, scan for runs of equal elements and keep
those longer than `n/3`.

```java
class SortingSolution implements MajorityElement2 {
    @Override
    public List<Integer> majorityElement(int[] nums) {
        var result = new ArrayList<Integer>();
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        int i = 0;
        while (i < n) {
            int j = i;
            while (j < n && sorted[j] == sorted[i]) j++;
            if (j - i > n / 3) result.add(sorted[i]);
            i = j;
        }
        return result;
    }
}
```

**Complexity:** Time `O(n log n)` (dominated by the sort), Space `O(1)`
extra (ignoring the sorted copy and output).

**Drawback:** Sorting imposes an unnecessary `O(n log n)` floor. Since the
problem only ever needs to track at most 2 "heavy" values, a single linear
scan that maintains two running candidates can beat this, dropping to `O(n)`.

---

### 4. Optimized Solution (Generalized Boyer-Moore Voting) — O(n) Time, O(1) Space

**Key Insight:** Because at most 2 values can have frequency `> n/3`, run a
generalized Boyer-Moore vote with **two** candidate/count slots instead of
one. Every element either reinforces an existing candidate, fills an empty
candidate slot, or — if neither applies — "cancels" one vote from *both*
candidates simultaneously. A final verification pass re-counts the two
surviving candidates exactly, since the voting phase alone only guarantees
*candidacy*, not an actual majority.

```java
class MajorityElement2Impl implements MajorityElement2 {

    @Override
    public List<Integer> majorityElement(int[] nums) {
        var result = new ArrayList<Integer>();
        if (nums.length == 0) return result;

        // At most 2 elements can appear more than n/3 times, so track two
        // candidates simultaneously using the Boyer-Moore voting algorithm.
        int candidate1 = 0, candidate2 = 0;
        int count1 = 0, count2 = 0;

        for (int num : nums) {
            if (count1 > 0 && num == candidate1) {
                count1++;
            } else if (count2 > 0 && num == candidate2) {
                count2++;
            } else if (count1 == 0) {
                candidate1 = num;
                count1 = 1;
            } else if (count2 == 0) {
                candidate2 = num;
                count2 = 1;
            } else {
                count1--;
                count2--;
            }
        }

        // Candidates may not be true majorities, so verify actual counts.
        count1 = 0;
        count2 = 0;
        for (int num : nums) {
            if (num == candidate1) count1++;
            else if (num == candidate2) count2++;
        }

        if (count1 > nums.length / 3) result.add(candidate1);
        if (count2 > nums.length / 3) result.add(candidate2);

        return result;
    }
}
```

**Complexity:** Time `O(n)` (two linear passes), Space `O(1)` extra.

---

### Correctness Proof

**Definitions.** For `0 <= t <= n`, let `candidate1(t)`, `count1(t)`,
`candidate2(t)`, `count2(t)` denote the values of those variables
immediately *after* the voting loop has processed the prefix `nums[0..t)`
(so `t = 0` is the initial state, `t = n` is the state entering the
verification pass). At each step `t -> t+1`, exactly one of five branches
executes on `num = nums[t]`:
- **(A)** reinforce candidate 1: `count1(t) > 0` and `num == candidate1(t)`,
- **(B)** reinforce candidate 2: (A) false, `count2(t) > 0`, `num == candidate2(t)`,
- **(C)** fill empty slot 1: (A),(B) false, `count1(t) == 0`,
- **(D)** fill empty slot 2: (A),(B),(C) false, `count2(t) == 0`,
- **(E)** cancel: none of the above, i.e. `count1(t) > 0`, `count2(t) > 0`,
  `num != candidate1(t)`, `num != candidate2(t)`.

Define the *residual multiset* `Residual(t)` to contain `count1(t)` copies of
`candidate1(t)` (if `count1(t) > 0`) and `count2(t)` copies of `candidate2(t)`
(if `count2(t) > 0`). Define `Rem(t)` as the multiset union of one triple
`{candidate1(s), candidate2(s), nums[s]}` for every index `s < t` at which
branch (E) executed.

**Lemma 1 (Candidate distinctness).**
For every `t`, if `count1(t) > 0` and `count2(t) > 0`, then
`candidate1(t) != candidate2(t)`.

*Proof by induction on `t`.* **Base case `t = 0`:** `count1 = count2 = 0`,
so the hypothesis is vacuous.

**Inductive step:** assume the claim for `t`; show it for `t + 1` by cases
on the branch taken at step `t`.
- **(A):** only `count1` changes (`candidate1`, `candidate2`, `count2`
  untouched). If `count2(t+1) > 0`, then `count2(t) > 0` too (unchanged), so
  by IH `candidate1(t) != candidate2(t)`; since `candidate1(t+1) = candidate1(t)`
  and `candidate2(t+1) = candidate2(t)`, the inequality persists.
- **(B):** symmetric to (A) — only `count2`/`candidate2` change.
- **(C):** `candidate1(t+1) = num`, `count1(t+1) = 1`. Branch (C) requires (B)
  false. If `count2(t) > 0`, (B) being false (with `count2(t) > 0` true)
  forces `num != candidate2(t)`. Since `candidate2(t+1) = candidate2(t)`
  (unchanged in this branch), `candidate1(t+1) = num != candidate2(t+1)`
  whenever `count2(t+1) = count2(t) > 0`.
- **(D):** `candidate2(t+1) = num`, `count2(t+1) = 1`. Branch (D) requires (A)
  false. Reaching (D) also requires (C) false, i.e. `count1(t) > 0` (else (C)
  would have fired). With `count1(t) > 0` true, (A) being false forces
  `num != candidate1(t)`. Since `candidate1(t+1) = candidate1(t)`, we get
  `candidate2(t+1) = num != candidate1(t+1)` whenever `count1(t+1) > 0`.
- **(E):** candidates are untouched; both counts decrease but branch (E)
  requires `count1(t) > 0` and `count2(t) > 0`, so by IH
  `candidate1(t) != candidate2(t)` already, and this is simply carried over
  unchanged. ∎

**Lemma 2 (Decomposition invariant).**
For every `t`, the prefix multiset `{nums[0], ..., nums[t-1]}` equals
`Rem(t) ⊎ Residual(t)` (disjoint multiset union), and `Rem(t)` decomposes
into disjoint triples of **pairwise-distinct** values.

*Proof by induction on `t`.* **Base case `t = 0`:** both sides are empty.

**Inductive step:** assume the decomposition holds at `t`; verify it after
processing `nums[t]`, by cases on the branch taken.
- **(A)/(B):** one unit is added to `count1` or `count2` respectively, and
  `num` equals the corresponding candidate, so `Residual(t+1) = Residual(t) ⊎ {num}`
  and `Rem(t+1) = Rem(t)`. The prefix grows by exactly `{num}`, matching.
- **(C)/(D):** an empty slot (`count = 0`, contributing nothing to
  `Residual(t)`) is filled with `num`, so `Residual(t+1) = Residual(t) ⊎ {num}`
  and `Rem(t+1) = Rem(t)`. Matches the prefix growing by `{num}`.
- **(E):** `count1` and `count2` each drop by one, removing one copy of
  `candidate1(t)` and one copy of `candidate2(t)` from `Residual(t)`, while
  `Rem(t+1) = Rem(t) ⊎ {candidate1(t), candidate2(t), num}`. Branch (E)'s
  guard gives `count1(t) > 0`, `count2(t) > 0`, so by **Lemma 1**
  `candidate1(t) != candidate2(t)`; the same guard gives `num != candidate1(t)`
  and `num != candidate2(t)` directly. Hence the new triple is pairwise
  distinct, and
  `Rem(t+1) ⊎ Residual(t+1) = Rem(t) ⊎ Residual(t) ⊎ {candidate1(t), candidate2(t), num} \ {candidate1(t), candidate2(t)}`
  which simplifies to the old decomposition plus exactly `{num}` — matching
  the prefix growing by `{num}`. ∎

**Theorem (Completeness — every true majority survives to the verification
pass).**
If a value `v` satisfies `freq_nums(v) > n/3` (a *true majority*, where
`freq_nums(v)` is `v`'s total count in `nums`), then after the voting loop
(`t = n`), either `candidate1(n) == v` with `count1(n) > 0`, or
`candidate2(n) == v` with `count2(n) > 0`.

*Proof.* By **Lemma 2** with `t = n`, `nums = Rem(n) ⊎ Residual(n)`, so
```
freq_nums(v) = freq_Rem(n)(v) + freq_Residual(n)(v).
```
`Rem(n)` decomposes into `k = |Rem(n)| / 3` disjoint, pairwise-distinct
triples, and each triple contributes at most 1 copy of `v` (its 3 elements
are mutually distinct, so `v` can equal at most one of them). Hence
`freq_Rem(n)(v) <= k = |Rem(n)| / 3 <= n / 3`, since `|Rem(n)| <= n` (it's a
sub-multiset of the length-`n` array).

Suppose, for contradiction, that `v` is *not* one of the surviving
candidates with positive count, i.e. `freq_Residual(n)(v) = 0` (because
`Residual(n)` only ever contains copies of `candidate1(n)` and
`candidate2(n)`, each with positive count by construction). Then
```
freq_nums(v) = freq_Rem(n)(v) <= n / 3,
```
contradicting `freq_nums(v) > n/3`. Therefore `freq_Residual(n)(v) > 0`,
meaning `v` equals `candidate1(n)` (with `count1(n) > 0`) or `candidate2(n)`
(with `count2(n) > 0`). ∎

**Theorem (Overall correctness).**
`MajorityElement2Impl.majorityElement` returns exactly the set of values
with `freq_nums(v) > n/3`.

*Proof.* **Soundness:** the verification pass recomputes `count1`/`count2`
by an exact, direct scan of the entire array (`count1 = freq_nums(candidate1(n))`,
`count2 = freq_nums(candidate2(n))`), and only adds a candidate to the
result if that exact count exceeds `n/3`. So nothing is ever added
incorrectly.

**Completeness:** by the theorem above, every true majority `v` is equal to
`candidate1(n)` or `candidate2(n)` after the voting loop. The verification
pass then computes `v`'s *exact* frequency (since it scans the whole array
checking equality with `candidate1(n)`/`candidate2(n)`, and `v` matches one
of them), finds `freq_nums(v) > n/3`, and adds it to the result.

Hence the returned set is both sound and complete: it equals exactly
`{v : freq_nums(v) > n/3}`. ∎

---

## Optimization Journey

```
Naive (per-value rescan) ─────────────────────────────────────────────►
       For each distinct value, scan the whole array to count it.
       Time: O(n²)    Space: O(1)

           ▼  Observation: a single pass with a hash map can tally every
              value's frequency at once, instead of rescanning per value.

HashMap Counting ──────────────────────────────────────────────────────►
       One pass builds exact frequencies for all distinct values, then
       filter those exceeding n/3.
       Time: O(n)    Space: O(n)   ← frequency map sized by distinct values

           ▼  Observation: at most 2 values can ever qualify, so tracking
              every distinct value's count is overkill — sorting groups
              equal values into contiguous runs, letting O(1)-space run
              detection replace the hash map.

Sorting ────────────────────────────────────────────────────────────────►
       Sort, then scan for runs longer than n/3.
       Time: O(n log n)    Space: O(1) extra (excluding sort/output)

           ▼  Observation: since only 2 candidates can ever matter, a
              generalized Boyer-Moore vote can track exactly two
              candidate/count pairs in one linear pass, cancelling a vote
              from both whenever a third, different value appears —
              avoiding the sort entirely.

Generalized Boyer-Moore Voting ─────────────────────────────────────────►
       One pass to find at most 2 surviving candidates, one more pass to
       verify their exact counts against n/3.
       Time: O(n)    Space: O(1) extra
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (per-value rescan) | O(n²) | O(1) | Rescans the array once per distinct value |
| HashMap Counting | O(n) | O(n) | Exact counts for every distinct value |
| Sorting | O(n log n) | O(1) | Groups equal values into runs after sorting |
| Generalized Boyer-Moore Voting | O(n) | O(1) | Tracks exactly 2 candidates; verified by `MajorityElement2Impl` |

> **Best solution:** `MajorityElement2Impl` (Generalized Boyer-Moore Voting)
> — linear time and constant extra space, exploiting the fact that at most
> 2 values can ever exceed the `n/3` threshold, with every true majority
> provably surviving the voting phase as one of the two tracked candidates
> (see Correctness Proof above).
