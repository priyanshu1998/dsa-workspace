# Merge k Sorted Lists

## Problem Statement

You are given an array of `k` linked-lists `lists`, each linked-list is
sorted in ascending order. Merge all the linked-lists into one sorted
linked-list and return it.

**Example:**
```
lists = [[1,4,5],[1,3,4],[2,6]]
Answer = [1,1,2,3,4,4,5,6]

lists = []
Answer = []

lists = [[]]
Answer = []
```

This generalizes "Merge Two Sorted Lists" to `k` lists. The naive approach
of repeatedly merging two lists at a time works but scales poorly; the key
insight is that at every step we only ever need to know the *global
minimum* among the current heads of all `k` lists — a problem a **min-heap
of size `k`** solves in `O(log k)` per extraction instead of `O(k)`.

---

## Solutions

### 1. Brute Force — Collect, Sort, Relink — O(N log N) Time, O(N) Space

Walk every list, collect all `N` node values into one array, sort it, then
build a brand-new linked list from the sorted array.

**Complexity:** Time `O(N log N)` for the sort. Space `O(N)` for the
collected array (plus the new nodes).

**Trade-off:** Simple and obviously correct, but ignores the fact that
each individual list is *already sorted* — paying for a full sort when
far less work is required.

---

### 2. Sequential Pairwise Merge — O(N·k) Time, O(1) Space

Merge `lists[0]` with `lists[1]` using the standard two-pointer
merge-two-sorted-lists routine, merge that result with `lists[2]`, and so
on, folding the whole array into a single running result.

**Complexity:** Time `O(N·k)` — the running merged list grows to length
`O(N)` well before all `k` lists are folded in, so later merges each scan
close to `O(N)` nodes, over `k` merges. Space `O(1)` extra (besides the
output list itself, built in place by relinking existing nodes).

**Trade-off:** No extra heap/array bookkeeping, but quadratic-ish in `k` —
poor when `k` is large.

---

### 3. Divide and Conquer Pairwise Merge — O(N log k) Time, O(log k) Space

Pair up the lists `(0,1), (2,3), ...` and merge each pair with the
two-pointer routine; this halves the number of lists. Repeat on the
resulting `⌈k/2⌉` lists, then `⌈k/4⌉`, etc., until one list remains —
exactly like the merge step of merge sort, but merging `k` already-sorted
runs instead of two.

**Complexity:** Time `O(N log k)` — there are `⌈log₂ k⌉` rounds, and each
round does `O(N)` total work across all its merges (every node is visited
once per round). Space `O(log k)` for the recursion stack (or `O(1)` if
done iteratively with an explicit loop).

**Trade-off:** Matches the asymptotically-optimal `O(N log k)` time with
very low space, and is simple to reason about — but requires the array of
lists to be mutated/compacted round by round.

---

### 4. Min-Heap of List Heads (implemented in `MergeKSortedListsImpl`) — O(N log k) Time, O(k) Space

**Key Insight:** The next node to emit into the merged output is always
the smallest head among the `k` lists' *current* heads. A min-heap keyed
by `val` lets us find and remove that minimum in `O(log k)`, and after
consuming a node we simply push its successor (`node.next`) back into the
heap in its place — so the heap always holds exactly one "current head"
per still-non-exhausted list.

Rather than using `java.util.PriorityQueue`, this implementation hand-rolls
an **array-based binary min-heap** directly over the `lists` array itself
(no extra array is allocated for the heap):

```java
private int val(ListNode list){
    if(list == null) return Integer.MAX_VALUE;
    return list.val;
}

private void percolateDown(ListNode[] list, int lastIdx, int i){
    int smallest = i;
    int l = 2*i+1;
    int r = 2*i+2;
    if (l <= lastIdx && val(list[l]) < val(list[smallest])) smallest = l;
    if (r <= lastIdx && val(list[r]) < val(list[smallest])) smallest = r;
    if (smallest != i) {
        var temp = list[i]; list[i] = list[smallest]; list[smallest] = temp;
        percolateDown(list, lastIdx, smallest);
    }
}

private void percolateUp(ListNode[] list, int i){
    if(i == 0) return;
    int parent = (i - 1) / 2;
    if (val(list[parent]) <= val(list[i])) return;
    var temp = list[i]; list[i] = list[parent]; list[parent] = temp;
    percolateUp(list, parent);
}

private ListNode pop(ListNode[] list, int lastIdx){
    var minList = list[0];
    if (lastIdx == 0) { list[0] = null; return minList; }
    list[0] = list[lastIdx];
    list[lastIdx] = null;
    percolateDown(list, lastIdx-1, 0);
    return minList;
}

private void push(ListNode[] list, int lastIdx, ListNode newList){
    list[lastIdx] = newList;
    percolateUp(list, lastIdx);
}

private void heapify(ListNode[] lists){
    int n = lists.length;
    for(int i=n/2-1; i>=0; i--) percolateDown(lists, lists.length-1, i);
}

@Override
public ListNode mergeKLists(ListNode[] lists) {
    lists = java.util.Arrays.stream(lists)
            .filter(java.util.Objects::nonNull)
            .toArray(ListNode[]::new);
    int lastIdx = lists.length-1;
    heapify(lists);

    ListNode head = null, prev = null;
    while(lastIdx >= 0){
        var minList = pop(lists, lastIdx);
        var temp = new ListNode(minList.val);
        if(prev != null){ prev.next = temp; prev = temp; }
        else { head = temp; prev = temp; }

        if(minList.next == null) lastIdx--;
        else push(lists, lastIdx, minList.next);
    }
    return head;
}
```

Null input lists are filtered out up front so every slot of the working
array always holds a real (non-null) list head while it is "in the heap."
`val()`'s `Integer.MAX_VALUE` sentinel for `null` is defensive: by
construction, every index in `[0, lastIdx]` holds a non-null node for the
duration of any `percolateUp`/`percolateDown` call, so the sentinel is
never actually exercised — but it guards against exactly the class of bug
it looks like it's guarding against.

**Complexity:** Time `O(N log k)` — `heapify` is `O(k)`, and each of the
`N` total nodes is popped and (unless it was a list's last node) pushed
back exactly once, each operation costing `O(log k)` for the sift. Space
`O(k)` for the heap — but note it is stored *in place* inside the (already
allocated, nulls-filtered) `lists` array, so no additional array is
allocated; the only extra space is the `O(log k)` recursion stack depth of
`percolateUp`/`percolateDown` and the `O(N)` newly-allocated output nodes
(required regardless of approach, since the problem doesn't ask for
in-place relinking).

---

### Correctness Proof

**Setup.** Let `lists[0..k-1]` be the (null-filtered) input heads, and let
`N` be the total number of nodes across all lists. At any point during
execution, define the *frontier* as the multiset of node references
currently reachable as "the next unconsumed node of some list" — i.e.
whichever node of each not-yet-exhausted list has not yet been emitted to
the output.

#### Lemma 1 — `percolateDown`/`percolateUp` restore the min-heap property

**Min-heap property (invariant H):** for an array region `list[0..lastIdx]`,
every index `i` with a child `c ∈ {2i+1, 2i+2}` satisfying `c <= lastIdx`
has `val(list[i]) <= val(list[c])`.

*`percolateDown(list, lastIdx, i)` restores H at `i`, given H already holds
everywhere in `[0, lastIdx]` except possibly between `i` and its children:*
proof by strong induction on the height of the subtree rooted at `i`. Base
case: `i` is a leaf (no children `<= lastIdx`) — H holds vacuously. Inductive
step: the function identifies the smallest of `list[i]` and its in-range
children; if `i` is already smallest, H holds at `i` and the function stops
(both children's subtrees already satisfy H by assumption). Otherwise it
swaps `list[i]` with the smaller child `list[smallest]` — now `i` holds the
smaller value, satisfying H locally — and recurses into `smallest`, whose
subtree now has the *old* `list[i]` value possibly violating H, but is
strictly shorter (subtree of `i`'s child), so by the induction hypothesis
the recursive call restores H there too. ∎

*`percolateUp(list, i)` restores H along the path from `i` to the root,
given H holds everywhere except possibly between `i` and its parent:*
proof by induction on `i`'s depth. Base case `i = 0`: no parent, nothing to
do, H trivially holds (root has no parent constraint to violate). Inductive
step: if `val(list[parent]) <= val(list[i])`, H already holds at this edge
and the function stops. Otherwise it swaps `list[i]` and `list[parent]`,
restoring H at that edge, and recurses on `parent`'s new position, which by
the induction hypothesis (shallower depth) restores H the rest of the way
to the root. ∎

#### Lemma 2 — `pop` removes exactly the global minimum and preserves H on the remaining `lastIdx` elements

By invariant H, `list[0]` holds the smallest value in `list[0..lastIdx]`
(the heap root is always the minimum — a direct consequence of H applied
transitively from the root down any path). `pop` saves this value as
`minList`, then (if `lastIdx > 0`) moves `list[lastIdx]` into the root and
nulls out the vacated slot, shrinking the active region to
`list[0..lastIdx-1]`. This may violate H only along paths from the root
(the newly-placed element may be too large for its position), which is
exactly the precondition `percolateDown` requires; by Lemma 1, the
subsequent `percolateDown(list, lastIdx-1, 0)` call restores H everywhere
in the shrunk region. Hence `pop` returns the minimum and leaves a valid
`(lastIdx-1)`-sized min-heap (or, if `lastIdx == 0`, correctly empties it).
∎

#### Lemma 3 — `push` inserts a new element and restores H on the grown region

`push(list, lastIdx, newList)` places `newList` at the (previously vacant,
one-past-the-old-active-region) index `lastIdx`, growing the active region
to `list[0..lastIdx]`. This can only violate H along the single path from
`lastIdx` up to the root (every other H constraint among `[0, lastIdx-1]`
is untouched), which is exactly `percolateUp`'s precondition; by Lemma 1 it
restores H the rest of the way. ∎

#### Loop Invariant for `mergeKLists`

**Invariant I**, true before each iteration of the `while` loop:

1. `lists[0..lastIdx]` is a valid min-heap (H holds) whose values are
   exactly the frontier — one entry per list that still has unconsumed
   nodes — and `lastIdx + 1` equals the number of such lists.
2. The output chain built so far (`head .. prev`) is precisely the sorted
   merge of every node emitted so far, and every value in it is `<=`
   every value currently in the frontier.

*Initialization:* After filtering nulls and calling `heapify` (whose
correctness is Lemma 1 applied bottom-up — a standard build-heap
argument), `lists[0..lastIdx]` contains exactly one head per input list,
satisfying H; the output chain is empty, so part 2 holds vacuously.

*Maintenance:* Assume I holds before an iteration with current `lastIdx`.
By Lemma 2, `pop` returns `minList = ` the global minimum of the frontier
and restores H on the remaining `lastIdx` elements (now representing all
frontier lists except `minList`'s list). Since `minList.val` is the
minimum of the *entire* current frontier, and by I.2 every value already
in the output is `<=` every old frontier value, appending `minList.val` to
the output keeps the output sorted and keeps "output `<=` remaining
frontier" true for whatever remains. Then:
   - If `minList.next == null`, that list is now exhausted; decrementing
     `lastIdx` correctly shrinks the active heap region to match the new,
     smaller frontier (one fewer list) — H already holds there by Lemma 2.
   - Otherwise, `minList.next` is the new frontier element for that same
     list; `push` (Lemma 3) inserts it at the vacated top-of-region slot
     and restores H, so `lists[0..lastIdx]` (same `lastIdx` as before this
     iteration, since one element was removed and one re-added) is again
     exactly the frontier.

   Either way, I holds again before the next iteration.

*Termination:* `lastIdx` starts at `k' - 1` (`k'` = number of non-null
input lists) and strictly decreases by exactly 1 every time a list is
exhausted; since every list has finitely many nodes, every list is
eventually exhausted, so the loop runs exactly `N` times (once per node
across all lists) and terminates with `lastIdx = -1`.

**Conclusion.** Upon termination (`lastIdx < 0`, i.e. the frontier is
empty), by invariant I.2 the output chain contains every node's value in
sorted order — exactly the required merged list. ∎

---

## Worked Example: `lists = [[1,4,5],[1,3,4],[2,6]]`

After `heapify`, the three list heads `1, 1, 2` already satisfy H (any
arrangement with the two `1`s and the `2` in valid parent/child positions
works — say array `[1a, 1b, 2]` where `1a` is `lists[0]`'s head and `1b` is
`lists[1]`'s head).

| Step | Popped (`minList.val`) | Output so far | Action | New frontier |
|------|------------------------|----------------|--------|--------------|
| 1 | `1a` (from `[1,4,5]`) | `1` | push `4` (its successor) | `{4, 1b, 2}` |
| 2 | `1b` (from `[1,3,4]`) | `1,1` | push `3` | `{4, 3, 2}` |
| 3 | `2` (from `[2,6]`) | `1,1,2` | push `6` | `{4, 3, 6}` |
| 4 | `3` | `1,1,2,3` | push `4` | `{4, 4, 6}` |
| 5 | `4` (from list 1) | `1,1,2,3,4` | push `5` | `{5, 4, 6}` |
| 6 | `4` (from list 2, exhausted) | `1,1,2,3,4,4` | `lastIdx--` | `{5, 6}` |
| 7 | `5` (exhausted) | `1,1,2,3,4,4,5` | `lastIdx--` | `{6}` |
| 8 | `6` (exhausted) | `1,1,2,3,4,4,5,6` | `lastIdx--` → `-1`, loop ends | `{}` |

Final output: `[1,1,2,3,4,4,5,6]` — matches the expected answer.

---

## Optimization Journey

```
Brute Force (Collect, Sort, Relink) ────────────────────────────────────►
       Ignore existing sortedness entirely; dump everything and sort.
       Time: O(N log N)   Space: O(N)

           ▼  Observation: each list is already sorted — a full sort
              redoes work merge-based approaches get for free.

Sequential Pairwise Merge ───────────────────────────────────────────────►
       Fold lists in one at a time with the classic two-pointer merge.
       Time: O(N·k)   Space: O(1)

           ▼  Observation: late merges re-scan an output that has grown
              to nearly all N nodes, k times over. Processing all k lists
              "in parallel," one round at a time, avoids re-scanning the
              same already-merged nodes repeatedly.

Divide and Conquer Pairwise Merge ───────────────────────────────────────►
       Merge pairs of lists, then pairs of results, ⌈log k⌉ rounds total.
       Time: O(N log k)   Space: O(log k)

           ▼  Observation: rounds are just a way to repeatedly find "the
              smallest of the current candidates." A size-k min-heap does
              this directly and incrementally — no need to wait for a
              full round to finish before making progress — at the cost
              of O(k) heap storage instead of O(log k) recursion depth.

Min-Heap of List Heads (MergeKSortedListsImpl) ──────────────────────────►
       Maintain a k-sized min-heap of current list heads; repeatedly pop
       the global minimum, emit it, and push its successor.
       Time: O(N log k)   Space: O(k) (heap stored in place in `lists`)
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Brute Force (Collect, Sort, Relink) | O(N log N) | O(N) | Ignores pre-sortedness of input lists |
| Sequential Pairwise Merge | O(N·k) | O(1) | Simplest to implement; poor for large `k` |
| Divide and Conquer Pairwise Merge | O(N log k) | O(log k) | Matches asymptotic optimum with minimal space |
| Min-Heap of List Heads | O(N log k) | O(k) | Implemented by `MergeKSortedListsImpl`; heap reuses the input array in place |

> **Best solution:** The min-heap approach and divide-and-conquer are both
> asymptotically optimal at `O(N log k)` time. `MergeKSortedListsImpl`'s
> hand-rolled array-based heap achieves this without allocating any extra
> array for the heap itself (it operates directly on the filtered `lists`
> array), trading the divide-and-conquer approach's slightly smaller
> `O(log k)` space for a single-pass, incremental algorithm that never
> needs to wait for a "round" to complete before making progress.
