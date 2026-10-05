# Majority Element

## Problem Statement

Given an integer array `nums` of size `n`, return the element that appears
**more than `⌊n/2⌋` times**. If no such element exists, return `-1`.

**Example:**
```
nums = [3, 2, 3]
Answer = 3                   // 3 appears 2 times, ⌊3/2⌋ = 1, 2 > 1

nums = [2, 2, 1, 1, 1, 2, 2]
Answer = 2                   // 2 appears 4 times, ⌊7/2⌋ = 3, 4 > 3

nums = [1, 2, 3]
Answer = -1                  // no value appears more than 1 time
```

Note this is a slight generalization of the classic LeetCode 169 "Majority
Element" problem: the classic version *guarantees* a majority exists (so any
candidate found by voting is automatically correct), while here the
implementation must also be able to report that **no** majority exists.

**Key structural fact:** at most **one** distinct value can appear more than
`n/2` times in an array of length `n` (two such values would need more than
`n` elements combined). This uniqueness is exactly what makes a single-pass
Boyer-Moore voting scheme possible for *finding a candidate*: at most one
"serious" candidate ever needs to be tracked. However — as shown below — a
genuine **verification pass** is still required to confirm the candidate
really is a majority, because the voting phase alone cannot distinguish "no
majority exists" from "a majority exists and is this candidate."

The constraints push toward an `O(n)` time, `O(1)` extra-space solution.

---

## Solutions

### 1. Naive Solution (Brute Force) — O(n²) Time, O(1) Space

For each element, scan the whole array and count its occurrences.

```java
class NaiveSolution implements MajorityElement {
    @Override
    public int majorityElement(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count > n / 2) return nums[i];
        }
        return -1;
    }
}
```

**Complexity:** Time `O(n²)`, Space `O(1)`.

**Drawback:** Every candidate value triggers a full linear rescan of the
array, recomputing frequency information from scratch instead of
accumulating counts in a single pass.

---

### 2. HashMap Counting — O(n) Time, O(n) Space

Count every value's frequency in one pass with a hash map, then check which
(if any) exceeds `n/2`.

```java
class HashMapSolution implements MajorityElement {
    @Override
    public int majorityElement(int[] nums) {
        var freq = new HashMap<Integer, Integer>();
        for (int num : nums) {
            freq.merge(num, 1, Integer::sum);
        }
        for (var entry : freq.entrySet()) {
            if (entry.getValue() > nums.length / 2) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
```

**Complexity:** Time `O(n)`, Space `O(n)` for the frequency map.

**Drawback:** Correct and simple, but the `O(n)` auxiliary map is avoidable.
Since at most one value can ever qualify, we don't need to remember the
frequency of every distinct value — only enough information to narrow the
search down to a single "serious" candidate.

---

### 3. Sorting — O(n log n) Time, O(1) Space

Sort the array. If a majority element exists, it must occupy the middle
index `n/2` of the sorted array (a run longer than `n/2` can't avoid
covering the midpoint).

```java
class SortingSolution implements MajorityElement {
    @Override
    public int majorityElement(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        int candidate = sorted[n / 2];
        int count = 0;
        for (int num : nums) {
            if (num == candidate) count++;
        }
        return count > n / 2 ? candidate : -1;
    }
}
```

**Complexity:** Time `O(n log n)` (dominated by the sort), Space `O(1)`
extra (ignoring the sorted copy).

**Drawback:** Sorting imposes an unnecessary `O(n log n)` floor. Since only
one candidate ever needs to be tracked, a single linear scan maintaining one
running candidate/vote counter can beat this, dropping to `O(n)`.

---

### 4. Optimized Solution (Boyer-Moore Voting, Two-Pass) — O(n) Time, O(1) Space

**Key Insight:** Pair up occurrences of a true majority element against
occurrences of every other value, one-to-one; because the majority element
strictly outnumbers *all other elements combined*, this pairing can never
fully eliminate it, so it always survives the voting phase as the final
candidate. A single running `(candidate, diff)` pair is enough: matching the
current candidate increments `diff`, mismatching decrements it, and whenever
`diff` hits `0` the candidate is replaced by the current element.

Crucially, the voting phase only proves *candidacy* — it does **not**
reliably expose the candidate's true frequency (see the "Bug History"
section below for a concrete counterexample where a shortcut formula fails).
A second, explicit linear pass is required to re-count the surviving
candidate's exact frequency before trusting it.

```java
class MajorityElementImpl implements MajorityElement {
    private int vote(int[] nums) {
        int diff = 0;
        int majority = -1;

        for (int num : nums) {
            if (diff == 0) {
                majority = num;
            }

            if (num == majority) {
                diff += 1;
            } else {
                diff -= 1;
            }
        }

        return majority;
    }

    @Override
    public int majorityElement(int[] nums) {
        int candidate = vote(nums);

        int count = 0;
        for (int num : nums) {
            if (num == candidate) {
                count += 1;
            }
        }

        if (count <= nums.length / 2) {
            return -1;
        }

        return candidate;
    }
}
```

**Complexity:** Time `O(n)` (one voting pass + one verification pass),
Space `O(1)` extra.

---

### Correctness Proof

**Definitions.** For `0 <= t <= n`, let `candidate(t)` and `diff(t)` denote
the values of those variables immediately *after* the loop in `vote()` has
processed the prefix `nums[0..t)` (`candidate(0) = -1`, `diff(0) = 0`,
and `t = n` is the state returned by `vote()`). At each step `t -> t+1`, with
`num = nums[t]`, exactly one of three branches executes:
- **(Reset)** `diff(t) == 0`: `candidate(t+1) = num`; since
  `num == candidate(t+1)` trivially, `diff(t+1) = diff(t) + 1 = 1`.
- **(Match)** `diff(t) != 0` and `num == candidate(t)`:
  `candidate(t+1) = candidate(t)`, `diff(t+1) = diff(t) + 1`.
- **(Mismatch)** `diff(t) != 0` and `num != candidate(t)`:
  `candidate(t+1) = candidate(t)`, `diff(t+1) = diff(t) - 1`.

**Lemma 1 (`diff` stays non-negative).**
For every `t`, `diff(t) >= 0`.

*Proof by induction.* **Base case:** `diff(0) = 0`. **Inductive step:** in
**(Reset)**, `diff(t+1) = 1 >= 0`. In **(Match)**, `diff(t+1) = diff(t) + 1 > diff(t) >= 0`.
In **(Mismatch)**, this branch only fires when `diff(t) != 0`; combined with
the IH `diff(t) >= 0`, we get `diff(t) >= 1`, so `diff(t+1) = diff(t) - 1 >= 0`. ∎

**Lemma 2 (Decomposition invariant).**
For every `t`, the prefix multiset `{nums[0], ..., nums[t-1]}` equals
`Rem(t) ⊎ Residual(t)` (disjoint multiset union), where:
- `Residual(t)` is the multiset of **surviving, uncancelled votes** for the
  current candidate — it contains exactly `diff(t)` copies of `candidate(t)`.
- `Rem(t)` is the multiset of **already-cancelled votes** — it decomposes
  into disjoint **pairs of distinct values** (i.e. `|Rem(t)|` is even and each
  pair `{a, b}` has `a != b`), each pair representing a mismatch that
  cancelled one vote for the old candidate against the new element.

*Proof by induction on `t`.* **Base case `t = 0`:** both sides empty.

**Inductive step**, by branch at step `t`:
- **(Reset):** `Residual(t) ` is empty here, because reset only fires when
  `diff(t) == 0`. The new element `num` becomes `1` copy of the new
  candidate: `Residual(t+1) = {num}`, `Rem(t+1) = Rem(t)`. The prefix grows
  by exactly `{num}`, matching.
- **(Match):** one unit is added to `diff`, and `num` equals
  `candidate(t) = candidate(t+1)`, so `Residual(t+1) = Residual(t) ⊎ {num}`
  and `Rem(t+1) = Rem(t)`. Matches the prefix growing by `{num}`.
- **(Mismatch):** `diff` drops by one, removing one copy of `candidate(t)`
  from `Residual(t)` (valid since this branch requires `diff(t) > 0`, so
  `Residual(t)` is non-empty), while
  `Rem(t+1) = Rem(t) ⊎ {candidate(t), num}`. This branch's guard gives
  `num != candidate(t)` directly, so the new pair is a pair of distinct
  values. Removing one `candidate(t)` from `Residual(t)` and adding the pair
  `{candidate(t), num}` to `Rem(t)` nets out to the prefix growing by
  exactly `{num}`, matching. ∎

**Theorem (Completeness — a true majority always survives voting as the
final candidate).**
If a value `v` satisfies `freq(v) > n/2` in `nums[0..n)`, then
`candidate(n) == v` (i.e. `vote(nums) == v`).

*Proof.* By **Lemma 2** with `t = n`,
```
freq(v) = freq_Rem(n)(v) + freq_Residual(n)(v).
```
`Rem(n)` decomposes into `|Rem(n)| / 2` disjoint pairs of distinct values,
and each pair contributes at most 1 copy of `v` (its two elements are
distinct, so `v` equals at most one of them). Hence
`freq_Rem(n)(v) <= |Rem(n)| / 2 <= n / 2`, since `|Rem(n)| <= n`.

Suppose, for contradiction, `candidate(n) != v`. Then `Residual(n)` (which
only ever contains copies of `candidate(n)`) contributes `0` copies of `v`,
so `freq(v) = freq_Rem(n)(v) <= n/2`, contradicting `freq(v) > n/2`.
Therefore `candidate(n) == v`. ∎

**Theorem (Overall correctness).**
`MajorityElementImpl.majorityElement` returns `v` whenever `freq(v) > n/2`
for some `v`, and `-1` otherwise.

*Proof.* **Soundness:** `count` is computed by an exact, direct scan of the
entire array comparing every element against `candidate`, so
`count == freq(candidate)` exactly. The method only returns `candidate` when
`count > n/2`, i.e. when `candidate`'s exact frequency genuinely exceeds
`n/2` — so a non-`(-1)` result is always a true majority.

**Completeness:** by the theorem above, if some `v` has `freq(v) > n/2`,
then `candidate = vote(nums) == v`, and the verification pass computes
`count = freq(v) > n/2`, so the method returns `v` rather than `-1`.

**No false positives when no majority exists:** if no value has
`freq(v) > n/2`, then in particular `freq(candidate) <= n/2` (since
`candidate` is some value that actually occurs in `nums`, for `n >= 1`), so
the verification pass computes `count <= n/2` and the method correctly
returns `-1`.

Hence the method returns exactly the unique majority element when one
exists, and `-1` otherwise. ∎

---

### Bug History: Why the Verification Pass Cannot Be Skipped

An earlier version of this solution tried to avoid the second scan by
deriving the candidate's count algebraically from the final `diff`, via the
formula `count = (n + diff) / 2`, reasoning that `diff` is a "signed vote
total" for the candidate. **This formula is unsound** whenever the candidate
changes more than once and no true majority exists, because elements
discarded during *earlier, already-cancelled* candidate runs are silently
dropped from the accounting — `Rem(t)`'s pairs are invisible to the formula,
yet they can include occurrences of the value that later becomes the final
candidate.

**Counterexample:** `nums = [1, 1, 2, 2, 3]` (`n = 5`; no value appears more
than `⌊5/2⌋ = 2` times, so the correct answer is `-1`). Tracing the voting
loop:

| `t` | `num` | branch | `candidate` | `diff` |
|---|---|---|---|---|
| 0 | 1 | Reset | 1 | 1 |
| 1 | 1 | Match | 1 | 2 |
| 2 | 2 | Mismatch | 1 | 1 |
| 3 | 2 | Mismatch | 1 | 0 |
| 4 | 3 | Reset | 3 | 1 |

Final state: `candidate = 3`, `diff = 1`. The flawed formula computes
`count = (5 + 1) / 2 = 3`, which exceeds `n/2 = 2`, so the buggy
implementation incorrectly returned `3` — even though `3` occurs only once
in `nums`. The true frequency of `candidate = 3` is `1`, confirmed only by
an explicit re-scan.

This is exactly why the corrected `MajorityElementImpl` above performs a
genuine second pass: the **Completeness Theorem** only guarantees that a
*true* majority survives voting as the candidate — it says nothing about
what the candidate's `diff` means when no majority exists, and the
discarded `Rem(t)` pairs make any attempt to reconstruct the exact count
without re-scanning unsound in general.

---

## Optimization Journey

```
Naive (per-value rescan) ─────────────────────────────────────────────►
       For each value, scan the whole array to count it.
       Time: O(n²)    Space: O(1)

           ▼  Observation: a single pass with a hash map can tally every
              value's frequency at once, instead of rescanning per value.

HashMap Counting ──────────────────────────────────────────────────────►
       One pass builds exact frequencies for all distinct values, then
       checks which (if any) exceeds n/2.
       Time: O(n)    Space: O(n)   ← frequency map sized by distinct values

           ▼  Observation: at most 1 value can ever qualify, so tracking
              every distinct value's count is overkill — sorting places
              any majority element at the array's midpoint, letting
              O(1)-space index lookup replace the hash map.

Sorting ────────────────────────────────────────────────────────────────►
       Sort, then check the frequency of the middle element.
       Time: O(n log n)    Space: O(1) extra (excluding sort)

           ▼  Observation: since only 1 candidate can ever matter, a
              Boyer-Moore vote can track exactly one candidate/diff pair in
              one linear pass, cancelling a vote whenever a mismatching
              value appears — avoiding the sort entirely. A second,
              genuine verification pass (not an algebraic shortcut — see
              Bug History above) confirms the exact count.

Boyer-Moore Voting (two-pass) ──────────────────────────────────────────►
       One pass finds the surviving candidate, one more pass verifies its
       exact true frequency against n/2.
       Time: O(n)    Space: O(1) extra
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (per-value rescan) | O(n²) | O(1) | Rescans the array once per value |
| HashMap Counting | O(n) | O(n) | Exact counts for every distinct value |
| Sorting | O(n log n) | O(1) | Majority element, if any, sits at index n/2 |
| Boyer-Moore Voting (two-pass) | O(n) | O(1) | Tracks 1 candidate, verifies with an explicit re-scan; implemented by `MajorityElementImpl` |

> **Best solution:** `MajorityElementImpl` (Boyer-Moore Voting, two-pass) —
> linear time and constant extra space, exploiting the fact that at most 1
> value can ever exceed the `n/2` threshold. The voting phase alone only
> guarantees a true majority *survives* as the candidate (Completeness
> Theorem above); an explicit second scan is mandatory to confirm the exact
> count, since no algebraic shortcut from the voting state alone is sound
> (see Bug History).
