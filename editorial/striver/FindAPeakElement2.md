# Find a Peak Element II

## Problem Statement

Given a 2D matrix `mat` of size `m x n` where no two adjacent cells (horizontally or vertically) are equal, find the position `(i, j)` of **a peak element** and return it as `[i, j]`. An element is a peak if it is strictly greater than all of its existing neighbors (up, down, left, right — cells outside the grid are conceptually `-∞`). If there are multiple peaks, returning the position of **any** one of them is acceptable.

**Example:**
```
mat = [[1,4],[3,2]]   → [0,1] (value 4) is a peak
mat = [[10,20,15],[21,30,14],[7,16,32]] → [1,1] (30) or [2,2] (32) are valid peaks
```

Because the grid borders act as `-∞`, **a peak is guaranteed to exist** for any non-empty matrix.

---

## Solutions

### 1. Naive Solution — O(m·n) Time, O(1) Space

Scan every cell and check it against its up/down/left/right neighbors (treating out-of-bounds neighbors as `-∞`).

```java
class NaiveSolution implements FindAPeakElement2 {
    @Override
    public int[] findPeakGrid(int[][] mat) {
        int rows = mat.length, cols = mat[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int up = (i == 0) ? Integer.MIN_VALUE : mat[i - 1][j];
                int down = (i == rows - 1) ? Integer.MIN_VALUE : mat[i + 1][j];
                int left = (j == 0) ? Integer.MIN_VALUE : mat[i][j - 1];
                int right = (j == cols - 1) ? Integer.MIN_VALUE : mat[i][j + 1];
                if (mat[i][j] > up && mat[i][j] > down && mat[i][j] > left && mat[i][j] > right) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{}; // unreachable given the problem's guarantees
    }
}
```

**Complexity:**
Time: O(m·n)
Space: O(1)

**Drawback:** Inspects every cell even though, as in the 1D peak problem, the "slope" toward the maximum of a single column already tells us which half of the columns must contain a peak — no binary search speedup is used.

---

### 2. Optimized Solution (Column Binary Search / Divide-and-Conquer) — O(m·log n) Time, O(log n) Space

**Key Insight:** Binary search over **columns** instead of the full grid.

1. Pick `mid = (l + r) / 2` and find `h`, the row index of the **maximum value in column `mid`**. Because `h` is the argmax of its column, `mat[h][mid]` is automatically `>=` its up and down neighbors — the up/down peak condition is satisfied *for free*.
2. Only the left/right neighbors of `(h, mid)` need to be checked:
   - If `mat[h][mid]` beats both (or is at a grid edge where a side doesn't exist), `(h, mid)` is a 2D peak — return it.
   - If `mat[h][mid] < mat[h][mid-1]`, discard columns `[mid, r]` and recurse on `[l, mid-1]`.
   - Otherwise (`mat[h][mid] < mat[h][mid+1]`), discard columns `[l, mid]` and recurse on `[mid+1, r]`.

```java
class FindAPeakElement2Impl implements FindAPeakElement2 {

    private int globalMaximum(int[][] mat, int j) {
        int max = mat[0][j];
        int idx = 0;
        for (int i = 0; i < mat.length; i++) {
            if (mat[i][j] > max) {
                idx = i;
                max = mat[i][j];
            }
        }
        return idx;
    }

    private int[] divideAndConquer(int[][] mat, int l, int r) {
        if (l > r) return new int[]{};

        int mid = (l + r) / 2;
        int h = globalMaximum(mat, mid);
        int k = mid;

        if ((mid == 0 || mat[h][k] > mat[h][k - 1]) && (mid == mat[0].length - 1 || mat[h][k] > mat[h][k + 1])) {
            return new int[]{h, k};
        } else if (mid > 0 && mat[h][k] < mat[h][k - 1]) {
            return divideAndConquer(mat, l, mid - 1);
        } else {
            return divideAndConquer(mat, mid + 1, r);
        }
    }

    @Override
    public int[] findPeakGrid(int[][] mat) {
        return divideAndConquer(mat, 0, mat[0].length - 1);
    }
}
```

---

## Formal Proof of Correctness

### Invariant (stated over the recursive column range `[l, r]`)

**Invariant `I(l, r)`:** *The submatrix `mat[:, l..r]` (all rows, columns `l` through `r`) contains at least one cell that is a 2D peak of the **entire** original matrix `mat`.*

#### Base Case

Before the first call, `l = 0` and `r = n - 1` (the whole matrix). Let `M` be the maximum value anywhere in `mat`, located at `(i*, j*)`. By definition of maximum, no neighbor of `(i*, j*)` (up, down, left, or right, wherever they exist) can exceed `M`, and distinct-adjacent-values guarantee no neighbor equals `M` either — so `(i*, j*)` is strictly greater than every existing neighbor, i.e. `(i*, j*)` is a 2D peak of `mat`. Since `(i*, j*)` trivially lies in columns `[0, n-1]`, `I(0, n-1)` holds. ∎

#### Inductive Step (Maintenance)

Assume `I(l, r)` holds for the current call (a peak of the whole matrix exists in columns `[l, r]`). Let `mid = ⌊(l+r)/2⌋`, and let `h = argmax` of column `mid` (so `mat[h][mid] >= mat[i][mid]` for every row `i`).

**Case A — `(h, mid)` passes the peak check.** The algorithm verified `mat[h][mid] > mat[h][mid-1]` (or `mid == 0`) and `mat[h][mid] > mat[h][mid+1]` (or `mid == n-1`). Combined with `mat[h][mid] >= mat[i][mid]` for all `i` (up/down), `(h, mid)` is strictly greater than every existing neighbor — it is a genuine 2D peak, and the algorithm returns it immediately. This case terminates the recursion with a provably correct answer.

**Case B — `mat[h][mid] < mat[h][mid-1]`, recurse on `[l, mid-1]`.** We must show `I(l, mid-1)` holds, i.e. a whole-matrix peak exists within columns `[l, mid-1]`.

Let `X` be the maximum value of the submatrix `mat[:, l..mid-1]`, located at `(i*, j*)` with `l <= j* <= mid-1`. Since `mat[h][mid-1]` is one particular entry of this submatrix (column `mid-1` is inside `[l, mid-1]`):
```
X >= mat[h][mid-1] > mat[h][mid] >= mat[i][mid]   for every row i.      (★)
```
The middle inequality is the algorithm's branch condition; the last inequality is because `h` is the argmax of column `mid`. Now check all four neighbor directions of `(i*, j*)`:
- **Up/down:** both neighbors (if they exist) lie in column `j*`, which is inside `[l, mid-1]`, so they are `<= X` by definition of `X` as the submatrix maximum.
- **Left:** if it exists, it lies in column `j*-1 ∈ [l, mid-1]` (since `j* >= l`, if `j* > l` the left neighbor is still inside the submatrix), so it is `<= X`.
- **Right:** if `j* < mid - 1`, the right neighbor is column `j*+1 <= mid-1`, inside the submatrix, so `<= X`. If `j* = mid-1`, the right neighbor is in column `mid`, and by (★), `X > mat[i][mid]` for every row `i`, in particular the right neighbor's row — so the right neighbor is strictly `< X`.

In every case, every existing neighbor of `(i*, j*)` is `<= X`, and the only case needing strict inequality (right neighbor crossing into column `mid`) is strict by (★). Combined with distinct-adjacent-values (no neighbor equals `X`), `(i*, j*)` is strictly greater than all its existing neighbors — it is a 2D peak of the whole matrix, and `l <= j* <= mid-1`. Hence `I(l, mid-1)` holds. ∎

**Case C (symmetric) — `mat[h][mid] < mat[h][mid+1]`, recurse on `[mid+1, r]`.** Identical argument with `mid+1` in place of `mid-1` and the left/right neighbor roles swapped, concluding `I(mid+1, r)` holds. ∎

By induction over the recursion, `I(l, r)` holds at every call, so when the algorithm returns in Case A, the returned cell is guaranteed to be a genuine peak of the whole matrix.

### Termination (Decreasing Variant)

Define `V(l, r) = r - l`, a non-negative integer whenever `l <= r` (guaranteed to be well-defined since `I(l,r)` is only invoked on non-empty ranges, and the base case range `[0, n-1]` is non-empty for `n >= 1`).

- **Case B:** new range is `[l, mid-1]`. Since `l <= mid <= r`, we have `V' = (mid - 1) - l <= r - l - 1 = V - 1` (using `mid <= r`).
- **Case C:** new range is `[mid+1, r]`. Since `l <= mid <= r`, we have `V' = r - (mid + 1) <= r - l - 1 = V - 1` (using `mid >= l`).

So `V` strictly decreases by at least `1` on every recursive call that does not return directly (Case A). Since `V` is a non-negative integer (by `I`, a peak exists in `[l, r]`, so `l <= r` always holds when the function is invoked) that strictly decreases, by the well-ordering of `ℕ` it cannot decrease forever: it must reach `V = 0`, i.e. `l == r`, within finitely many steps — specifically at most `V_0 = n - 1` recursive calls. When `l == r`, `mid = l = r`, and `I(l, r)` with a single column forces column `mid`'s argmax row to itself satisfy the up/down condition trivially and both edge conditions (`mid == l` acts as a boundary on one or both sides), so Case A is guaranteed to fire and the recursion terminates. Hence the recursion always terminates, after `O(log n)` calls (since `V` at least halves each step via the midpoint split). ∎

---

## Optimization Journey

```
Naive ───────────────────────────────────────────────────────────────►
       Scan every cell, compare against all 4 existing neighbors.
       Time: O(m·n)    Space: O(1)

           ▼  Observation: picking the argmax row of any single column already
              satisfies the up/down peak condition for free. Comparing that
              cell's value to its left/right neighbors tells us which half of
              the *columns* is guaranteed to still contain a whole-matrix peak
              — the other half can be discarded without ever inspecting it.

Column Binary Search (Divide & Conquer) ────────────────────────────────►
       Each call scans one column (O(m)) to find its argmax row, then makes
       one left/right comparison to decide whether to return or recurse.
       Time: O(m·log n)    Space: O(log n) recursion stack
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Naive | O(m·n) | O(1) | Checks every cell's 4 neighbors |
| Column Binary Search | O(m·log n) | O(log n) | Exploits the "argmax-of-column satisfies up/down for free" observation |

> **Best solution:** `FindAPeakElement2Impl` (Column Binary Search) — trades the full O(m·n) scan for O(m·log n) by binary-searching over columns, using each column's maximum to both satisfy half the peak condition and determine which half of the columns to discard.
