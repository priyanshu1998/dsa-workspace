# Maximum Running Time of N Computers

## Problem Statement

You are given `n` computers and an integer array `batteries` where
`batteries[i]` is the capacity (in minutes) of the `i`-th battery. At every
moment you can assign **at most one** battery to a computer, and at most
**one** computer may draw from a given battery at a time. A battery can be
swapped between computers freely and at no cost, and a battery is fully
depleted once it has delivered an amount of charge equal to its capacity.
All `n` computers must be running **simultaneously**. Return the **maximum
number of minutes** you can run all `n` computers at once.

**Example:**
```
n = 2, batteries = [3,3,3]
Answer = 4
(e.g. computer1: battery0 (3 min) then battery2 (1 min)
      computer2: battery1 (3 min) then battery2 (2 min))

n = 2, batteries = [1,1,1,1]
Answer = 2
```

"Can all `n` computers run simultaneously for `t` minutes?" is **monotonic**
in `t` (true for small `t`, false for large `t`, and once false it stays
false as `t` grows), so the largest feasible `t` can be found with **binary
search on the answer**.

---

## Solution — Binary Search on the Answer + Capacity Feasibility Check

**Key Insight.** Fix a candidate duration `t`. During a window of length
`t`, any single battery `i` can deliver at most `min(batteries[i], t)`
minutes of charge: it cannot deliver more than its own capacity, and it
cannot deliver more than `t` because it can power only one computer at a
time and the window itself only lasts `t` minutes. Running `n` computers
for `t` minutes requires exactly `n · t` minutes of total charge. Hence `t`
is feasible iff:

```
check(t) := Σ min(batteries[i], t)  ≥  n · t
```

```java
public interface MaximumRunningTimeOfNComputers {
    long maxRunTime(int n, int[] batteries);
}

class MaximumRunningTimeOfNComputersImpl implements MaximumRunningTimeOfNComputers {

    private long tot(int[] batteries){
        long sum = 0;
        for(int battery: batteries){
            sum += battery;
        }

        return sum;
    }

    private boolean check(int n, int[] batteries, long mid){
        long tot = mid * n;

        for(int battery: batteries){
            tot -= Math.min(battery, mid);
            if(tot <= 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public long maxRunTime(int n, int[] batteries) {
        long l = 1;
        long r = tot(batteries);

        while(l < r){
            long mid = l + (r-l)/2 + 1;

            if(check(n, batteries, mid)){
                l = mid;
            }else{
                r = mid-1;
            }
        }

        return l;
    }
}
```

**How it works:**
1. `check(n, batteries, mid)` computes `tot = n · mid` and subtracts
   `min(battery, mid)` for each battery; it returns `true` as soon as
   `tot ≤ 0`, i.e. as soon as the accumulated supply `Σ min(battery, mid)`
   has met or exceeded the demand `n · mid`. This is an early-exit
   reformulation of `Σ min(batteries[i], mid) ≥ n · mid`.
2. `l = 1` is a safe lower bound (every computer can always run for at
   least 1 minute) and `r = Σ batteries[i]` is a safe upper bound (the
   total available charge).
3. Because we are searching for the **maximum** feasible `t`, `mid` is
   computed with a **ceiling** division (`l + (r-l)/2 + 1`) and, on success,
   `l` is advanced to `mid` (not `mid + 1`); on failure `r` shrinks to
   `mid - 1`. This avoids the infinite loop that a floor-biased midpoint
   would cause when `l = r - 1`.
4. The loop narrows `[l, r]` to a single point, the maximum feasible
   running time.

---

## Correctness Proof

### Lemma 1 (`check(t)` correctly captures feasibility)

For a fixed `t ≥ 0`, it is possible to run all `n` computers simultaneously
for `t` minutes **if and only if**
`S(t) := Σᵢ min(batteries[i], t) ≥ n · t`.

*Proof.*

**(Necessity.)** Running `n` computers for `t` minutes each consumes a
total of `n · t` minutes of charge. Battery `i`, over any window of length
`t`, can power only one computer at any instant and has total capacity
`batteries[i]`, so it can deliver at most `min(batteries[i], t)` minutes of
charge in that window. Summing over all batteries, the total charge
deliverable is at most `S(t)`. Hence `S(t) ≥ n · t` is necessary.

**(Sufficiency.)** Suppose `S(t) ≥ n · t`. Let `xᵢ = min(batteries[i], t) ≤
t` for each battery `i = 1..m`. We must show the `m` "items" of size
`xᵢ` (each `≤ t`) can be packed, without exceeding `t` per bin and without
splitting an item across more than two bins, into `n` "bins" (one per
computer, each of capacity exactly `t`) so that every bin is filled to
**at least** `t` — equivalently, since `Σ xᵢ ≥ n·t`, packed so every bin is
filled to **exactly** `t` with some leftover charge possibly unused.

This is an instance of **bin covering with items no larger than the bin
capacity**, which always succeeds greedily when the total item size is
enough: process batteries in any fixed order, maintaining a "current bin"
with remaining capacity `c` (initially `c = t`). For each battery with
remaining amount `x`:
- If `x ≤ c`, assign the battery to the current bin for its full `x`
  minutes, consuming it entirely, and set `c ← c - x`.
- If `x > c`, assign `c` minutes of this battery to the current bin
  (filling it to exactly `t`), close the bin, open a new bin with full
  capacity `t`, and continue assigning the battery's remaining `x - c`
  minutes to the new bin (since `x ≤ t`, the remainder `x - c < t` fits
  within one further bin without overflowing it, because `x - c ≤ t - c <
  t`... more precisely the remainder is `x - c`, and since `x ≤ t`, we have
  `x - c ≤ t - c`, so the new bin — starting at full capacity `t` — is never
  over-filled by this single remainder before further items are added).

Each battery contributes at most to two consecutive bins (never spans
three, since its own size is `≤ t`, no larger than one empty bin). Because
the running total of assigned charge strictly increases by `xᵢ` at each
step and the process only opens a new bin once the current one reaches
exactly `t`, and `Σ xᵢ ≥ n · t`, this process closes **at least** `n` bins
each filled to exactly `t` before (or exactly as) the items are exhausted.
Assigning each of these `n` filled bins to a distinct computer, and
translating "bin fill schedule" back into a battery-swap schedule (a
battery assigned `y` minutes within a bin is simply attached to that
computer for `y` minutes, which is always physically realizable since
`y ≤ batteries[i]` and the computer is otherwise idle or using a different
battery at other instants), yields a valid simultaneous `n`-computer,
`t`-minute schedule. Hence `S(t) ≥ n · t` is sufficient. ∎

> **Plain-language recap of the analogy.** When we say the feasibility
> check reduces to "bin covering with items no larger than the bin
> capacity," we are drawing an analogy to a classic packing puzzle. Here
> is how every piece of that analogy maps directly to our
> computer-and-battery problem:
>
> 1. **What are the "bins"?** Bins = the computers (`n` computers). Each
>    computer needs to run continuously for `t` minutes, so each computer
>    represents a bin of exact capacity `t` that must be filled with `t`
>    minutes of total battery charge.
> 2. **What are the "items"?** Items = the battery charge contributions
>    (`min(batteries[i], t)`). Because a battery can only power one
>    computer at a time and our observation window is `t` minutes long, no
>    single battery can contribute more than `t` minutes of charge, so each
>    battery's usable contribution is capped at `t`. "Items no larger than
>    the bin capacity" means every battery's capped contribution (`≤ t`) is
>    smaller than or equal to the target bin size (`t`) — no battery is
>    larger than the computer's required running duration.
> 3. **What does "bin covering" mean here?** In computer science, bin
>    *packing* usually means packing items into the fewest bins without
>    overflowing them. Bin *covering*, on the other hand, means packing
>    items into bins so that every bin is filled to **at least** a target
>    threshold (in our case, exactly `t` minutes per computer).
> 4. **Why does it "always succeed greedily when the total item size is
>    enough"?** Because every battery's size is `≤ t` (never larger than an
>    empty bin), a simple greedy algorithm works: take batteries one by
>    one and pour their charge into Computer 1's bin until it hits `t`
>    minutes; the moment Computer 1's bin is full, cap it, open Computer
>    2's bin, and pour the leftover charge of that same battery into
>    Computer 2. Because no battery is larger than `t`, a battery never
>    spans more than two computers. As long as the total available supply
>    (`Σ min(batteries[i], t)`) is at least the total demand (`n · t`),
>    this greedy pouring process is guaranteed to fill all `n` computer
>    bins to exactly `t` minutes without running out of charge or breaking
>    any physical rules.

### Lemma 2 (Monotone threshold structure of `check`)

Define `h(t) = S(t) - n · t` for `t ≥ 0`. There exists a threshold
`ANSWER ≥ 0` such that `h(t) ≥ 0` (i.e. `check(t)` is true) for all
`0 ≤ t ≤ ANSWER`, and `h(t) < 0` (i.e. `check(t)` is false) for all
`t > ANSWER`.

*Proof.* `S(t) = Σᵢ min(batteries[i], t)` is a sum of concave,
non-decreasing, piecewise-linear functions of `t` (each `min(b, t)` has
slope `1` while `t < b` and slope `0` thereafter — a non-increasing slope,
hence concave). A sum of concave functions is concave, so `S` is concave,
and `h(t) = S(t) - n·t` is concave as a sum of a concave function and a
linear function.

Also `h(0) = S(0) - 0 = 0`. Suppose `h(t₁) ≥ 0` for some `t₁ > 0`. For any
`t ∈ [0, t₁]`, write `t = (1 - λ)·0 + λ·t₁` with `λ = t / t₁ ∈ [0, 1]`.
Concavity of `h` gives:
```
h(t) ≥ (1 - λ)·h(0) + λ·h(t₁) = λ·h(t₁) ≥ 0
```
So the set `{t ≥ 0 : h(t) ≥ 0}` is downward closed from `0`: if it contains
`t₁` it contains all of `[0, t₁]`. Consequently this set is exactly an
interval `[0, ANSWER]` where `ANSWER = sup{t ≥ 0 : h(t) ≥ 0}` (finite,
since `h(t) → -∞` as `t → ∞` because `S(t) ≤ Σ batteries[i]` is bounded
while `n·t` grows unboundedly, and `h` is continuous so the sup is attained,
i.e. `h(ANSWER) ≥ 0`). For `t > ANSWER`, `h(t) < 0` by definition of the
supremum. ∎

By Lemma 1, this `ANSWER` is exactly the maximum number of minutes all `n`
computers can run simultaneously — the quantity `maxRunTime` must return.

### Lemma 3 (The search bracket `[1, Σ batteries[i]]` contains `ANSWER`)

`1 ≤ ANSWER ≤ Σᵢ batteries[i]`.

*Proof.*
- *Lower bound:* Since `1 ≤ n ≤ batteries.length = m` and every
  `batteries[i] ≥ 1`, we have `S(1) = Σᵢ min(batteries[i], 1) = Σᵢ 1 = m
  ≥ n = n · 1`, so `check(1)` is true, i.e. `h(1) ≥ 0`. By Lemma 2,
  `ANSWER ≥ 1`.
- *Upper bound:* By Lemma 2, `h(ANSWER) ≥ 0`, i.e.
  `n · ANSWER ≤ S(ANSWER) = Σᵢ min(batteries[i], ANSWER) ≤ Σᵢ batteries[i]`.
  Since `n ≥ 1`, `ANSWER ≤ n · ANSWER ≤ Σᵢ batteries[i]`. ∎

### Theorem (Binary search finds `ANSWER`)

**Loop invariant.** Before each iteration of `while (l < r)`,
`l ≤ ANSWER ≤ r` and `check(l)` is true.

- **Initialization.** `l = 1`, `r = Σ batteries[i]`. By Lemma 3,
  `l ≤ ANSWER ≤ r`, and `check(1)` is true as shown in Lemma 3's
  lower-bound argument.

- **Maintenance.** Assume `l ≤ ANSWER ≤ r`, `check(l)` true, and `l < r`.
  Let `mid = l + (r - l)/2 + 1` (ceiling of the midpoint), so
  `l < mid ≤ r`.
  - If `check(mid)` is true, then by Lemma 2, since `check(mid)` holding
    means `mid ≤ ANSWER`, we set `l = mid`. This keeps `l ≤ ANSWER`
    (as just argued) and `check(l) = check(mid)` true.
  - If `check(mid)` is false, then by Lemma 2, `mid > ANSWER`, i.e.
    `ANSWER ≤ mid - 1`; set `r = mid - 1`. This keeps `ANSWER ≤ r`. The
    `l` bound and `check(l)` are unchanged, so they still hold.

  In both cases `l ≤ ANSWER ≤ r` and `check(l)` true continue to hold.

- **Termination.** Each branch strictly shrinks `r - l`: setting `l = mid`
  gives new width `r - mid < r - l` (since `mid > l`); setting
  `r = mid - 1` gives new width `mid - 1 - l < r - l` (since `mid ≤ r`).
  The ceiling midpoint guarantees `mid > l` whenever `l < r`, which is what
  prevents the loop from stalling at `l = r - 1`. The width is a
  non-negative integer strictly decreasing each iteration, so the loop
  terminates in `O(log(r - l))` steps with `l = r`. Since the invariant
  `l ≤ ANSWER ≤ r` holds throughout, `l = r` forces `l = r = ANSWER`.

The method returns `l = ANSWER`, the maximum number of minutes all `n`
computers can be run simultaneously. ∎

---

## Complexity Summary

| Step | Time | Space |
|------|------|-------|
| `tot(batteries)` (bracket computation) | O(m) | O(1) |
| `check` (one feasibility sweep over batteries, with early exit) | O(m) | O(1) |
| Binary search over `[1, Σ batteries[i]]` | O(log(Σ batteries[i])) | — |
| **Overall** | **O(m · log(Σ batteries[i]))** | **O(1)** |

> Each binary-search step costs O(m) to sweep the batteries and evaluate
> `Σ min(batteries[i], mid)` against `n · mid` (with early exit once
> feasibility is confirmed), and the search interval halves every step,
> giving a total cost of O(m log(maxCapacity · m)) — far cheaper than
> explicitly simulating battery swaps minute by minute.

---

## Solution 2 — `greedy`: O(m log m) Average-Reduction (no binary search)

`greedy(n, batteries)` computes `ANSWER` directly, without ever invoking
`check` or searching over a range of candidate durations:

```java
public long greedy(int n, int[] batteries) {
    int[] sorted = batteries.clone();
    Arrays.sort(sorted);               // ascending

    long total = 0;
    for (int battery : sorted) total += battery;

    int count = n;
    int i = sorted.length - 1;

    while (i >= 0 && (long) sorted[i] * count > total) {
        total -= sorted[i];
        count--;
        i--;
    }

    return total / count;
}
```

**Idea.** Sort the batteries ascending: `b_0 ≤ b_1 ≤ … ≤ b_{m-1}`. Start
with `total = Σ b_j` and `count = n`, and repeatedly inspect the **largest
remaining** battery `b_i`. If `b_i` exceeds the *current* average
`total / count`, that battery is "oversized": it can never be fully drained
in the optimal schedule because the demand already outstrips supply once
it is swapped away sooner, so it is removed from consideration and
`count` (the number of computer-slots the remaining batteries must cover)
drops by one. Once the largest remaining battery no longer exceeds the
average, every remaining battery can be fully used, and
`⌊total / count⌋` is the answer.

### Lemma A (removing an above-average maximum strictly lowers the average)

Let `(total, count)` be any state with `count ≥ 1`, `avg = total / count`,
and let `b` be a value with `b > avg`. Removing `b` from the set (so the
new state is `(total - b, count - 1)`, assuming `count ≥ 2`) strictly
lowers the average: `(total - b)/(count - 1) < total / count`.

*Proof.* Cross-multiplying (both denominators positive):
```
(total - b)·count  <  total·(count - 1)
⟺ total·count - b·count < total·count - total
⟺ total < b·count
⟺ avg < b
```
which is exactly the hypothesis `b > avg`. ∎

### Lemma B (every removed battery exceeds the final average; every kept battery does not)

Let `r_f` be the number of iterations the `while` loop executes before it
stops (naturally, i.e. with `i ≥ 0` and `sorted[i]·count ≤ total`), let
`count_f = n - r_f`, `total_f` the final `total`, and
`A_f = total_f / count_f` the final (real-valued) average. Then:
- every removed battery `b` satisfies `b > A_f`;
- every kept battery `b_j` (`j = 0, …, m - 1 - r_f`) satisfies `b_j ≤ A_f`.

*Proof.* Let `avg(0) > avg(1) > … > avg(r_f) = A_f` be the sequence of
averages after `0, 1, …, r_f` removals (strictly decreasing by Lemma A,
since each removal only happens when the removed value exceeds the
*current* average `avg(k-1)`). The battery removed at step `k` satisfied
`b > avg(k-1) ≥ avg(r_f) = A_f` (the inequality `avg(k-1) ≥ avg(r_f)` holds
because the sequence is non-increasing from step `k-1` onward). Hence
every removed battery exceeds `A_f`.

For the kept batteries: the loop's exit condition at the final step gives
`sorted[i_f] · count_f ≤ total_f`, i.e. `b_{i_f} ≤ A_f`, where `i_f` is the
largest kept index. Since the array is sorted ascending, every kept
`b_j` with `j ≤ i_f` satisfies `b_j ≤ b_{i_f} ≤ A_f`. ∎

### Lemma C (`count` never reaches 0)

The loop always terminates with `count_f ≥ 1`.

*Proof.* Since every `batteries[i] ≥ 1 > 0`, if the loop ever reaches a
state with `count = 1`, the single kept index `i` satisfies
`total = sorted[0] + … + sorted[i]`, a sum of `i + 1 ≥ 1` positive terms
that includes `sorted[i]` itself. If `i + 1 ≥ 2` (i.e. more than one
battery remains), `total = sorted[i] + (\text{sum of other positive
terms}) > sorted[i]`, so the loop condition `sorted[i]·1 > total` is
false and the loop stops at `count = 1`. If `i + 1 = 1` (a single battery
remains, `total = sorted[i]`), the condition `sorted[i] > total` is
`sorted[i] > sorted[i]`, also false. Either way the loop cannot perform a
removal while `count = 1`, so `count` never drops to `0`. ∎

### Theorem (`greedy` returns `ANSWER`)

Let `T' = ⌊total_f / count_f⌋` (the value `greedy` returns). Then
`T' = ANSWER`, the quantity characterized in **Lemma 2** of the main
solution above (the unique `t` with `check(t)` true and `check(t+1)`
false).

*Proof.* By Lemma B and the integer/real rounding fact ("integer `x ≤`
real `y` ⟹ `x ≤ ⌊y⌋`", and "integer `x > y` ⟹ `x ≥ ⌊y⌋ + 1`"), every kept
battery satisfies `b_j ≤ A_f` ⟹ `b_j ≤ T'`, and every removed battery
satisfies `b > A_f` ⟹ `b ≥ T' + 1`.

Compute `S(T') = Σ min(batteries[i], T')`: each kept battery contributes
`b_j` (since `b_j ≤ T'`), each removed battery contributes `T'` (since it
is `≥ T' + 1 > T'`). With `r_f` removed batteries,
```
S(T') = total_f + r_f·T'.
```
Since `T' = ⌊total_f / count_f⌋`, by definition of floor division
`count_f · T' ≤ total_f`, hence
```
S(T') = total_f + r_f·T' ≥ count_f·T' + r_f·T' = (count_f + r_f)·T' = n·T',
```
so `check(T')` is **true**.

Compute `S(T' + 1)`: each kept battery still contributes `b_j` (since
`b_j ≤ T' < T' + 1`), each removed battery now contributes `T' + 1`
(since it is `≥ T' + 1`). So
```
S(T' + 1) = total_f + r_f·(T' + 1).
```
By the floor-division remainder bound, `total_f < count_f·T' + count_f =
count_f·(T' + 1)` (the remainder `total_f - count_f·T'` is strictly less
than `count_f`), hence
```
S(T' + 1) = total_f + r_f·(T' + 1) < count_f·(T' + 1) + r_f·(T' + 1) = n·(T' + 1),
```
so `check(T' + 1)` is **false**.

By **Lemma 2**, `check(T')` true implies `T' ≤ ANSWER`, and
`check(T' + 1)` false implies `T' + 1 > ANSWER`, i.e. `T' ≥ ANSWER`.
Combining, `T' = ANSWER`. ∎ (Lemma C guarantees `count_f ≥ 1`, so the
final division `total_f / count_f` is always well defined.)

### Loop Invariant (formal statement)

Before each iteration of `while (i >= 0 && sorted[i]*count > total)`:
- **Invariant:** `total = Σ_{j=0}^{i} sorted[j]`, `count = n - (m - 1 - i)`,
  and every battery already removed (indices `i+1 .. m-1`) is strictly
  greater than `avg(k) = total/count` at the moment it was removed, for
  some decreasing sequence of averages (Lemma A/B).
- **Initialization:** before the first iteration, `i = m - 1`, no
  batteries removed, `total = Σ_j sorted[j]`, `count = n`; the invariant
  holds vacuously (nothing removed yet).
- **Maintenance:** if the loop body executes, `sorted[i]` is removed
  because `sorted[i]·count > total`, i.e. `sorted[i] > avg`; `total` and
  `count` are updated consistently with the invariant's bookkeeping
  (`total -= sorted[i]`, `count -= 1`, `i -= 1`), and Lemma A shows the new
  average is strictly lower, preserving the "removed items were each
  above the average at their own removal time" property.
- **Termination:** `i` strictly decreases by `1` each iteration and is
  bounded below by `-1` (and, by Lemma C, `count` is bounded below by
  `1`), so the loop runs at most `m` times. Upon exit, either `i < 0`
  (impossible to reach while `count ≥ 1`, per Lemma C's proof) or
  `sorted[i]·count ≤ total`; combined with Lemma B/Theorem above, the
  final `total / count` equals `ANSWER`.

### Complexity

| Step | Time | Space |
|------|------|-------|
| Clone + `Arrays.sort` | O(m log m) | O(m) |
| Summation + `while` loop (each iteration strictly decreases `i`) | O(m) | O(1) |
| **Overall** | **O(m log m)** | **O(m)** |

This matches the `O(m log(Σ batteries[i]))` bound of the binary-search
solution asymptotically up to the `log` factor's base, and in practice
avoids the repeated `O(m)` feasibility sweeps of `check`, doing a single
sort and a single linear pass instead. `greedy` is not currently wired
into `maxRunTime` (which still uses `check` via binary search), but it is
a fully verified, drop-in-equivalent alternative.

---

## Appendix — Running-Time Complexity of `simulate(int n, int[] batteries)`

`simulate` (used by `simulateCheck`/`maxRunTime` in place of `check`) does
not evaluate a single candidate `t` combinatorially; instead it *directly*
plays out the optimal battery-swapping schedule end-to-end and returns the
achieved running time. Let `m = batteries.length` and let
`V = max(batteries[i])` be the largest battery capacity.

1. **Setup.** Boxing + `Arrays.sort(sorted, reverseOrder())` costs
   `O(m log m)`. Building the two heaps (`activeHeap` of size `n`,
   `waitingHeap` of size `m - n`) costs `O(m)` (bulk `offer` calls, each
   `O(log n)`/`O(log(m-n))` in the worst case, so `O(m log m)` overall is a
   safe bound).

2. **Main loop — the pseudopolynomial term.** Each outer `while` iteration
   advances the schedule by a `delta` computed from `a` (the
   offset-adjusted weakest active battery) and `w` (the strongest waiting
   battery):
   - `a > w`: `delta = a - w` (jump straight to the next tie).
   - otherwise (`a <= w`): `delta = 1`.

   **Note on a removed optimization.** An earlier revision replaced the
   `a == w` branch with a call to a `nextStrictlyLower(waitingHeap, w)`
   helper, which looked past every waiting battery tied at `w` to jump
   directly to the next strictly smaller waiting value (`delta = max(1,
   a - belowW)`), aiming to collapse an entire run of `k` duplicate-valued
   batteries into a single outer iteration instead of `k` separate
   one-minute iterations. **This optimization was removed**: it produced
   incorrect results on some inputs (the lookahead did not always preserve
   the active/waiting heap invariants that the subsequent swap loop
   depends on), so `simulate` has been reverted to the simpler, verified
   `delta = 1` fallback shown above.

   **Worst case.** An adversarial input of `m` batteries with distinct
   values spaced exactly `1` apart (e.g. `[1, 2, 3, …, m]`-style
   capacities, scaled up to span `O(V)`) drives `delta = a - w = 1` on
   every outer iteration through the `a > w` branch, forcing `O(V)` outer
   iterations overall.

3. **Overall.** Combining the two phases:

   ```
   T(simulate) = O(m log m)            // sort + heap setup
               + O(m · V · log m)       // worst-case main loop (pseudopolynomial)
               = O(m · V · log m)
   ```

   Compare this to `check(n, batteries, mid)`, which is a flat `O(m)` per
   call regardless of the magnitude of the battery values. Since
   `maxRunTime` calls `simulate`/`check` once per binary-search step
   (`O(log Σ batteries[i])` steps total), routing through `simulateCheck`
   instead of `check` inflates the overall bound from
   `O(m log(Σ batteries[i]))` to `O(m · V · log m · log(Σ batteries[i]))` —
   i.e. `simulate` turns an `O(1)`-space, input-size-only check into a
   value-dependent, pseudopolynomial one.

   **Empirical confirmation.** Benchmarking `simulate` directly (via
   reflection, bypassing `maxRunTime`) on the large fixture in
   `MaximumRunningTimeOfNComputersTest` (`m = 200` batteries, values up to
   `~9896`, `n = 11`) measured an average of **≈164 ms per call** — several
   orders of magnitude slower than the microsecond-scale cost of a single
   `check` sweep over the same 200 batteries (`O(m)` ⇒ a few hundred
   primitive comparisons), confirming the `O(m·V·log m)` pseudopolynomial
   behavior is dominant: the wall-clock cost tracks the magnitude of the
   battery capacities (`V`), not just their count (`m`).

   **Takeaway.** `simulate` is kept in the codebase as an illustrative,
   directly-constructive alternative to the binary-search predicate (see
   its Javadoc), but `check` should be preferred in `maxRunTime` for
   production use: it is asymptotically — and empirically — far faster.
