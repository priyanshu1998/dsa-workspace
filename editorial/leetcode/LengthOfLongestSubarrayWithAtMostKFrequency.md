# Length of Longest Subarray With at Most K Frequency

## Problem Statement

You are given an integer array `nums` and an integer `k`.

The **frequency** of an element `x` is the number of times it occurs in an
array. An array is called **good** if the frequency of **every** element in it
is **at most `k`**.

Return the length of the **longest good subarray** of `nums`.

A subarray is a contiguous, non-empty sequence of elements within an array.

**Example:**
```
nums = [1, 2, 3, 1, 2, 3, 1, 2], k = 2
[1,2,3,1,2,3] has every value exactly twice → good, length 6
Any length-7 window contains some value 3 times
Answer = 6

nums = [1, 2, 1, 2, 1, 2, 1, 2], k = 1
Any window of length 3 repeats a value
Answer = 2

nums = [5, 5, 5, 5, 5, 5, 5], k = 4
At most four 5s allowed
Answer = 4
```

**Constraints:** `1 ≤ nums.length ≤ 10^5`, `1 ≤ nums[i] ≤ 10^9`, `1 ≤ k ≤ nums.length`.

---

## Core Insight: "Good" Is Closed Under Shrinking

If a subarray is good, every subarray **inside** it is also good: removing
elements can only lower frequencies, never raise them.

```
[1,2,3,1,2,3]  good (k=2)  →  [2,3,1,2,3]  still good
[1,2,3,1,2,3,1] bad        →  [1,2,3,1,2,3,1,2] still bad (extending never fixes it)
```

This monotonicity is exactly what a **variable-size sliding window** needs:
when the window turns bad, shrinking from the left is the only way to fix it,
and once fixed we never need to look at the dropped left positions again.

---

## Solutions

### 1. Naive Solution — O(n²) Time, O(n) Space

Fix every start `i`, extend `j` to the right while maintaining a frequency
map, and stop as soon as some frequency exceeds `k`.

```java
@Override
public int maxSubarrayLength(int[] nums, int k) {
    int len = 0;
    for (int i = 0; i < nums.length; i++) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int j = i; j < nums.length; j++) {
            if (freq.merge(nums[j], 1, Integer::sum) > k) {
                break;
            }
            len = Math.max(len, j - i + 1);
        }
    }
    return len;
}
```

The `break` is valid because of the insight above — once `nums[i..j]` is bad,
every longer `nums[i..j']` is bad too.

**Drawback:** When moving from start `i` to `i + 1`, the map is thrown away
and rebuilt from scratch, even though `nums[i+1..j-1]` was already known to
be good. With `n = 10^5` and all-distinct values, that is ~5 · 10^9 steps.

---

### 2. Optimized Solution (Sliding Window) — O(n) Time, O(n) Space

**Key Insight.** Keep a window `[l, r]` that is always good, along with the
frequency of each value inside it.

- **Expand:** add `nums[r]` to the window.
- **Shrink:** if that made `nums[r]`'s frequency exceed `k`, drop elements from
  the left until it is back to `k`.
- **Record:** the window is good again, so update the answer with `r − l + 1`.

Only `nums[r]`'s count went up in this step, so **only `nums[r]` can be over
the limit** — there's no need to scan the whole map to check the window.

```java
public interface LengthOfLongestSubarrayWithAtMostKFrequency {
    int maxSubarrayLength(int[] nums, int k);
}

class LengthOfLongestSubarrayWithAtMostKFrequencyImpl implements LengthOfLongestSubarrayWithAtMostKFrequency {

    private void add(Map<Integer, Integer> map, int num){
        map.put(num, map.getOrDefault(num, 0)+1);
    }

    private void remove(Map<Integer, Integer>map, int num){
        int v = map.get(num);
        if(v == 1){
            map.remove(num);
        }else{
            map.put(num, v-1);
        }
    }

    @Override
    public int maxSubarrayLength(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();

        int len = 0;
        int l = 0;

        for(int r=0; r<nums.length; r++){
            add(freq, nums[r]);

            while(freq.get(nums[r]) > k){
                remove(freq, nums[l]);
                l++;
            }
            len = Math.max(len, r-l+1);
        }

        return len;
    }
}
```

**How it works:**
1. Start with an empty window: `l = 0`, `freq = {}`.
2. For each `r`:
   - Add `nums[r]` to `freq`.
   - While `freq[nums[r]] > k`, remove `nums[l]` from `freq` and advance `l`.
     The loop must stop by the time `l` passes the oldest copy of `nums[r]`
     in the window, since that brings its count down by one.
   - The window `[l, r]` is now good; update `len`.
3. Return `len`.

> **Why update `len` inside the loop, every iteration?** An earlier
> version of this code updated `len` only at the moment the window became
> bad (`len = max(len, r − l)` in an `else` branch). That misses the case
> where the best window runs to the **end** of the array — e.g.
> `nums = [1, 2, 3], k = 1` would return `0`. Recording after every
> expansion avoids that edge case entirely.

---

## Step-by-step Trace

```
nums = [1, 2, 3, 1, 2, 3, 1, 2], k = 2
(window is [l, r], inclusive)

r=0  add 1  freq{1:1}                         l=0  len=1
r=1  add 2  freq{1:1, 2:1}                    l=0  len=2
r=2  add 3  freq{1:1, 2:1, 3:1}               l=0  len=3
r=3  add 1  freq{1:2, 2:1, 3:1}               l=0  len=4
r=4  add 2  freq{1:2, 2:2, 3:1}               l=0  len=5
r=5  add 3  freq{1:2, 2:2, 3:2}               l=0  len=6
r=6  add 1  freq[1]=3 > 2 → shrink:
       remove nums[0]=1  freq[1]=2            l=1  window [2,3,1,2,3,1]  len=6
r=7  add 2  freq[2]=3 > 2 → shrink:
       remove nums[1]=2  freq[2]=2            l=2  window [3,1,2,3,1,2]  len=6

Answer: 6 ✓
```

```
nums = [5, 5, 5, 5, 5, 5, 5], k = 4

r=0..3  freq{5:1..4}                          l=0  len=4
r=4     freq[5]=5 → remove nums[0], l=1       window size 4
r=5     freq[5]=5 → remove nums[1], l=2       window size 4
r=6     freq[5]=5 → remove nums[2], l=3       window size 4

Answer: 4 ✓
```

---

## Correctness Proof

For each right end `r`, let `L(r)` be the **smallest** `l` such that
`nums[l..r]` is good. (It always exists: `nums[r..r]` has one element with
frequency `1 ≤ k`.)

### Lemma 1 (Good starts form a suffix)

For fixed `r`, `nums[l..r]` is good **iff** `l ≥ L(r)`.

*Proof.* `nums[l..r]` for `l ≥ L(r)` is a subarray of the good
`nums[L(r)..r]`, so it is good. For `l < L(r)` it is bad by minimality. ∎

So the longest good subarray ending at `r` has length `r − L(r) + 1`, and
the answer is `maxᵣ (r − L(r) + 1)`.

### Lemma 2 (`L(r)` is non-decreasing)

`L(r) ≤ L(r + 1)`.

*Proof.* `nums[L(r+1)..r]` is a subarray of the good `nums[L(r+1)..r+1]`,
so it is good, hence `L(r) ≤ L(r+1)`. ∎

### Theorem

**Invariant.** After processing `r`, `l = L(r)` and `freq` holds the exact
counts of `nums[l..r]`.

- **Before shrinking.** By the invariant for `r − 1` and Lemma 2,
  `l = L(r−1) ≤ L(r)`. So we never need to move `l` *left*.
- **Shrinking.** `nums[l..r−1]` was good, and adding `nums[r]` raised only
  `freq[nums[r]]`. So the window is bad **iff** `freq[nums[r]] > k` — the
  loop condition checks exactly the right thing. While the window is bad,
  `l < L(r)` by Lemma 1, so advancing `l` never skips past `L(r)`.
- **After shrinking.** The loop exits when the window is good, i.e.
  `l ≥ L(r)`. Combined with `l ≤ L(r)`, we get `l = L(r)`.

So at every `r` the code records `r − L(r) + 1`, and `len` ends as the
maximum over all `r`. ∎

---

## Optimization Journey

```
Naive  ─────────────────────────────────────────────────────────────────────►
       For each start i, extend j with a fresh frequency map; break when bad.
       Rebuilds the map from scratch for every i.
       Time: O(n²)   Space: O(n)

           ▼  Observation: shrinking a good window keeps it good (Lemma 1),
              and the best start for each end only moves right (Lemma 2).
              So one map and two forward-only pointers are enough.

           ▼  Observation: after adding nums[r], only nums[r] can exceed k,
              so checking validity is O(1) instead of scanning the map.

Sliding Window ──────────────────────────────────────────────────────────────►
       Expand r, shrink l while freq[nums[r]] > k, record r − l + 1.
       Each element enters and leaves the map at most once.
       Time: O(n)   Space: O(n)   ← Optimal
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(n) | Every start, extend until a frequency exceeds `k` |
| Sliding Window | **O(n)** | **O(n)** | Shrink only while `freq[nums[r]] > k`; both pointers move forward |

> The inner `while` does not make this O(n²): `l` is never reset, so across
> the whole run it advances at most `n` times in total. Space is O(n) because
> `nums[i]` goes up to `10^9`, so a `HashMap` is needed instead of a
> fixed-size array; in the worst case it holds every distinct value.
