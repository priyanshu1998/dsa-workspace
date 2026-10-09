# Minimized Maximum of Products Distributed to Any Store

## Problem Statement

There are `n` stores and `m` product types, given as `quantities[i]` — the
number of products of the `i`-th type. Each type must be split among the
stores (a store may receive products of multiple types, and a type may be
split across multiple stores), but **a single store cannot receive products
of more than one type at once from its "slot"** — concretely, type `i`'s
`quantities[i]` units must be partitioned into some number of **contiguous
store-allocations**, each ≤ some common cap `x`, and the total number of
stores used across *all* types must be `≤ n`. Return the minimum possible
value of the **maximum number of products given to any single store**.

**Example:**
```
n = 6, quantities = [11, 6]
Answer = 3
  type 0 (11 units) → 4 stores of size 3,3,3,2  (ceil(11/3) = 4 stores)
  type 1 (6 units)  → 2 stores of size 3,3       (ceil(6/3) = 2 stores)
  total stores used = 6 ≤ n, max per store = 3

n = 7, quantities = [15, 10, 10]
Answer = 5

n = 1, quantities = [100000]
Answer = 100000   (only one store exists, it must take everything)
```

This is another **"minimize the maximum"** problem: the quantity
`check(n, quantities, x)` — *"can every type be distributed using at most
`x` products per store, using `n` stores in total?"* — is **monotonic** in
`x` (false for small `x`, true for large `x`). That makes it solvable with
**binary search on the answer**.

---

## Solution — Binary Search on the Answer + Greedy Store-Count Check

**Key Insight.** For a fixed cap `x`, the minimum number of stores needed to
distribute a single type's `quantity` units — each store receiving at most
`x` units — is exactly `ceil(quantity / x)`, because splitting into equal
(or near-equal) chunks of size `≤ x` is always optimal and achievable for
any partition of a quantity into stores of capacity `≤ x` (any such
partition uses at least `ceil(quantity / x)` stores, and chunks of size `x`,
`x`, …, `x`, remainder achieve that bound exactly).

So `check(n, quantities, x) = (Σᵢ ceil(quantities[i] / x)) ≤ n` decides
feasibility of cap `x` directly, with no greedy simulation needed — it is an
exact arithmetic formula, unlike `SplitArrayLargestSum`'s scan-based check.

```java
public interface MinimizedMaximumOfProductsDistributedToAnyStore {
    int minimizedMaximum(int n, int[] quantities);
}

class MinimizedMaximumOfProductsDistributedToAnyStoreImpl
        implements MinimizedMaximumOfProductsDistributedToAnyStore {

    private int max(int[] quantities) {
        var M = quantities[0];
        for (var quantity : quantities) {
            M = Math.max(M, quantity);
        }
        return M;
    }

    private boolean check(int n, int[] quantities, int x) {
        int count = 0;
        for (int quantity : quantities) {
            count += (quantity + x - 1) / x;   // ceil(quantity / x)
        }
        return count <= n;
    }

    @Override
    public int minimizedMaximum(int n, int[] quantities) {
        int l = 0;
        int r = max(quantities);

        while (l < r) {
            int mid = l + (r - l) / 2;
            if (check(n, quantities, mid)) {
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
1. `l = 0` (an impossible cap, serves only as an open lower bracket) and
   `r = max(quantities)` bracket the true answer — `r` is always feasible
   because giving one store the entire largest type (and as many stores as
   needed for the rest, capped by `x = max(quantities)`) never needs more
   than `n` stores in the worst realistic case considered below.
2. Each iteration tests midpoint `mid` as a candidate cap `x`:
   - If `check(n, quantities, mid)` is `true` (total stores needed `≤ n`),
     `mid` is feasible, so the answer is `≤ mid`; shrink `r = mid`.
   - Otherwise `mid` is too small — shrink is impossible within budget `n`
     — so the answer must be `> mid`; raise `l = mid + 1`.
3. The loop narrows `[l, r]` until `l == r`, the smallest feasible cap.

---

## Correctness Proof

### Lemma 1 (`ceil(quantity / x)` is the exact minimum store count for one type)

For a fixed cap `x ≥ 1` and a single type with `quantity` units, the minimum
number of stores needed to distribute all `quantity` units, each store
receiving `≤ x` units, is exactly `⌈quantity / x⌉`.

*Proof.*
- *Lower bound:* with `s` stores each holding `≤ x` units, the total held is
  `≤ s·x`. To hold all `quantity` units we need `s·x ≥ quantity`, i.e.
  `s ≥ quantity / x`, and since `s` is an integer, `s ≥ ⌈quantity / x⌉`.
- *Achievability:* take `s = ⌈quantity / x⌉` stores and fill the first
  `s − 1` with exactly `x` units each and the last with the remainder
  `quantity − (s − 1)x`. By definition of ceiling, `0 < quantity − (s−1)x ≤ x`
  (if the remainder were `0` or negative, `s − 1` stores would already
  suffice, contradicting minimality of `s`; if it exceeded `x`, `s` would be
  too small), so every store's load is in `(0, x]`, a valid distribution.

Hence the minimum is exactly `⌈quantity / x⌉`, achieved by (and only by,
up to permutation/splitting) this near-equal partition. ∎

In the code, `(quantity + x - 1) / x` is the standard integer-arithmetic
formula for `⌈quantity / x⌉` (for positive `quantity`, `x`), so `check`
computes `Σᵢ ⌈quantities[i] / x⌉` exactly.

### Lemma 2 (Summed minimum store counts is the true feasibility test)

Let `m(x) = Σᵢ ⌈quantities[i] / x⌉`. A cap `x` is feasible (some valid
distribution of **all** types uses `≤ n` stores total) if and only if
`m(x) ≤ n`.

*Proof.* Each type's units can be distributed independently of the others
(stores assigned to one type are disjoint from stores assigned to another,
since the problem only bounds the *max per store*, not shared capacity
across types). By Lemma 1, type `i` needs at least `⌈quantities[i]/x⌉`
stores and this minimum is achievable. Summing over independent types: the
overall minimum total store count for cap `x` is `Σᵢ ⌈quantities[i]/x⌉ =
m(x)`, achieved by combining each type's optimal partition. So `x` is
feasible (achievable with `≤ n` stores total) iff `m(x) ≤ n`. Hence
`check(n, quantities, x) = (m(x) ≤ n)` is exactly the feasibility predicate,
not an approximation. ∎

### Lemma 3 (Monotonicity of feasibility)

If `x₁ < x₂` (both positive integers), then `m(x₁) ≥ m(x₂)`; consequently if
`m(x₁) ≤ n` then `m(x₂) ≤ n`.

*Proof.* For each `i`, `⌈quantities[i] / x₁⌉ ≥ ⌈quantities[i] / x₂⌉` because
`⌈·⌉` of a quantity divided by a larger divisor cannot increase (dividing by
a bigger number gives a smaller-or-equal real quotient, and ceiling is
monotonic non-decreasing in its argument). Summing over `i` gives
`m(x₁) ≥ m(x₂)`. If `m(x₁) ≤ n`, then `m(x₂) ≤ m(x₁) ≤ n`. ∎

This makes `check(n, quantities, ·)` a monotonic predicate: false for all
`x` below some threshold `ANSWER`, true for all `x ≥ ANSWER`, where
`ANSWER = min { x ≥ 1 : m(x) ≤ n }` is exactly the value
`minimizedMaximum` must return.

### Lemma 4 (The search bracket contains the answer)

`0 < ANSWER ≤ max(quantities)`, so `l = 0, r = max(quantities)` safely
brackets `ANSWER` (with `l` itself infeasible/unreachable, serving only as
an exclusive floor, consistent with the problem's guarantee `n ≥ m`, i.e.
there are always at least as many stores as product types, so a solution
always exists).

*Proof.*
- *Upper bound:* at `x = max(quantities)`, each type `i` needs
  `⌈quantities[i] / max(quantities)⌉ = 1` store (since
  `0 < quantities[i] ≤ max(quantities)`), so `m(max(quantities))` equals the
  number of distinct types, `m.length`. The problem guarantees
  `n ≥ quantities.length`, so `m(max(quantities)) ≤ n`: `x = max(quantities)`
  is always feasible, giving `ANSWER ≤ max(quantities)`.
- *Lower bound:* for `x = 0`, the formula is undefined (division by zero) —
  no store can hold a non-negative number of units under a zero cap for a
  positive quantity, so `x = 0` is never feasible and `ANSWER ≥ 1 > l = 0`.
  (The loop never evaluates `check` at `x = 0` itself as a final answer
  because `l < r` is required to enter the loop body, and `mid ≥ l + 0`; the
  only risk would be returning `l = 0`, which cannot happen once we show
  `r`'s feasibility invariant below forces convergence to a feasible value
  `≥ 1`.) ∎

### Theorem (Binary search finds `ANSWER`)

**Loop invariant.** Before each iteration of `while (l < r)`,
`l ≤ ANSWER ≤ r` and `check(n, quantities, r)` is true.

- **Initialization.** `l = 0 ≤ ANSWER` (trivially, since `ANSWER ≥ 1 > 0`)
  and `r = max(quantities) ≥ ANSWER` by Lemma 4's upper bound. By Lemma 4's
  proof, `check(n, quantities, max(quantities))` is true, so the invariant
  holds initially.

- **Maintenance.** Assume `l ≤ ANSWER ≤ r` and `check(n, quantities, r)` is
  true, with `l < r`. Let `mid = l + (r - l)/2`, so `l ≤ mid < r`.
  - If `check(n, quantities, mid)` is true, by definition of `ANSWER` as the
    smallest feasible value, `ANSWER ≤ mid`; setting `r = mid` preserves
    `ANSWER ≤ r` and `check` true at the new `r`.
  - If `check(n, quantities, mid)` is false, then by Lemma 3's
    contrapositive, every `x ≤ mid` is also infeasible (monotonicity: if
    some `x₀ ≤ mid` were feasible, `m(x₀) ≤ n`, and since `x₀ ≤ mid` implies
    `m(mid) ≤ m(x₀) ≤ n` by Lemma 3, contradicting infeasibility of `mid`),
    so `ANSWER ≥ mid + 1`; setting `l = mid + 1` preserves `l ≤ ANSWER`. The
    `r` bound and its feasibility are untouched.

  In both branches `l ≤ ANSWER ≤ r` continues to hold.

- **Termination.** `r - l` is a non-negative integer that strictly
  decreases each iteration: if `r = mid`, the new width `mid - l < r - l`
  (since `mid < r`); if `l = mid + 1`, the new width
  `r - (mid+1) < r - l` (since `mid ≥ l`). By well-ordering of the
  non-negative integers, the loop terminates after finitely many steps,
  with `l = r`. Since `l ≤ ANSWER ≤ r` held throughout, `l = r` forces
  `l = r = ANSWER`.

The method returns `r`, which equals `ANSWER`, the minimized maximum number
of products assigned to any single store. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `max` (bracket computation) | O(m) | O(1) |
| `check` (one feasibility test, summing ceilings) | O(m) | O(1) |
| Binary search over `[0, max(quantities)]` | O(log(max(quantities))) checks | — |
| **Overall** | **O(m · log(max(quantities)))** | **O(1)** |

where `m = quantities.length`. Each binary-search step costs `O(m)` for the
arithmetic feasibility sum (no simulation loop needed, unlike problems where
the check requires a greedy scan), and the search interval halves each
round, giving `O(m log(max(quantities)))` overall — far better than trying
every possible cap linearly, `O(m · max(quantities))`.
