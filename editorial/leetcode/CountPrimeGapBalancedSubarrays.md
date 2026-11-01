# Count Prime-Gap Balanced Subarrays

## Problem Statement

You are given an integer array `nums` and an integer `k`. A subarray is
called **prime-gap balanced** if:

- it contains **at least two** prime numbers, and
- the difference between the **maximum** and **minimum** prime numbers in that
  subarray is **at most `k`**.

Return the number of prime-gap balanced subarrays in `nums`.

A subarray is a contiguous, non-empty sequence of elements within an array.
Non-prime elements are allowed inside the subarray; they are simply ignored
when computing the prime gap.

**Example:**
```
nums = [1, 2, 3], k = 1
Primes: 2 (idx 1), 3 (idx 2)
Valid subarrays: [2,3], [1,2,3]          (gap 3 − 2 = 1 ≤ 1)
Answer = 2

nums = [2, 3, 5, 7], k = 3
Valid subarrays: [2,3] (gap 1), [2,3,5] (gap 3), [3,5] (gap 2), [5,7] (gap 2)
Invalid: [3,5,7] (gap 4), [2,3,5,7] (gap 5), every single element (< 2 primes)
Answer = 4
```

**Constraints:** `1 ≤ nums.length ≤ 5 · 10^4`, `1 ≤ nums[i] ≤ 5 · 10^4`,
`0 ≤ k ≤ 5 · 10^4`.

The problem has two independent conditions, and they pull in **opposite
directions** as a subarray grows to the left:

| Condition | Extending the start leftwards… |
|-----------|-------------------------------|
| "at least two primes" | can only **help** (more primes) |
| "prime gap ≤ k" | can only **hurt** (max can grow, min can shrink) |

So for a fixed right end `r`, the valid starts form a **contiguous range**
`[L, S]` — bounded on the left by the gap condition and on the right by the
"two primes" condition. The whole solution is about tracking those two
boundaries as `r` moves forward.

---

## Solution — Sieve + Sliding Window with Monotonic Deques

**Key Insight.** Fix the right end `r`.

- **Right boundary `S`.** A start `s` contains at least two primes in
  `nums[s..r]` iff `s ≤ secondLastPrime`, the index of the second-most-recent
  prime at or before `r`.
- **Left boundary `L`.** The prime gap of `nums[s..r]` is non-increasing as `s`
  moves right (we only drop primes). So there is a smallest start `L(r)` with
  gap `≤ k`, and every `s ≥ L(r)` also satisfies it.

The count for this `r` is therefore `max(0, secondLastPrime − L(r) + 1)`.

Moreover, `L(r)` never moves left as `r` grows (adding an element can only
widen the gap), so `L` is a classic sliding-window left pointer. To read the
current max/min prime of the window in `O(1)`, keep two **monotonic deques**
of prime indices:

- `maxDeque` — values decreasing front-to-back; front is the window's max prime.
- `minDeque` — values increasing front-to-back; front is the window's min prime.

Primality is precomputed once with a **Sieve of Eratosthenes** up to `5 · 10^4`.

```java
public interface CountPrimeGapBalancedSubarrays {
    int primeSubarray(int[] nums, int k);
}

class CountPrimeGapBalancedSubarraysImpl implements CountPrimeGapBalancedSubarrays {

    private void primeSieve(boolean[] isPrime){
        Arrays.fill(isPrime, true);

        int n = isPrime.length-1;
        isPrime[0] = isPrime[1] = false;

        for(int i=2; i<=n; i++){
            if(isPrime[i]){
                for(long j = (long) i *i; j<=n; j+=i){
                    isPrime[Math.toIntExact(j)] = false;
                }
            }
        }
    }

    private void add(Deque<Integer> maxDeque,
                    Deque<Integer> minDeque,
                    int[] nums, int i){

        while(!maxDeque.isEmpty() && nums[maxDeque.getLast()] < nums[i]){
            maxDeque.removeLast();
        }
        maxDeque.addLast(i);

        while(!minDeque.isEmpty() && nums[minDeque.getLast()] > nums[i]){
            minDeque.removeLast();
        }
        minDeque.addLast(i);
    }

    private void remove(Deque<Integer> maxDeque, Deque<Integer> minDeque, int l){
        while(!maxDeque.isEmpty() && maxDeque.getFirst() <= l){
            maxDeque.removeFirst();
        }
        while(!minDeque.isEmpty() && minDeque.getFirst() <= l){
            minDeque.removeFirst();
        }
    }

    private int primeDiff(Deque<Integer> maxDeque, Deque<Integer> minDeque, int[] nums){
        if(maxDeque.isEmpty() || minDeque.isEmpty()) return 0;
        return nums[maxDeque.getFirst()] - nums[minDeque.getFirst()];
    }

    @Override
    public int primeSubarray(int[] nums, int k) {
        boolean[] isPrime = new boolean[50_001];
        primeSieve(isPrime);

        Deque<Integer> maxDeque = new ArrayDeque<>();
        Deque<Integer> minDeque = new ArrayDeque<>();
        int cnt = 0;
        int l = 0;
        int lastPrime = -1, secondLastPrime = -1;

        for(int r=0; r<nums.length; r++){
            if(isPrime[nums[r]]){
                secondLastPrime = lastPrime;
                lastPrime = r;
                add(maxDeque, minDeque, nums, r);

                // shrink until the prime gap in [l, r] is within k
                while(primeDiff(maxDeque, minDeque, nums) > k){
                    remove(maxDeque, minDeque, l);
                    l++;
                }
            }

            // any start in [l, secondLastPrime] gives >= 2 primes with gap <= k
            if(secondLastPrime >= l){
                cnt += secondLastPrime - l + 1;
            }
        }
        return cnt;
    }
}
```

**How it works:**
1. **Sieve.** Mark every number in `[0, 50000]` as prime / not prime in
   `O(M log log M)`. Starting the inner loop at `i * i` (as a `long`, to avoid
   overflow) is the standard optimisation.
2. **Scan right ends.** For each `r`:
   - **Non-prime `nums[r]`:** nothing about the window's primes changes, so
     `l`, the deques, and `secondLastPrime` stay as they were. The same starts
     that were valid for `r − 1` are valid for `r` (just with one more
     non-prime on the right), so the same amount is added again.
   - **Prime `nums[r]`:**
     - Shift the prime history: `secondLastPrime = lastPrime`, `lastPrime = r`.
     - **Add** `r` to both deques, popping from the back any index that can
       never again be the max (resp. min) because `r` is both newer and at
       least as large (resp. small).
     - **Shrink:** while `max − min > k`, advance `l`, discarding any deque
       front whose index has fallen out of the window. This stops at the
       latest when only one prime remains (gap `0 ≤ k`).
3. **Count.** Valid starts are exactly `l..secondLastPrime`, contributing
   `secondLastPrime − l + 1` when that range is non-empty.

> **Why is there no shrink for non-primes?** The gap only depends on the
> primes inside the window, and a non-prime on the right adds none. `l` is
> already minimal for the current set of primes, so it is still minimal.

> **Why `int` is enough.** With `n = 5 · 10^4`, the total number of subarrays
> is at most `n(n+1)/2 ≈ 1.25 · 10^9 < 2^31 − 1`.

---

## Step-by-step Trace

```
nums = [2, 3, 5, 7], k = 3
(deques show indices; values in parentheses)

r=0: 2 prime → last=0, second=-1
     max=[0(2)]  min=[0(2)]  gap=0  l=0
     second < l → += 0                                      total=0

r=1: 3 prime → last=1, second=0
     max=[1(3)]  min=[0(2),1(3)]  gap=3-2=1 ≤ 3  l=0
     starts 0..0 → += 1                                     total=1  ([2,3])

r=2: 5 prime → last=2, second=1
     max=[2(5)]  min=[0(2),1(3),2(5)]  gap=5-2=3 ≤ 3  l=0
     starts 0..1 → += 2                                     total=3  ([2,3,5], [3,5])

r=3: 7 prime → last=3, second=2
     max=[3(7)]  min=[0(2),1(3),2(5),3(7)]  gap=7-2=5 > 3
       remove l=0 → min=[1(3),2(5),3(7)]  l=1  gap=7-3=4 > 3
       remove l=1 → min=[2(5),3(7)]       l=2  gap=7-5=2 ≤ 3
     starts 2..2 → += 1                                     total=4  ([5,7])

Answer: 4 ✓
```

```
nums = [1, 2, 3], k = 1

r=0: 1 not prime, second=-1 → += 0                          total=0
r=1: 2 prime → last=1, second=-1
     max=[1(2)]  min=[1(2)]  gap=0  → second < l → += 0     total=0
r=2: 3 prime → last=2, second=1
     max=[2(3)]  min=[1(2),2(3)]  gap=1 ≤ 1  l=0
     starts 0..1 → += 2                                     total=2  ([1,2,3], [2,3])

Answer: 2 ✓
```

---

## Correctness Proof

For a right end `r` and a start `s ≤ r`, let `P(s, r)` be the set of primes in
`nums[s..r]`, and define

- `gap(s, r) = max P(s, r) − min P(s, r)` (or `0` if `|P(s, r)| ≤ 1`),
- `S(r)` = the second-largest index `≤ r` holding a prime (or `−1`),
- `L(r)` = the smallest `s` with `gap(s, r) ≤ k`.

### Lemma 1 (Valid starts form the range `[L(r), S(r)]`)

`nums[s..r]` is prime-gap balanced **iff** `L(r) ≤ s ≤ S(r)`.

*Proof.* `nums[s..r]` contains at least two primes iff it contains the two most
recent primes at or before `r`, i.e. iff `s ≤ S(r)`. For the gap: if `s < s'`
then `P(s', r) ⊆ P(s, r)`, so `gap(s', r) ≤ gap(s, r)`. Hence `gap(s, r) ≤ k`
holds exactly for `s ≥ L(r)`. Both conditions together give the range. ∎

So the answer is `Σᵣ max(0, S(r) − L(r) + 1)`.

### Lemma 2 (`L(r)` is non-decreasing)

`L(r) ≤ L(r + 1)`.

*Proof.* `P(s, r) ⊆ P(s, r + 1)`, so `gap(s, r) ≤ gap(s, r + 1)`. If
`gap(L(r+1), r+1) ≤ k` then `gap(L(r+1), r) ≤ k`, so `L(r) ≤ L(r + 1)`. ∎

### Lemma 3 (Deques report the window's extreme primes)

At all times, the fronts of `maxDeque` / `minDeque` are the indices of the
max / min prime in `nums[l..r]`.

*Proof.* Every prime index in `[l, r]` is pushed when reached. An index `j` is
popped from the back of `maxDeque` only when a later index `i` with
`nums[i] ≥ nums[j]` arrives; `i` stays in the window at least as long as `j`,
so `j` can never again be the unique max. Indices are popped from the front
only once they are `< l` (the `remove` call for `l` runs right before `l++`).
So the deque holds, in index order, a decreasing chain whose head is the
window's maximum. The argument for `minDeque` is symmetric. ∎

### Theorem

**Invariant.** After processing `r`, `l = L(r)` and `secondLastPrime = S(r)`.

- **`secondLastPrime`.** It is shifted exactly when a prime is seen, so it
  always holds the second-most-recent prime index (or `−1`).
- **Non-prime `nums[r]`.** `P(s, r) = P(s, r − 1)` for every `s`, so
  `L(r) = L(r − 1) = l`, and the code correctly leaves `l` unchanged.
- **Prime `nums[r]`.** By Lemma 2, `L(r) ≥ L(r − 1) = l`, so it is safe to
  only move `l` forward. By Lemma 3, `primeDiff` equals `gap(l, r)`. The loop
  advances `l` while `gap(l, r) > k` and stops at the first `l` with
  `gap(l, r) ≤ k`, which is `L(r)` by definition. It terminates because once
  `l = r` the window has one prime and gap `0 ≤ k`.

With the invariant, the code adds `S(r) − L(r) + 1` when positive, matching
Lemma 1 for every `r`. ∎

---

## Alternative — Brute Force

For each start `s`, extend `e` to the right while tracking the running min/max
prime and the prime count; count the subarray when `count ≥ 2` and
`max − min ≤ k`, and `break` once `max − min > k` (it can only get worse).
This is `O(n²)` in the worst case (e.g. all primes equal) — about `2.5 · 10^9`
steps at `n = 5 · 10^4`, too slow. The sliding window removes the restart for
each `s` by exploiting Lemma 2.

---

## Complexity Summary

Let `n = nums.length` and `M = 5 · 10^4` (the value bound).

| Step | Time | Space |
|------|------|-------|
| Sieve of Eratosthenes | O(M log log M) | O(M) |
| Sliding window (`r` and `l` each move forward at most `n` times) | O(n) | O(1) |
| Deque pushes / pops (each index pushed and popped at most once per deque) | O(n) amortised | O(n) |
| **Overall** | **O(M log log M + n)** | **O(M + n)** |

> The nested `while` loops do not make this `O(n²)`: `l` is never reset, and
> every index enters and leaves each deque at most once over the whole run.
