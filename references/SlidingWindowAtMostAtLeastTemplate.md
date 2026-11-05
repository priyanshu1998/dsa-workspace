# The `atMost` / `atLeast` Dual-Output Sliding Window Template

> Source: `minSubArrayLen` (Minimum Size Subarray Sum), refactored around two
> general-purpose helpers that each answer **two** questions about a
> monotonic window condition in a single `O(n)` pass: "how many subarrays
> satisfy it?" and "what is the shortest one that does?"

```java
class Solution {

    private record Tuple(long count, int min){}

    private Tuple atMost(int target, int[] nums){
        int l=0;
        long sum = 0;
        long count = 0;
        int result = nums.length+1;

        for(int r=0; r<nums.length; r++){
            sum += nums[r];
            while(sum > target){
                sum -= nums[l];
                l++;
            }
            count += r-l+1;
            if (l <= r) {                     // guard: skip degenerate/empty window
                result = Math.min(result, r-l+1);
            }
        }

        return (result <= nums.length)?
            new Tuple(count, result):
            new Tuple(0, 0);
    }

    private Tuple atLeast(int target, int[] nums){
        long sum = 0;
        long count = 0;
        int result = nums.length+1;
        for(int l=0,r=0; l<nums.length; l++){
            while(r < nums.length && sum < target){
                sum += nums[r];
                r++;
            }
            if(sum >= target){
                count += nums.length-r+1;
                result = Math.min(result, r-l);
            }
            sum -= nums[l];
        }
        return (result <= nums.length)?
            new Tuple(count, result):
            new Tuple(0, 0);
    }

    public int minSubArrayLen(int target, int[] nums) {
        return atLeast(target, nums).min;
    }
}
```

---

## Why Bundle `count` and `min` Together?

Most sliding-window editorials in this repo (`MinimumSizeSubarraySum.md`,
`SubarrayProductLessThanK.md`, `CountOfSubstringsContainingEveryVowelAndKConsonants2.md`)
treat "count all valid windows" and "find the shortest valid window" as
separate problems with separate helpers. This template observes that **both
quantities fall out of the same scan**:

- Every time the window `[l, r]` is confirmed valid, the number of valid
  windows *anchored at the current pivot* (`r - l + 1` elements ending at
  `r` for `atMost`, or `nums.length - r + 1` windows starting at `l` for
  `atLeast`) can be added to a running `count`.
- The same moment of validity is exactly when a length (`r - l + 1` or
  `r - l`) is a candidate for the minimum.

So instead of writing `atMostCount`, `atMostMinLen`, `atLeastCount`,
`atLeastMinLen` as four separate single-purpose scans, one scan per
direction suffices, returned as a `Tuple(count, min)`.

---

## `atMost(target, nums)` — Window Sum `<= target`

This is the classic "count subarrays with sum at most k" scan (see
`SubarrayProductLessThanK.md` for the product variant), extended to also
track the minimum window length:

1. **Expand** `r` unconditionally each iteration, folding `nums[r]` into `sum`.
2. **Shrink** from `l` while `sum > target` — this restores the window to
   the largest valid (`sum <= target`) window ending at `r`.
3. **Count**: every subarray `[i, r]` for `i` in `[l, r]` has sum `<= target`
   (monotonicity: shrinking `i` down to `l` only adds more positive
   elements and could violate the bound, but everything from `l` to `r` is
   *already* known valid by construction of `l`), so there are
   `r - l + 1` valid subarrays ending at `r`.
4. **Track min**: `r - l + 1` is also the shortest valid window ending at `r`.

### ⚠️ Pitfall: degenerate windows corrupt `min` (confirmed by testing, now fixed above)

If a single element already exceeds `target`
(e.g. `nums = [10, 1, 1, 1]`, `target = 3`), the shrink loop can push `l`
**past** `r` (`l == r + 1`), making `r - l + 1 == 0`. The `count` update is
harmless (`+= 0`), but an unguarded `result = Math.min(result, 0)` would
permanently pin `result` at `0` — even though real, non-empty valid windows
exist later in the array:

```
// without the guard:
atMost(3, [10, 1, 1, 1]) → Tuple(count=6, min=0)   // WRONG: min should be 1
```

`count = 6` is correct (all 6 subarrays of `[1, 1, 1]` have sum `<= 3`), but
the true shortest valid window has length `1` (e.g. `[1]`), not `0`. The
template's final guard `result <= nums.length ? ... : (0, 0)` only filters
out the case where **no** window was ever valid (`result` never updated
from the `n + 1` sentinel) — it does not filter out this in-between case of
a spuriously small `result` from a zero-length degenerate window.

**Fix (already applied in the `atMost` listing above):** only update
`result` when the window is non-empty, i.e. `l <= r`:
```java
if (l <= r) {
    result = Math.min(result, r-l+1);
}
```
With the guard in place:
```
atMost(3, [10, 1, 1, 1]) → Tuple(count=6, min=1)   // correct
```
(`atMost`'s `count` was never affected by this bug — `+= 0` for the
degenerate window is harmless — only `min` needed the guard.)

---

## `atLeast(target, nums)` — Window Sum `>= target`

The mirror scan for "at least": here `l` is the outer loop driver (one step
per iteration, matching the standard two-pointer invariant where both
pointers only move forward), and `r` catches up only as needed:

1. For each `l`, **expand** `r` while the window `[l, r)` hasn't yet
   reached `target`.
2. If `sum >= target` after expanding, window `[l, r)` is the *shortest*
   valid window starting at `l` (by construction — `r` stopped at the first
   point validity was reached). Every further extension `[l, r')` for
   `r' >= r` stays valid too (elements are positive), so there are
   `nums.length - r + 1` valid windows starting at `l`.
3. **Track min**: `r - l` is the shortest valid window anchored at `l`.
4. Unconditionally remove `nums[l]` from `sum` before advancing `l`,
   preparing the invariant for `l + 1` (the next iteration's `while` loop
   will re-expand `r` if the removal dropped the window below `target`).

Unlike `atMost`, `atLeast` does **not** exhibit the degenerate-window bug:
`r >= l` always holds here (the `while` only ever increases `r`, and the
`if` only fires once `sum >= target`, which requires at least one element),
so `r - l >= 0` is never corrupted the way `atMost`'s `r - l + 1` can be
when a single oversized element forces `l` past `r`.

This is exactly the engine behind `MinimumSizeSubarraySum.md`'s optimized
solution, generalized to also accumulate `count` — see that file's
correctness proof (prefix-of-valid-starts argument) for the formal argument
that `atLeast`'s per-`l` window is minimal and that both pointers only
advance, giving `O(n)` total time.

---

## Applying the Template

| Call | Answers |
|------|---------|
| `atLeast(target, nums).min()` | `minSubArrayLen` — shortest subarray with sum `>= target` |
| `atLeast(target, nums).count()` | Count of subarrays with sum `>= target` |
| `atMost(target, nums).count()` | Count of subarrays with sum `<= target` (⚠️ `.min()` needs the guard above) |
| `atMost(k).count() - atMost(k-1).count()` | Count of subarrays with sum **exactly** `k` (the same `atLeast(k) - atLeast(k+1)` trick used in `CountOfSubstringsContainingEveryVowelAndKConsonants2.md`, mirrored for sums) |

### Variation: swap the aggregation, not the skeleton

Both helpers only ever touch `sum` and the comparison against `target`.
Replacing `sum += nums[r]` / `sum -= nums[l]` with a running **product**
(`SubarrayProductLessThanK.md`) or a **frequency map** (distinct-count
problems like `CountSubstringAtMostKDistinct` / `CountExactlyKDistinct` in
`SlidingWindowMindmap.md`) reuses the identical pointer-movement skeleton —
only the predicate (`sum > target` vs. `product >= target` vs.
`distinct.size() > k`) and the per-window contribution formula change.

### Variation: extend the `Tuple`

Nothing restricts the payload to `(count, min)`. The same single pass could
just as easily track a **maximum** length, the **sum/product of the
shortest window**, or the **starting index** of the best window, by adding
fields to `Tuple` and updating them at the same point the existing fields
are updated — the scan structure (expand/shrink/record) does not change.

---

## Complexity Summary

| Helper | Time | Space | Notes |
|--------|------|-------|-------|
| `atMost` | `O(n)` | `O(1)` | `l`, `r` each advance `<= n` times total; `min` requires the `l <= r` degenerate-window guard (included above) for correctness |
| `atLeast` | `O(n)` | `O(1)` | `l`, `r` each advance `<= n` times total; no degenerate-window issue |
| Combined `exactly(k)` via either helper | `O(n)` | `O(1)` | Two calls, same asymptotics |
