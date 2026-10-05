# Climbing Stairs

## Problem Statement

You are climbing a staircase with `n` steps. Each move you can climb either `1` step or `2` steps. Return the number of **distinct ways** to reach the top.

**Simplified example (walk through this by hand):**
```
n = 3

Ways to reach step 3:
1 + 1 + 1
1 + 2
2 + 1

→ 3 distinct ways
```
Smaller still, `n = 2`: the only ways are `1+1` and `2`, so the answer is `2`. These two tiny cases (`n = 2 → 2`, `n = 3 → 3`) are exactly what the unit tests check, and they are enough to see the Fibonacci pattern: `ways(n) = ways(n-1) + ways(n-2)`.

---

## Solutions

### 1. Naive Solution (Plain Recursion) — O(2ⁿ) Time, O(n) Space

**Key Insight:** To reach step `n`, the last move was either a `1`-step from `n-1` or a `2`-step from `n-2`. So `ways(n) = ways(n-1) + ways(n-2)`.

```java
class NaiveSolution implements ClimbingStairs {
    private long waysTo(int n) {
        if (n == 0 || n == 1) return 1;
        return waysTo(n - 1) + waysTo(n - 2);
    }

    @Override
    public int climbStairs(int n) {
        return Math.toIntExact(waysTo(n));
    }
}
```

**Drawback:** `waysTo(n)` recomputes `waysTo(k)` for every `k < n` an exponential number of times (e.g. `waysTo(2)` is computed twice just within `waysTo(4)`). This is the redundant work removed by memoization.

---

### 2. Repository Solution (Top-Down Memoization) — `ClimbingStairsImpl`, O(n) Time, O(n) Space

**Key Insight:** Cache each `waysTo(k)` the first time it is computed, so the exponential recursion tree collapses to one call per distinct `k`.

```java
class ClimbingStairsImpl implements ClimbingStairs {

    Map<Integer, Long> dp = new HashMap<>();

    private long climbStairsHelper(int n) {
        if (n == 0 || n == 1) return 1;
        if (dp.containsKey(n)) {
            return dp.get(n);
        }
        dp.put(n - 1, climbStairsHelper(n - 1));
        dp.put(n - 2, climbStairsHelper(n - 2));

        return dp.get(n - 1) + dp.get(n - 2);
    }

    @Override
    public int climbStairs(int n) {
        return Math.toIntExact(climbStairsHelper(n));
    }
}
```

**Trace for `n = 3` (simplified input):**
```
climbStairsHelper(3)
 ├─ dp.put(2, climbStairsHelper(2))
 │    ├─ dp.put(1, climbStairsHelper(1) = 1)
 │    ├─ dp.put(0, climbStairsHelper(0) = 1)
 │    └─ returns dp[1] + dp[0] = 1 + 1 = 2        → dp = {0:1, 1:1, 2:2}
 ├─ dp.put(1, climbStairsHelper(1))                → cache hit, returns 1 (no recomputation)
 └─ returns dp[2] + dp[1] = 2 + 1 = 3             → matches hand-counted answer above
```

**Complexity:**
Time: O(n) — each `n` from `0` to the input is computed at most once
Space: O(n) — hash map entries + recursion stack depth

---

### 3. Optimized Solution (Bottom-Up Tabulation) — O(n) Time, O(n) Space

**Key Insight:** Build the same table iteratively, from the base cases upward, removing recursion overhead entirely.

```java
class TabulationSolution implements ClimbingStairs {
    @Override
    public int climbStairs(int n) {
        if (n <= 1) return 1;
        long[] dp = new long[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return Math.toIntExact(dp[n]);
    }
}
```

**Complexity:**
Time: O(n)
Space: O(n) — the `dp` array

---

### 4. Most Optimized Solution (Two Rolling Variables) — O(n) Time, O(1) Space

**Key Insight:** `dp[i]` only ever depends on `dp[i-1]` and `dp[i-2]`. Keeping the full array is unnecessary — two variables suffice.

```java
class OptimizedSolution implements ClimbingStairs {
    @Override
    public int climbStairs(int n) {
        if (n <= 1) return 1;
        long prev2 = 1; // dp[i-2]
        long prev1 = 1; // dp[i-1]
        for (int i = 2; i <= n; i++) {
            long curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }
        return Math.toIntExact(prev1);
    }
}
```

**Complexity:**
Time: O(n)
Space: O(1)

---

## Formal Correctness Proof (applies to all DP variants)

**Claim:** For all integers `n >= 0`, `ways(n)` as defined by `ways(0) = ways(1) = 1` and `ways(n) = ways(n-1) + ways(n-2)` for `n >= 2` equals the true number of distinct ways to climb `n` stairs using steps of size `1` or `2`.

### Induction (recursive definition)

**Base cases:**
- `n = 0`: there is exactly one way to be "at the top" of a `0`-step staircase — take no moves. `ways(0) = 1`. ✓
- `n = 1`: exactly one way — a single `1`-step move. `ways(1) = 1`. ✓

**Inductive hypothesis `P(k)`:** for all `0 <= k < n`, `ways(k)` equals the true number of distinct ways to climb `k` stairs.

**Inductive step — prove `P(n)` from `P(n-1)` and `P(n-2)` (strong induction), for `n >= 2`:**

Partition every valid way to climb `n` stairs by its **last move**, which must be either a `1`-step or a `2`-step (these are the only two moves, and some move must be last since `n >= 1`):
- Ways ending in a `1`-step: removing that last move leaves a valid way to climb exactly `n-1` stairs, and conversely any way to climb `n-1` stairs can be extended by a final `1`-step. This is a bijection, so the count of such ways is exactly `ways(n-1)` (by `P(n-1)`, valid since `n-1 < n`).
- Ways ending in a `2`-step: by the same bijection argument, the count is exactly `ways(n-2)` (by `P(n-2)`, valid since `n-2 < n`).

These two sets are disjoint (a way cannot end in both a `1`-step and a `2`-step) and exhaustive (every way ends in one or the other). By the sum rule, the total number of ways to climb `n` stairs is `ways(n-1) + ways(n-2)`, which is exactly the recurrence's output `ways(n)`. This proves `P(n)`. ∎

By strong induction, `P(n)` holds for all `n >= 0`.

### Memoization preserves correctness (loop/recursion invariant for `ClimbingStairsImpl`)

**Invariant `I(n)`:** whenever `dp.containsKey(n)` is true, `dp.get(n)` equals the mathematically correct `ways(n)`.

- **Initialization:** before any call, `dp` is empty, so `I` holds vacuously.
- **Maintenance:** the only writes are `dp.put(n-1, climbStairsHelper(n-1))` and `dp.put(n-2, climbStairsHelper(n-2))`. Each value stored is the direct return of a recursive call that either returns a correct base case (`1` for `n <= 1`), an already-cached correct value (by `I` holding before the call), or a fresh computation `dp.get(n-1) + dp.get(n-2)` which is correct by the induction proof above, given both operands satisfy `I`. So every write preserves `I`.
- **Termination/use:** since `I` holds at every point where a cached value is read, the final returned value `dp.get(n-1) + dp.get(n-2)` (or a base case) is always the true `ways(n)`.

**Termination (decreasing variant):** each recursive call `climbStairsHelper(n)` only calls itself with strictly smaller arguments `n-1` and `n-2`, and the base cases `n ∈ {0, 1}` make no further calls. The argument `n` is a non-negative integer that strictly decreases on every recursive call, so by well-ordering of ℕ the recursion cannot continue indefinitely and must terminate.

### Why the rolling-variable version (`OptimizedSolution`) is correct

**Loop invariant:** before iteration `i` (for `i` from `2` to `n`), `prev2 = ways(i-2)` and `prev1 = ways(i-1)`.
- **Initialization:** before the loop starts (`i = 2`), `prev2 = ways(0) = 1` and `prev1 = ways(1) = 1` by the base cases. ✓
- **Maintenance:** assume the invariant holds before iteration `i`. The loop computes `curr = prev1 + prev2 = ways(i-1) + ways(i-2) = ways(i)` (by the recurrence proved above), then shifts `prev2 ← prev1 = ways(i-1)` and `prev1 ← curr = ways(i)`. So before iteration `i+1`, `prev2 = ways(i-1) = ways((i+1)-2)` and `prev1 = ways(i) = ways((i+1)-1)` — the invariant holds again.
- **Termination:** the loop runs while `i <= n` with `i` incrementing by `1` each time, so it terminates after exactly `n - 1` iterations. At that point the invariant (with `i = n`) gives `prev1 = ways(n)`, which is returned. ∎

---

## Optimization Journey

```
Naive Recursion ────────────────────────────────────────────────────────────►
       Recomputes ways(k) exponentially many times (overlapping subproblems).
       Time: O(2ⁿ)   Space: O(n) recursion stack

           ▼  Observation: ways(k) only depends on k, so cache each result once

Top-Down Memoization (ClimbingStairsImpl) ──────────────────────────────────►
       Each distinct n computed once, cached in a map; recursion stack remains.
       Time: O(n)    Space: O(n)

           ▼  Observation: the recursion can be unrolled bottom-up, no stack needed

Bottom-Up Tabulation ────────────────────────────────────────────────────────►
       Fill dp[0..n] iteratively from the base cases upward.
       Time: O(n)    Space: O(n)

           ▼  Observation: dp[i] only ever needs dp[i-1] and dp[i-2]

Two Rolling Variables (OptimizedSolution) ───────────────────────────────────►
       Keep only the last two values instead of the whole table.
       Time: O(n)    Space: O(1)   ← Best possible
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive Recursion | O(2ⁿ) | O(n) | Exponential blowup from overlapping subproblems |
| Top-Down Memoization (`ClimbingStairsImpl`) | O(n) | O(n) | Repository's current solution |
| Bottom-Up Tabulation | O(n) | O(n) | Iterative, no recursion stack |
| Two Rolling Variables (`OptimizedSolution`) | O(n) | O(1) | Optimal in both dimensions |

> **Best solution:** Two Rolling Variables — linear time and constant space, since each state only ever depends on the previous two values.
