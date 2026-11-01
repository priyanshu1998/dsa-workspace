# Minimum Operations to Reduce X to Zero

## Problem Statement

You are given an integer array `nums` and an integer `x`. In one operation,
you can either remove the **leftmost** or the **rightmost** element from
`nums` and subtract its value from `x`. Note that this **modifies** the array
for subsequent operations.

Return the **minimum number of operations** to reduce `x` to exactly `0`, or
`-1` if it's not possible.

**Example:**
```
nums = [1,1,4,2,3], x = 5
Take the last two: 3 + 2 = 5 → 2 operations
Answer = 2

nums = [5,6,7,8,9], x = 4
No prefix+suffix sums to 4
Answer = -1

nums = [3,2,20,1,1,3], x = 10
Take 3,2 from the left and 1,1,3 from the right: 3+2+1+1+3 = 10 → 5 operations
Answer = 5
```

**Constraints:** `1 ≤ nums.length ≤ 10^5`, `1 ≤ nums[i] ≤ 10^4`,
`1 ≤ x ≤ 10^9`.

---

## Core Insight: Flip "Remove the Ends" into "Keep the Middle"

Removing a prefix and a suffix that together sum to `x` is equivalent to
**keeping a contiguous middle subarray** whose sum is `total - x`, where
`total = sum(nums)`. The number of operations removed is `nums.length` minus
the length of that middle subarray.

```
total = sum(nums)
k = total - x

minimize (prefix length + suffix length)
        = minimize (nums.length - length of the kept middle subarray)
        = maximize (length of a contiguous subarray summing to k)
```

If `k < 0`, no subarray (all elements are positive) can sum to it, so the
answer is `-1` immediately. Otherwise this becomes **"find the longest
subarray with sum exactly k"**, a classic sliding-window problem because all
`nums[i] > 0` — the prefix-sum sequence is strictly increasing, so the window
sum moves monotonically as both pointers move forward.

---

## Solutions

### 1. Naive Solution: O(n²) Time, O(1) Space

Try every pair of prefix length `i` and suffix length `j`, check if
`prefixSum[i] + suffixSum[j] == x`, keep the one minimizing `i + j`.

```java
public int minOperations(int[] nums, int x) {
    int n = nums.length;
    long[] prefix = new long[n + 1];
    for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];

    int ans = n + 1;
    for (int i = 0; i <= n; i++) {          // take first i elements
        long need = x - prefix[i];
        if (need < 0) break;                // prefix only grows, nothing smaller works later
        for (int j = 0; i + j <= n; j++) {   // take last j elements
            long suffixSum = prefix[n] - prefix[n - j];
            if (suffixSum == need) {
                ans = Math.min(ans, i + j);
                break;                       // suffixSum grows with j, no shorter j works
            }
            if (suffixSum > need) break;
        }
    }
    return ans == n + 1 ? -1 : ans;
}
```

**Drawback:** Even with the early breaks, this still explores O(n) suffix
lengths for O(n) prefix lengths in the worst case — O(n²) overall. It doesn't
exploit that the "kept middle" view turns this into a single sliding window.

---

### 2. Optimized Solution (Sliding Window): O(n) Time, O(1) Space

**Key Insight.** Reframe as: find the **longest** subarray `nums[l..r]` with
sum exactly `k = total - x`. Because every `nums[i] > 0`, growing the window
only increases its sum and shrinking only decreases it, so a classic
expand/shrink two-pointer window applies directly (the same template as
[Minimum Size Subarray Sum](MinimumSizeSubarraySum.md), just maximizing
length instead of minimizing it).

```java
public interface MinimumOperationsToReduceXToZero {
    int minOperations(int[] nums, int x);
}

class MinimumOperationsToReduceXToZeroImpl implements MinimumOperationsToReduceXToZero {

    private long total(int[] nums){
        long tot = 0;
        for(int num:nums){
            tot += num;
        }
        return tot;
    }

    @Override
    public int minOperations(int[] nums, int x) {
        int k = Math.toIntExact(total(nums) - x);

        int l = 0;
        int r = 0;
        int ans = nums.length+1;
        long windowSum = 0;

        while(l < nums.length){
            // add leading element if updated windowSum is less than k else remove trailing element
            if(r < nums.length && windowSum + nums[r] <= k){
                windowSum += nums[r];
                r++;
            }else{
                if(windowSum == k){
                    ans = Math.min(ans, nums.length - (r-l));
                }
                windowSum -= nums[l];
                l++;
            }
        }

        return ans == nums.length+1 ? -1 : ans;
    }
}
```

**How it works:**
1. Compute `k = total(nums) - x`, the target sum for the kept middle
   subarray. If `k < 0`, every later `windowSum == k` check simply never
   matches (see edge case below), so `-1` is returned correctly. `total - x`
   is computed as a `long` before the cast to `int`, which is safe because
   `total ≤ 10^5 · 10^4 = 10^9` and `x ≤ 10^9` keep the difference within
   `int` range.
2. Maintain a window `[l, r)` with running sum `windowSum`:
   - **Expand** (`r` advances) whenever adding `nums[r]` keeps `windowSum ≤ k`.
   - **Otherwise shrink**: first check whether the *current* window already
     equals `k` (record the answer), then remove `nums[l]` and advance `l`.
3. Because `nums[i] > 0`, `windowSum` is monotonic in both `l` and `r`, so no
   window is ever re-examined — each index enters and leaves the window at
   most once.
4. The number of operations for a kept window of length `r - l` is
   `nums.length - (r - l)`; `ans` tracks the minimum of these, i.e. the
   **longest** valid window.
5. Loop until `l` reaches `nums.length` (every window position considered);
   return `-1` if no valid window was ever found.

> **Edge case — `k < 0`:** `windowSum` starts at `0` and only ever increases
> while expanding or decreases one `nums[l]` at a time while shrinking, so it
> never goes negative; it can therefore never equal a negative `k`. The loop
> still runs safely to completion (every iteration takes the shrink branch,
> since `windowSum + nums[r] ≤ k` is false for `k < 0`) and correctly returns
> `-1` — no special-case check is needed before the loop.

---

## Step-by-step Trace

```
nums = [1,1,4,2,3], x = 5
total = 11, k = total - x = 6   (find longest subarray summing to 6)

l=0 r=0 windowSum=0
  0+1=1 ≤ 6 → expand: windowSum=1, r=1
  1+1=2 ≤ 6 → expand: windowSum=2, r=2
  2+4=6 ≤ 6 → expand: windowSum=6, r=3        [l,r)=[0,3) sum=6
  6+2=8 > 6 → shrink: windowSum==6 → ans=min(6,5-3)=2
              windowSum-=nums[0]=1 → windowSum=5, l=1
  5+2=7 > 6 → shrink: windowSum==5≠6, no record
              windowSum-=nums[1]=1 → windowSum=4, l=2
  4+2=6 ≤ 6 → expand: windowSum=6, r=4         [l,r)=[2,4) sum=6
  6+3=9 > 6 → shrink: windowSum==6 → ans=min(2,5-2)=2
              windowSum-=nums[2]=4 → windowSum=2, l=3
  2+3=5 ≤ 6 → expand: windowSum=5, r=5
  r==5 (end) → shrink: windowSum==5≠6, no record
              windowSum-=nums[3]=2 → windowSum=3, l=4
  r==5 → shrink: windowSum==3≠6
              windowSum-=nums[4]=3 → windowSum=0, l=5
loop ends (l==5)

Answer: ans = 2 ✓
```

```
nums = [5,6,7,8,9], x = 4
total = 35, k = 31   (find longest subarray summing to 31)

l=0 r=0 windowSum=0
  0+5=5  ≤ 31 → expand: windowSum=5,  r=1
  5+6=11 ≤ 31 → expand: windowSum=11, r=2
  11+7=18≤ 31 → expand: windowSum=18, r=3
  18+8=26≤ 31 → expand: windowSum=26, r=4
  26+9=35> 31 → shrink: windowSum==26≠31
              windowSum-=nums[0]=5 → windowSum=21, l=1
  21+9=30≤ 31 → expand: windowSum=30, r=5
  r==5 (end) → shrink: windowSum==30≠31
              windowSum-=nums[1]=6 → windowSum=24, l=2
  r==5 → shrink: windowSum==24≠31
              windowSum-=nums[2]=7 → windowSum=17, l=3
  r==5 → shrink: windowSum==17≠31
              windowSum-=nums[3]=8 → windowSum=9, l=4
  r==5 → shrink: windowSum==9≠31
              windowSum-=nums[4]=9 → windowSum=0, l=5
loop ends (l==5)

windowSum never equals 31, so ans stays nums.length+1 = 6.

Answer: -1 ✓
```

```
nums = [3,2,20,1,1,3], x = 10
total = 30, k = 20   (find longest subarray summing to 20)

l=0 r=0 windowSum=0
  0+3=3 ≤ 20 → expand: windowSum=3, r=1
  3+2=5 ≤ 20 → expand: windowSum=5, r=2
  5+20=25 > 20 → shrink: windowSum==5≠20
              windowSum-=nums[0]=3 → windowSum=2, l=1
  2+20=22 > 20 → shrink: windowSum==2≠20
              windowSum-=nums[1]=2 → windowSum=0, l=2
  0+20=20 ≤ 20 → expand: windowSum=20, r=3       [l,r)=[2,3) sum=20
  20+1=21 > 20 → shrink: windowSum==20 → ans=min(7,6-1)=5
              windowSum-=nums[2]=20 → windowSum=0, l=3
  0+1=1 ≤ 20 → expand: windowSum=1, r=4
  1+1=2 ≤ 20 → expand: windowSum=2, r=5
  2+3=5 ≤ 20 → expand: windowSum=5, r=6
  r==6 (end) → shrink: windowSum==5≠20
              windowSum-=nums[3]=1 → windowSum=4, l=4
  r==6 → shrink: windowSum==4≠20
              windowSum-=nums[4]=1 → windowSum=3, l=5
  r==6 → shrink: windowSum==3≠20
              windowSum-=nums[5]=3 → windowSum=0, l=6
loop ends (l==6)

Answer: ans = 5 ✓  (the lone element 20, kept as the middle, removing the
                    other 5 elements 3,2,1,1,3)
```

---

## Correctness Proof

Let `total = sum(nums)` and `k = total - x`. Define `S(l, r) = sum(nums[l..r))`
for `0 ≤ l ≤ r ≤ n`.

### Lemma 1 (Equivalence of the two framings)

Removing a prefix of length `p` and a suffix of length `s` reduces `x` to `0`
**iff** the kept middle subarray `nums[p .. n-s)` has sum exactly `k`.

*Proof.* The operation succeeds iff `prefixSum(p) + suffixSum(s) = x`. Since
`prefixSum(p) + S(p, n-s) + suffixSum(s) = total`, this is equivalent to
`S(p, n-s) = total - x = k`. ∎

By Lemma 1, minimizing `p + s` is the same as maximizing the length of a
contiguous subarray with sum `k`, since `p + s = n - (\text{length of middle})`.

### Lemma 2 (Monotonicity of `windowSum`)

Since every `nums[i] > 0`: for fixed `l`, `S(l, r)` strictly increases as `r`
increases; for fixed `r`, `S(l, r)` strictly increases as `l` decreases.

*Proof.* Immediate from summing positive terms. ∎

### Theorem (Two-pointer window never misses a valid subarray)

**Invariant.** At the start of each outer-loop iteration, `windowSum = S(l, r)`,
and for the current `l`, every `r' < r` with `S(l, r') = k` has already been
considered this round (trivially true, since the window only grows `r`
forward from its last position for this `l`).

- **Expansion step.** If `windowSum + nums[r] ≤ k`, advancing `r` keeps
  `windowSum ≤ k` (by Lemma 2 this is the only way `windowSum` can still
  reach `k` without first overshooting, since it increases monotonically in
  `r`).
- **Shrink step.** If expansion would overshoot `k` (or `r` is exhausted), the
  current window is first checked: if `windowSum == k`, it is exactly the
  longest valid window achievable with this `l` as the left end (by Lemma 2,
  no larger `r` keeps the sum at `k`, since `windowSum` only increases past
  this point). The window is then shrunk by removing `nums[l]`, which by
  Lemma 2 strictly decreases `windowSum`, restoring the possibility of
  reaching `k` again with a larger `l`.
- Every `l` from `0` to `n` is tried exactly once (the outer `while` advances
  `l` monotonically, never resetting it), and for each `l`, `r` only moves
  forward, so **every** pair `(l, r)` with `l ≤ r` is examined at most once in
  total across the whole run — in particular, the maximizing pair
  `(l*, r*)` with `S(l*, r*) = k` and `r* - l*` maximal is examined, and its
  `windowSum == k` check fires.

By Lemma 1, `ans = n - (r* - l*)` after this check is exactly the minimum
number of operations, or `n + 1` (→ `-1`) if no window ever sums to `k`. ∎

### Termination

Each iteration either advances `r` or advances `l`, both bounded by `n`, and
neither ever decreases, so the loop runs at most `2n + 1` iterations before
`l` reaches `nums.length` and the loop exits. `f(state) = (n - l) + (n - r)`
is a non-negative integer that strictly decreases every iteration, giving a
well-founded termination measure.

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────────────►
       For every prefix length i, scan suffix lengths j until the sums match
       or overshoot.
       Time: O(n²) worst case   Space: O(n) for prefix sums

           ▼  Observation (Lemma 1): removing a prefix+suffix summing to x is
              the same as keeping a middle subarray summing to total − x.

           ▼  Observation (Lemma 2): since all nums[i] > 0, the window sum is
              monotonic in both pointers, so a single forward-only two-pointer
              scan finds the longest such subarray without backtracking.

Sliding Window ──────────────────────────────────────────────────────────────►
       k = total − x; expand r while windowSum + nums[r] ≤ k, else record a
       match and shrink from l.
       Time: O(n)   Space: O(1)   ← Optimal
```

---

## Complexity Summary

| Solution | Time | Space | Key Idea |
|----------|------|-------|----------|
| Naive | O(n²) | O(n) | Try every prefix/suffix length pair via prefix sums |
| Sliding Window | **O(n)** | **O(1)** | Maximize the length of a subarray summing to `total − x`; both pointers move forward only |

> The positivity of `nums[i]` is essential: it is what makes `windowSum`
> monotonic, which is what lets both pointers advance without ever
> backtracking, giving the O(n) bound.
