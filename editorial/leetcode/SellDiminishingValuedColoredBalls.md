# Sell Diminishing-Valued Colored Balls

## Problem Statement

You have `inventory[i]` balls of the `i`-th color. A customer wants to buy
exactly `orders` balls, one at a time. Each ball sold is priced at the
**current count of balls remaining of that color** *before* the sale, and
after the sale that color's count decreases by `1`. You choose which ball to
sell at every step. Return the **maximum total profit**, modulo
`1_000_000_007`.

**Example:**
```
inventory = [2, 5], orders = 4
Answer = 14
  sell color-1 ball (price 5) -> inventory = [2, 4]
  sell color-1 ball (price 4) -> inventory = [2, 3]
  sell color-1 ball (price 3) -> inventory = [2, 2]
  sell either color  (price 2) -> inventory = [1, 2] or [2, 1]
  total = 5 + 4 + 3 + 2 = 14

inventory = [3, 5], orders = 6
Answer = 19
```

Constraints push toward `1 <= n <= 10^5`, `1 <= inventory[i] <= 10^9`,
`1 <= orders <= 10^9`, and `orders <= sum(inventory)` — far too large for an
`O(orders)` or even `O(orders · log n)` simulation to be the final answer,
but small enough that an `O(n log(max(inventory)))` solution is required.

---

## Solutions

### 1. Naive Solution (Max-Heap Simulation) — O(orders · log n) Time, O(n) Space

Simulate the process literally: repeatedly pop the color with the largest
current count, sell one ball at that price, decrement it, and push it back.

```java
class MaxHeapSolution implements SellDiminishingValuedColoredBalls {
    @Override
    public int maxProfit(int[] inventory, int orders) {
        long M = 1_000_000_007;
        PriorityQueue<Integer> heap = new PriorityQueue<>(Collections.reverseOrder());
        for (int count : inventory) heap.offer(count);

        long profit = 0;
        for (int sold = 0; sold < orders; sold++) {
            int top = heap.poll();
            profit = (profit + top) % M;
            heap.offer(top - 1);
        }
        return (int) profit;
    }
}
```

**Complexity:** Time `O(n + orders · log n)`, Space `O(n)`.

**Drawback:** `orders` can be as large as `10^9`, making a per-sale
heap operation completely infeasible — the simulation must be replaced by a
*closed-form* computation of what the greedy process converges to, without
ever touching individual sales.

---

### 2. Optimized Solution (Binary Search on a Price Threshold + Arithmetic-Series Formula) — O(n log(max(inventory))) Time, O(1) Space

**Key Insight.** Model every ball as a distinct "price token": color `i`
contributes the multiset of prices `{inventory[i], inventory[i]-1, ..., 1}`
(the price it would fetch if sold `1st, 2nd, 3rd, ...` from that color).
Selling always decrements the chosen color by exactly `1`, so the sequence
of prices actually realizable from color `i`, in the order they must be
sold, is forced to be a **prefix of this descending sequence** — you cannot
sell the `k`-th-from-top ball of a color without having already sold the
`(k-1)` balls above it. Maximizing total profit is therefore exactly:
**pick `orders` tokens, respecting each color's forced order, to maximize
their sum** — which (proved formally below) is identical to simply taking
the **largest `orders` tokens** from the combined multiset `S` of all
colors' tokens, since the globally largest tokens always form a valid
per-color prefix automatically.

So the problem reduces to: *find the sum of the top `orders` elements of
`S`*, a classic "`k`-th order statistic over virtual sorted sequences" task,
solved by **binary searching the threshold price `r`** such that the number
of tokens strictly greater than `r` is `<= orders`, then paying for every
token `> r` at its exact price (via a closed-form triangular-number sum) and
filling the remaining orders at the flat price `r`.

```java
class SellDiminishingValuedColoredBallsImpl implements SellDiminishingValuedColoredBalls {

    private int maximum(int[] inventory) {
        int max = inventory[0];
        for (int i = 0; i < inventory.length; i++) {
            max = Math.max(max, inventory[i]);
        }
        return max;
    }

    // count(mid) = number of tokens strictly greater than `mid`
    private boolean check(int[] inventory, int orders, int mid) {
        long count = 0;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] > mid) {
                count += inventory[i] - mid;
            }
        }
        return count <= orders;
    }

    // revenue when every pile is brought down to level `r`,
    // plus the leftover orders sold at the flat price `r`
    private int answer(int[] inventory, int orders, int r) {
        long ret = 0;
        int count = 0;
        int M = 1_000_000_007;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] > r) {
                count += inventory[i] - r;
                int a = Math.toIntExact(((long) inventory[i] * (inventory[i] + 1) / 2) % M);
                int b = Math.toIntExact(((long) r * (r + 1) / 2) % M);
                ret = (Math.toIntExact(ret + a - b) % M + M) % M;
            }
        }
        ret = (ret + ((long) (orders - count) * r) % M) % M;
        return Math.toIntExact(ret);
    }

    @Override
    public int maxProfit(int[] inventory, int orders) {
        int l = 0;
        int r = maximum(inventory);

        while (l < r) {
            int mid = l + (r - l) / 2;
            if (check(inventory, orders, mid)) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }

        return answer(inventory, orders, r);
    }
}
```

**How it works:**
1. `check(inventory, orders, x)` computes `count(x) = Σᵢ max(0, inventory[i] - x)`
   — exactly the number of tokens with price `> x` — and reports whether
   that many tokens is affordable within the `orders` budget.
2. Binary search finds the **smallest threshold `r`** for which
   `count(r) <= orders`: every token `> r` *must* be sold (there's no way to
   reach a smaller threshold without exceeding the order budget), and the
   remaining `orders - count(r)` sales are filled at the flat price `r`
   (proved below to always be available in sufficient quantity).
3. `answer` prices every token `> r` exactly via
   `T(inventory[i]) - T(r)` (triangular-number telescoping, `T(k) = k(k+1)/2`)
   and adds `(orders - count(r)) * r` for the flat-priced remainder, all
   under modulo `10^9 + 7` arithmetic.

---

## Correctness Proof

### Setup

Let `n = inventory.length`. Define the multiset
`S = ⊎ᵢ {1, 2, ..., inventory[i]}` (⊎ = disjoint multiset union), so
`|S| = Σᵢ inventory[i]`. For `x >= 0`, let
`count(x) = |{ s ∈ S : s > x }| = Σᵢ max(0, inventory[i] - x)` — this is
exactly what `check`'s `count` variable computes.

### Lemma 1 (Reduction to "sum of the top `orders` elements of `S`")

The maximum achievable profit equals the sum of the `orders` largest
elements of `S` (ties broken arbitrarily, since `S` is a multiset).

*Proof.* Any selling strategy is a sequence of `orders` token choices, one
per sale, where selling the `k`-th token of color `i` requires the previous
`k - 1` tokens of color `i` (prices `inventory[i], ..., inventory[i]-k+2`)
to have already been sold — i.e., the chosen tokens from color `i` must
form a **prefix** `{inventory[i], inventory[i]-1, ..., inventory[i]-kᵢ+1}`
of that color's descending sequence, for some `kᵢ >= 0` with `Σᵢ kᵢ = orders`.
Profit is the sum of the chosen tokens, `Σᵢ Σ_{j=0}^{kᵢ-1} (inventory[i]-j)`.

Take the `orders` globally largest elements of `S`, call this multiset `T`
(well-defined up to tie-breaking). Because within any single color the
tokens `inventory[i], inventory[i]-1, ..., 1` are already strictly
decreasing, selecting the globally largest elements can never skip a larger
token of a color while keeping a smaller one of the *same* color (that
would contradict `T` being the top `orders` elements) — so `T`'s elements
from color `i` are automatically exactly its top `kᵢ = |T ∩ color i|`
tokens, i.e. a valid prefix. Hence `T` is *achievable* by some strategy.

Conversely, for **any** achievable multiset `T'` of size `orders` (a union
of per-color prefixes), and `T` the true top-`orders` elements of `S`: if
`T' != T` then some element `t' ∈ T' \ T` and some element `t ∈ T \ T'` with
`t > t'` must exist (`T` is the maximal-sum size-`orders` subset of `S` by
construction, so swapping any non-`T` element of `T'` for a missing
larger element of `T` cannot decrease the sum). Replacing `t'` by `t` in
`T'` strictly increases (or preserves, under ties) the sum while remaining
achievable (the prefix structure of a color is unaffected by swapping which
*equal-valued* tokens are counted, and since `t > t'` forces them to belong
to a position that's still consistent with a prefix — formally, the
multiset `T` itself is achievable per the paragraph above, and no size-
`orders` subset of `S` has a strictly larger sum than `T`, since `T` is by
definition the `orders` largest elements). Hence `T` attains the maximum
possible sum over all achievable size-`orders` selections, proving the
optimum profit equals `sum(T)`. ∎

### Lemma 2 (Monotonicity of `count`)

For `x₁ <= x₂`, `count(x₁) >= count(x₂)`; consequently `check` is a
monotonic predicate: false for all `x` below some threshold, true for all
`x` at or above it.

*Proof.* For each `i`, `max(0, inventory[i] - x)` is non-increasing in `x`
(increasing `x` only shrinks or keeps zero this term). Summing over `i`
preserves the (non-strict) monotonic decrease, so `count` is non-increasing
in `x`. Hence `check(·, orders, x) = (count(x) <= orders)` flips from false
to true at most once as `x` increases — i.e. it is monotonic. ∎

### Lemma 3 (The binary search finds the minimal feasible threshold)

Let `ANSWER = min { x >= 0 : count(x) <= orders }`. The binary-search loop
`while (l < r) { ... }` with `l = 0, r = maximum(inventory)` terminates with
`l = r = ANSWER`.

*Proof (loop invariant).* **Invariant:** before each iteration,
`l <= ANSWER <= r` and `check(inventory, orders, r)` is true.

- **Initialization:** `r = maximum(inventory)` gives `count(r) = 0 <= orders`
  (no `inventory[i]` exceeds the max, so every term of the sum is `0`),
  i.e. `check(·, r)` is true, and since `ANSWER` is well-defined and
  `count` is non-increasing with `count(maximum(inventory)) = 0 <= orders`,
  `ANSWER <= r`. Also `l = 0 <= ANSWER` trivially (since `ANSWER >= 0`).
- **Maintenance:** let `mid = l + (r-l)/2`, so `l <= mid < r`.
  - If `check(mid)` is true: `ANSWER <= mid` by minimality of `ANSWER`;
    setting `r = mid` keeps `ANSWER <= r` and `check(r)` true.
  - If `check(mid)` is false: by Lemma 2's contrapositive, `check(x)` is
    false for every `x <= mid` (else monotonicity would force `check(mid)`
    true), so `ANSWER >= mid + 1`; setting `l = mid+1` keeps `l <= ANSWER`.
    `r` and its feasibility are untouched.
- **Termination:** `r - l` strictly decreases each iteration (standard
  midpoint halving), so by well-ordering the loop terminates with `l = r`;
  the invariant `l <= ANSWER <= r` then forces `l = r = ANSWER`. ∎

### Lemma 4 (At threshold `r = ANSWER`, the flat-priced remainder is always available)

Let `r = ANSWER` and `count = count(r)`. Then `0 <= orders - count` and the
number of elements of `S` equal to `r` is at least `orders - count`.

*Proof.* `count(r) <= orders` directly from `r`'s feasibility
(Lemma 3), giving `orders - count >= 0`. For the availability claim: by
minimality of `ANSWER`, `r - 1` is *not* feasible (or `r = 0`, handled
below), i.e. `count(r-1) > orders` when `r >= 1`. Since
`count(r-1) = |{s ∈ S : s > r-1}| = |{s ∈ S : s >= r}| = count(r) + |{s ∈ S : s = r}|`,
we get `count(r) + |{s = r}| > orders`, i.e.
`|{s ∈ S : s = r}| > orders - count(r) = orders - count`, so there are
strictly more than enough (hence "at least") elements equal to `r` to cover
the shortfall `orders - count`. (If `r = 0`, then `count(0) = Σ inventory[i] = |S| >= orders`
by the problem's guarantee `orders <= sum(inventory)`, and `count = count(0) <= orders`
forces `count = orders` whenever `r=0` is the minimal feasible value only if
`count(0) <= orders`; combined with `|S| >= orders` and `count(0)=|S|`, this
means `orders = |S| = count`, so the shortfall `orders - count = 0` needs no
elements equal to `r`, trivially satisfied.) ∎

### Theorem (Overall correctness of `maxProfit`)

`maxProfit(inventory, orders)` returns the maximum profit modulo
`10^9 + 7`.

*Proof.* By Lemma 3, the binary search computes `r = ANSWER`, the minimal
threshold with `count(r) <= orders`. By Lemma 1, the true maximum profit
(before modulo) equals the sum of the top `orders` elements of `S`. We show
`answer(inventory, orders, r)` computes exactly this sum, mod `10^9+7`:

- Every element of `S` strictly greater than `r` **must** belong to the
  top-`orders` selection: there are `count(r) <= orders` such elements
  total (by definition of `count`), so none of them can be excluded in
  favor of a smaller element without decreasing the sum, and there is
  always room (`count(r) <= orders`) to include all of them.
- The remaining `orders - count(r)` slots of the top-`orders` selection
  must be filled by the next-largest available elements, which are exactly
  elements of `S` equal to `r` (nothing strictly between — `S` only
  contains integers, so the next value below "greater than `r`" is exactly
  `r`). Lemma 4 guarantees at least `orders - count(r)` such elements
  exist, so this is always realizable.

Hence the top-`orders` sum decomposes as:
```
sum(T) = Σ_{s ∈ S, s > r} s   +   r · (orders - count(r))
```
For a fixed color `i` with `inventory[i] > r`, its tokens exceeding `r` are
exactly `r+1, r+2, ..., inventory[i]`, summing to the telescoping
triangular-number difference `T(inventory[i]) - T(r)` where
`T(k) = k(k+1)/2`; summing over all such colors gives
`Σ_{s ∈ S, s > r} s = Σᵢ [T(inventory[i]) - T(r)]` (only over `i` with
`inventory[i] > r`, matching exactly the `if (inventory[i] > r)` guard in
`answer`). This is precisely the `a - b` term accumulated into `ret` for
each qualifying color, and `(orders - count) * r` is the flat-priced
remainder term added at the end — both computed under correct modular
arithmetic (each subtraction is renormalized via `(x % M + M) % M` to stay
non-negative before the next modulo reduction, and the final multiplication
`(orders - count) * r` is performed in `long` to avoid overflow before
taking `% M`). Therefore `answer(inventory, orders, r) ≡ sum(T) (mod 10^9+7)`,
which — combined with Lemma 1's identification of `sum(T)` as the true
maximum profit — proves `maxProfit` returns the correct answer modulo
`10^9 + 7`. ∎

---

## Optimization Journey

```
Naive (max-heap simulation) ────────────────────────────────────────────►
       Repeatedly pop the largest pile, sell one ball, push back.
       Time: O(n + orders log n)    Space: O(n)

           ▼  Observation: `orders` can be up to 10^9 — simulating sale by
              sale is infeasible. But the greedy process always converges
              to "every pile gets flattened toward a common threshold",
              i.e. the answer is exactly the sum of the top `orders`
              virtual price-tokens across all piles (Lemma 1) — a
              closed-form quantity that doesn't require simulating sales.

Binary Search on Threshold + Arithmetic-Series Formula ─────────────────►
       Binary-search the minimal flattening level `r` such that flattening
       every pile down to `r` costs at most `orders` sales (monotonic
       predicate, Lemma 2/3); price every token above `r` exactly via
       telescoping triangular numbers, and the leftover budget at the flat
       price `r` (guaranteed available, Lemma 4).
       Time: O(n log(max(inventory)))    Space: O(1)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive (max-heap simulation) | O(n + orders log n) | O(n) | Infeasible for `orders` up to `10^9` |
| Binary Search + Triangular-Sum Formula | O(n log(max(inventory))) | O(1) | Implemented by `SellDiminishingValuedColoredBallsImpl` |

> **Best solution:** `SellDiminishingValuedColoredBallsImpl` (binary search
> on the flattening threshold, closed-form revenue via triangular numbers)
> — the problem reduces to "sum of the top `orders` elements across virtual
> sorted sequences" (Lemma 1), which the monotonic `count` predicate
> (Lemma 2) lets us pin down in `O(log(max(inventory)))` search rounds, each
> costing `O(n)`, entirely avoiding any per-sale simulation.
