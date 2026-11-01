# Count the Number of Good Subarrays

## Problem Statement

You are given an integer array `nums` and an integer `k`. Return the number of
**good** subarrays of `nums`.

A subarray `arr` is **good** if there are **at least `k`** pairs of indices
`(i, j)` such that `i < j` and `arr[i] == arr[j]`.

A subarray is a contiguous, non-empty sequence of elements within an array.

**Example:**
```
nums = [1, 1, 1, 1, 1], k = 10
Only the whole array has C(5,2) = 10 equal pairs
Answer = 1

nums = [3, 1, 4, 3, 2, 2, 4], k = 2
Good subarrays: [3,1,4,3,2,2], [3,1,4,3,2,2,4], [1,4,3,2,2,4], [4,3,2,2,4]
Answer = 4
```

**Constraints:** `1 ≤ nums.length ≤ 10^5`, `1 ≤ nums[i], k ≤ 10^9`.

---

## Building Block — Counting Equal Pairs Incrementally

If a value `x` appears `f` times in a window, it contributes `C(f, 2)` equal
pairs. We never need to recompute that from scratch:

- **Adding** a new copy of `x` pairs it with each of the `f` copies already
  there → `pairs += f` (then `f → f + 1`).
- **Removing** a copy of `x` breaks its pairing with the other `f − 1`
  copies → `pairs −= f − 1` (then `f → f − 1`).

So with a frequency map, the window's pair count can be maintained in `O(1)`
per step.

---

## Solution — Sliding Window ("at least k" counting)

**Key Insight.** Fix the right end `r`. "At least `k` equal pairs" is
**monotonic** in the left end: moving the start of a subarray to the left only
adds elements, and so only adds pairs. If `nums[i..r]` is good, then every
`nums[i'..r]` with `i' ≤ i` is good too.

So for each `r`, the good starts form a **prefix** `0, 1, …, l − 1`, where `l`
is the **smallest** start for which `nums[l..r]` is *not* good. Exactly `l`
good subarrays end at `r`.

As `r` moves right, that boundary `l` never moves left (appending an element
only adds pairs, so a start that was good stays good). Both pointers move only
forward — the classic **two-pointer sliding window**.

```java
public interface CountTheNumberOfGoodSubarrays {
    long countGood(int[] nums, int k);
}

class CountTheNumberOfGoodSubarraysImpl implements CountTheNumberOfGoodSubarrays {

    private void add(Map<Integer, Integer> freq, int num){
        freq.put(num, freq.getOrDefault(num, 0)+1);
    }

    private void remove(Map<Integer, Integer> freq, int num){
        int v = freq.get(num);
        if(v == 1){
            freq.remove(num);
        }else{
            freq.put(num, v-1);
        }
    }

    @Override
    public long countGood(int[] nums, int k) {
        int l = 0;
        int pairs = 0;
        long answer = 0;
        Map<Integer, Integer> freq = new HashMap<>();

        for(int r=0; r<nums.length; r++){

            // add leading element
            add(freq, nums[r]);
            pairs += freq.get(nums[r])-1;

            // remove trailing elements while constraint is violated
            while(pairs >= k){
                pairs -= (freq.get(nums[l])-1);
                remove(freq, nums[l]);
                l++;
            }

            // for l, condition is violated, for index in [0, l) condition is satisfied.
            answer += l;
        }

        return answer;
    }
}
```

**How it works:**
1. Maintain a window `nums[l..r]`, a frequency map `freq` of its elements,
   and `pairs` = number of equal pairs inside it.
2. For each `r`:
   - **Expand:** add `nums[r]`. After the `add`, `freq[nums[r]]` counts the new
     copy too, so the number of new pairs is `freq[nums[r]] − 1`.
   - **Shrink:** while the window is good (`pairs ≥ k`), drop `nums[l]`. It was
     paired with the other `freq[nums[l]] − 1` copies, so subtract that from
     `pairs`, then `l++`. Every start we drop is a good start for this `r`.
   - **Count:** now `nums[l..r]` is not good, but every `nums[i..r]` with
     `i < l` is (each one contains a window we saw as good). That adds `l`
     subarrays ending at `r`.
3. The shrink loop always stops: once the window is empty, `pairs = 0 < k`.

> **Why does `l` count earlier starts too?** Every start `i < l` was dropped
> while the window `nums[i..r']` was good, for some `r' ≤ r`. Extending it to
> `nums[i..r]` only adds pairs, so it is still good. Starts dropped for earlier
> `r` values therefore keep counting for every later `r`, which is why we add
> `l` itself and not just how far `l` moved in this step.

> **Why `≥ k` and not `== k`?** Unlike counting single occurrences, one step
> can add **many** pairs at once. Adding a value that already appears `f`
> times jumps `pairs` by `f`, so it can leap from below `k` straight past it.
> For `nums = [1,1,1,1], k = 2`, adding the third `1` takes `pairs` from `1`
> to `3`. A check of `pairs == k` would never shrink the window and would
> return `0` instead of `3`.

> **Why `long` for `answer`, but `int` for `pairs`?** With `n = 10^5`, the number of
> subarrays can reach `n(n+1)/2 ≈ 5 · 10^9`, which overflows `int`. `pairs`
> is `< k` before each addition, and one addition adds at most `n − 1`, so it
> stays below `k + n ≤ 10^9 + 10^5`. That fits in an `int`.

---

## Step-by-step Trace

```
nums = [3, 1, 4, 3, 2, 2, 4], k = 2, n = 7
(window is [l, r], both inclusive)

r=0: add 3(+0) → p=0                         l=0  += 0   total=0
r=1: add 1(+0) → p=0                         l=0  += 0   total=0
r=2: add 4(+0) → p=0                         l=0  += 0   total=0
r=3: add 3(+1) → p=1                         l=0  += 0   total=0
r=4: add 2(+0) → p=1                         l=0  += 0   total=0
r=5: add 2(+1) → p=2 ≥ 2, shrink:
       remove 3: freq[3]=2 → p -= 1 → p=1, l=1
                                             l=1  += 1   total=1
     ([3,1,4,3,2,2])
r=6: add 4(+1) → p=2 ≥ 2, shrink:
       remove 1: freq[1]=1 → p -= 0 → p=2, l=2
       remove 4: freq[4]=2 → p -= 1 → p=1, l=3
                                             l=3  += 3   total=4
     ([3,1,4,3,2,2,4], [1,4,3,2,2,4], [4,3,2,2,4])

Answer: 4 ✓
```

```
nums = [1, 1, 1, 1, 1], k = 10, n = 5

r=0..3: add 1(+0) 1(+1) 1(+2) 1(+3) → p=6 < 10, l=0, nothing added
r=4:    add 1(+4) → p=10 ≥ 10, shrink:
          remove 1: freq[1]=5 → p -= 4 → p=6, l=1
                                             l=1  += 1   total=1

Answer: 1 ✓
```

```
nums = [1, 1, 1, 1], k = 2, n = 4   (the "overshoot" case)

r=0: add 1(+0) → p=0                         l=0  += 0   total=0
r=1: add 1(+1) → p=1                         l=0  += 0   total=0
r=2: add 1(+2) → p=3 ≥ 2  (p jumped past k), shrink:
       remove 1: freq[1]=3 → p -= 2 → p=1, l=1
                                             l=1  += 1   total=1
     ([1,1,1] at 0..2)
r=3: add 1(+2) → p=3 ≥ 2, shrink:
       remove 1: freq[1]=3 → p -= 2 → p=1, l=2
                                             l=2  += 2   total=3
     ([1,1,1,1] at 0..3, [1,1,1] at 1..3)

Answer: 3 ✓
```

---

## Correctness Proof

Let `n = nums.length`, and let `P(i, r)` be the number of equal pairs in
`nums[i..r]` (with `P(r+1, r) = 0` for the empty window). For each `r`, let
`s(r)` = the smallest `i ∈ [0, r+1]` with `P(i, r) < k`. Such an `i` always
exists because `P(r+1, r) = 0 < k`. Let `s(−1) = 0`.

### Lemma 1 (Good starts form a prefix)

For a fixed `r`, `nums[i..r]` is good **iff** `i < s(r)`.

*Proof.* Every pair inside `nums[i+1..r]` is also inside `nums[i..r]`, so
`P(i, r)` is non-increasing in `i`. For `i < s(r)`, `P(i, r) ≥ k` by
minimality of `s(r)`. For `i ≥ s(r)`, `P(i, r) ≤ P(s(r), r) < k`. ∎

Hence exactly `s(r)` good subarrays end at `r`, and the answer is
`Σᵣ s(r)`.

### Lemma 2 (`s(r)` is non-decreasing)

`s(r) ≤ s(r+1)`.

*Proof.* `P(i, r+1) ≥ P(i, r)`, because appending an element only adds
pairs. So every `i < s(r)` has `P(i, r+1) ≥ k`, which gives `s(r+1) ≥ s(r)`. ∎

### Lemma 3 (Incremental pair updates)

If `x` occurs `f` times in a window, appending `x` increases the pair count
by exactly `f`, and removing one copy of `x` decreases it by exactly `f − 1`.

*Proof.* `C(f+1, 2) − C(f, 2) = f` and `C(f, 2) − C(f−1, 2) = f − 1`; pairs of
other values are unaffected. ∎

### Theorem

**Invariant.** At the start of iteration `r`, `freq` is the multiset of
`nums[l..r−1]`, `pairs = P(l, r−1)`, and `l = s(r−1)`.

- **Initialization.** `r = 0`, `l = 0 = s(−1)`, the window is empty and
  `pairs = 0`.
- **Expansion.** By Lemma 3, after adding `nums[r]`, `pairs = P(l, r)`.
- **Shrinking.** By Lemma 2, `l = s(r−1) ≤ s(r)`. By Lemma 1, for every `i`
  with `l ≤ i < s(r)` we have `P(i, r) ≥ k`, and `P(s(r), r) < k`. The loop
  removes `nums[l]` while `pairs ≥ k`, and each removal keeps
  `pairs = P(l, r)` by Lemma 3. So it stops exactly when `l = s(r)`. It never
  removes from an empty window, since an empty window has `pairs = 0 < k`.
- **Counting.** The code adds `l = s(r)`, which matches Lemma 1.
- **Maintenance.** Now `freq` is the multiset of `nums[s(r)..r]` and
  `pairs = P(s(r), r)`, which is the invariant for `r + 1`.

Each `r` therefore contributes exactly `s(r)`, and the sum is the answer. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| Sliding window (`r` and `l` each move forward at most `n` times) | O(n) | O(n) |
| Frequency map updates (`HashMap`, expected) | O(1) each | — |
| **Overall** | **O(n)** expected | **O(n)** |

> The inner `while` does not make this O(n²): `l` is never reset, so across
> the whole run it advances at most `n` times in total.
