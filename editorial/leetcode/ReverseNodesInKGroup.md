# Reverse Nodes in k-Group

## Problem Statement

Given the `head` of a linked list, reverse the nodes of the list `k` at a
time, and return the modified list.

`k` is a positive integer and is less than or equal to the length of the
linked list. If the number of nodes is not a multiple of `k`, then the
nodes left out at the end should remain as-is (i.e. the trailing partial
group is **not** reversed).

**Example:**
```
head = [1, 2, 3, 4, 5], k = 2
Answer = [2, 1, 4, 3, 5]      // groups of 2 reversed; lone node 5 untouched

head = [1, 2, 3, 4, 5], k = 3
Answer = [3, 2, 1, 4, 5]      // first group of 3 reversed; [4,5] has < k nodes, left as-is
```

**Follow-up:** Can you solve the problem in `O(1)` extra memory space? You
may not alter the values in the list's nodes, only the nodes themselves may
be changed.

This generalizes the classic "reverse a linked list" problem: instead of
reversing the whole list, we reverse it in fixed-size chunks, leaving any
final undersized chunk untouched. The constant-memory follow-up pushes
toward an *iterative* group-by-group reversal; the implementation here
instead favors a clean *recursive* decomposition, trading `O(1)` auxiliary
space for recursion-stack space in exchange for a much simpler proof of
correctness.

---

## Solutions

### 1. Iterative, Group-by-Group Reversal — O(n) Time, O(1) Space

Walk the list with a dummy node and a `groupPrev` pointer. For each group:
check that `k` nodes remain (otherwise stop); reverse exactly those `k`
nodes in place using the standard three-pointer (`prev`/`curr`/`next`)
in-place reversal; relink `groupPrev` to the new group head and the old
group head to the next group; advance `groupPrev` to the old group head
(now the group's tail).

```java
class IterativeSolution implements ReverseNodesInKGroup {
    @Override
    public ListNode reverseKGroup(ListNode head, int k) {
        var dummy = new ListNode(0, head);
        var groupPrev = dummy;

        while (true) {
            var kth = groupPrev;
            for (int i = 0; i < k && kth != null; i++) kth = kth.next;
            if (kth == null) break; // fewer than k nodes remain

            var groupNext = kth.next;
            var prev = groupNext;
            var curr = groupPrev.next;
            while (curr != groupNext) {
                var tmp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = tmp;
            }

            var tmp = groupPrev.next;
            groupPrev.next = kth;
            groupPrev = tmp;
        }
        return dummy.next;
    }
}
```

**Complexity:** Time `O(n)` — every node is visited a constant number of
times across the scan and the reversal. Space `O(1)` extra — only a
handful of pointers are held, satisfying the follow-up exactly.

**Trade-off:** Fully meets the memory constraint, but the pointer
bookkeeping (three nested roles — scanning ahead, reversing in place,
relinking the previous group) is fiddly to get right and harder to reason
about directly.

---

### 2. Recursive, Isolate-and-Reverse (implemented in `ReverseNodesInKGroupImpl`) — O(n) Time, O(n/k + k) Space

**Key Insight:** If you can (a) find the `k`-th node of a chain, (b)
*reverse a self-contained, null-terminated chain* and attach its new tail
to whatever comes next, and (c) recursively solve "everything after this
group" first, then stitching one group onto the correctly-processed
remainder is almost free.

```java
private ListNode reverseListHelper(ListNode current, ListNode successor) {
    if (successor == null) {
        return current;
    }
    var newHead = reverseListHelper(current.next, successor.next);
    successor.next = current;
    return newHead;
}

public ListNode reverseList(ListNode head, ListNode nextHead) {
    if (head == null) return null;
    var newHead = reverseListHelper(head, head.next);
    head.next = nextHead;
    return newHead;
}

private ListNode kthSuccessor(ListNode node, int k) {
    var curr = node;
    for (int i = 0; i < k; i++) {
        if (curr == null) return null;
        curr = curr.next;
    }
    return curr;
}

@Override
public ListNode reverseKGroup(ListNode head, int i) {
    if (head == null || i == 1) return head;
    var tail = kthSuccessor(head, i - 1);

    // Not enough nodes for a complete group.
    if (tail == null) return head;

    var nextHead = tail.next;
    tail.next = null;

    var newNextHead = reverseKGroup(nextHead, i); // last k Group is first reversed
    return reverseList(head, newNextHead);        // actual reversal, last k group can contain lesser than k elements
}
```

`kthSuccessor` locates the `k`-th node ahead of `head` (or `null` if the
list is too short — that's how a trailing partial group is detected).
`tail.next = null` physically **isolates** that group into its own finite,
null-terminated chain, which is exactly what `reverseListHelper`'s base
case (`successor == null`) requires in order to terminate. The recursive
call processes the remainder *first* (so by the time we reverse the
current group, we already know what it must point to), and `reverseList`
reverses the isolated group and wires its new tail to that already-solved
remainder.

**Complexity:** Time `O(n)` — `kthSuccessor` and `reverseList` each do
`O(k)` work per group, over `O(n / k)` groups. Space `O(n/k + k)` — the
`reverseKGroup` recursion is `O(n/k)` deep (one frame per group, all
pending while the remainder is solved), and on top of that, `reverseList`
transiently adds up to `O(k)` more stack frames per group while reversing
it. In the worst case (`k = 1` or `k = n`) this degrades to `O(n)`.

---

### Correctness Proof

**Setup.** Fix `k >= 1`. For a (possibly empty) finite chain of nodes
`n0 -> n1 -> ... -> n_{m-1} -> null`, "reversing it in groups of `k`" means:
reverse `n0..n_{k-1}`, then `n_k..n_{2k-1}`, etc., and leave untouched
whatever trailing block has fewer than `k` nodes, all while preserving the
left-to-right order of the (now internally reversed) blocks.

#### Lemma 1 — `kthSuccessor(node, m)` returns the `m`-th successor, or `null`

**Loop invariant:** before the `i`-th iteration of the `for` loop (and
assuming no prior iteration has already returned), `curr` equals the
`i`-th successor of the original `node`.

- *Initialization* (`i = 0`): `curr = node`, which is trivially the `0`-th
  successor.
- *Maintenance*: assume `curr` is the `i`-th successor before iteration
  `i`. If `curr == null`, the `i`-th successor doesn't exist, so the
  `(i+1)`-th doesn't either — returning `null` is correct. Otherwise
  `curr = curr.next` sets `curr` to the `(i+1)`-th successor, restoring the
  invariant for iteration `i+1`.
- *Termination*: the loop runs exactly `k` times unless it returns early.
  If it completes all `k` iterations, by the invariant `curr` is the
  `k`-th successor of `node`, which is returned. If it returns early, no
  `k`-th successor exists. ∎

#### Lemma 2 — `reverseListHelper` reverses a finite chain, leaving only the head's `.next` unpatched

**Claim:** For any finite chain `n0 -> n1 -> ... -> n_{m-1} -> null`
(`m >= 1`), calling `reverseListHelper(n0, n0.next)` returns `n_{m-1}`, and
afterward `n_j.next == n_{j-1}` for every `j` in `1..m-1`, while `n0.next`
is left completely unmodified by the call.

*Proof by induction on `m`.*

- **Base case (`m = 1`):** the chain is just `n0 -> null`, so
  `successor = n0.next = null`. The function immediately returns
  `current = n0` without touching anything — matching the claim (no links
  to reverse, `n0.next` untouched). ✓

- **Inductive step:** assume the claim for chains of length `m`; consider
  one of length `m + 1`: `n0 -> n1 -> ... -> n_m -> null`. Here
  `successor = n1`. The call recurses into
  `reverseListHelper(n1, n1.next)`, which — by the induction hypothesis
  applied to the sub-chain `n1 -> ... -> n_m -> null` of length `m` —
  returns `n_m`, reverses every link `n_j.next = n_{j-1}` for `j` in
  `2..m`, and leaves `n1.next` untouched (still pointing at `n2`). Back in
  the outer frame, the statement `successor.next = current` executes
  `n1.next = n0`, patching exactly the one link the inner call didn't
  touch. The function then returns the inner result, `n_m`.
  Checking the postcondition for length `m + 1`: the returned head is
  `n_m` ✓; every `n_j.next = n_{j-1}` for `j` in `1..m` now holds (`j=1`
  just patched, `j in 2..m` from the IH) ✓; and `n0.next` — the outer
  frame's `current.next` — is never assigned anywhere in this call (only
  `successor.next` is ever written, and `n0` never plays the role of
  `successor` here), so it remains unmodified ✓. ∎

#### Lemma 3 — `reverseList(head, nextHead)` reverses an isolated, finite group and re-attaches it

If the chain starting at `head` is finite (terminates in `null` — which
`reverseKGroup` guarantees by setting `tail.next = null` beforehand), then
`reverseList(head, nextHead)` returns the new head of that reversed chain
(the original last node), with all internal links reversed per Lemma 2,
and with the original `head`'s `.next` explicitly set to `nextHead` —
patching the one link Lemma 2 deliberately leaves alone. The `head == null`
guard handles the empty case correctly (nothing to reverse). ∎

#### Theorem — `reverseKGroup(head, k)` is correct

**Claim:** for every finite list and every `k >= 1`, `reverseKGroup`
returns the head of the list reversed in groups of `k`, per the setup
above.

*Proof by strong induction on the list length `n` reachable from `head`.*

- **Base cases.** If `n = 0` (`head == null`), the function returns `null`
  immediately — correct, nothing to reverse. If `k = 1`, every "group" has
  exactly one node, so group-reversal is the identity transformation; the
  function special-cases this and returns `head` unchanged, which matches
  the specification (this is an optimization, not a correctness
  necessity, but it is what the code does, so it belongs in the base
  case).

- **Inductive step.** Assume the claim holds for every list shorter than
  `n`. Take a list of length `n >= 1` with `k >= 2`. By Lemma 1,
  `tail = kthSuccessor(head, k - 1)` is exactly the `k`-th node from
  `head` if at least `k` nodes exist, else `null`.

  - *Case `tail == null`* (fewer than `k` nodes remain): the function
    returns `head` unmodified — no node was mutated prior to this check,
    so this is exactly the untouched trailing partial group required by
    the specification. ✓

  - *Case `tail != null`*: let `nextHead = tail.next` (start of the
    remainder, a list of length `n - k`). Setting `tail.next = null`
    isolates `head..tail` into a finite, `k`-node chain, satisfying
    Lemma 3's precondition. The recursive call
    `reverseKGroup(nextHead, k)` operates on a strictly shorter list
    (length `n - k < n`), so by the induction hypothesis it correctly
    returns the head of the remainder reversed in groups of `k` — call it
    `R`. Finally, `reverseList(head, newNextHead)` (Lemma 3) reverses the
    isolated `k`-node chain in place and attaches its new tail's `.next`
    to `R`, returning the new head of this reversed group (the original
    `tail`).

    The overall return value is therefore: *[first `k` nodes, reversed]*
    followed by *R*, which is precisely "reverse the first group of `k`,
    then recursively apply the same rule to everything after it" — the
    definition of group-`k` reversal for a list of length `n`. ✓

  Both cases establish `P(n)` given `P(<n)`. ∎

**Termination.** Each recursive call to `reverseKGroup` is made on
`nextHead`, a list strictly shorter by at least `k >= 2` nodes than the
current one (the `k = 1` case never recurses). The list length is thus a
strictly decreasing, non-negative measure, so the recursion terminates
after at most `⌈n / k⌉` levels.

---

## Worked Example: `head = [1,2,3,4,5]`, `k = 3`

Trace of `ReverseNodesInKGroupImpl.reverseKGroup`, showing the call tree
and every pointer mutation, in the order they happen.

### Step 1 — Outer call: `reverseKGroup(head=1, i=3)`

- `head != null` and `i != 1`, so we proceed.
- `tail = kthSuccessor(1, i-1=2)`: walk 2 `.next` hops from `1` →
  `1 → 2 → 3`. **`tail = 3`.**
- `tail != null`, so this is a full group: `nextHead = tail.next = 4`.
- **Sever** the group: `tail.next = null`, i.e. `3.next = null`. The list
  is now split into two independent, finite chains:
  ```
  Group (to reverse):   1 -> 2 -> 3 -> null
  Remainder:             4 -> 5 -> null        (nextHead = 4)
  ```

### Step 2 — Recurse on the remainder first: `reverseKGroup(head=4, i=3)`

- `tail = kthSuccessor(4, 2)`: walk 2 hops from `4` → `4 → 5 → null`.
  The second hop lands on `null`, so **`tail = null`**.
- Fewer than `k` nodes remain, so this call returns `head` unchanged:
  **returns `4`** (chain `4 -> 5 -> null` is left untouched).
- Back in Step 1: **`newNextHead = 4`**.

### Step 3 — Reverse the isolated group: `reverseList(head=1, nextHead=4)`

`reverseList` calls `reverseListHelper(current=1, successor=1.next=2)` on
the isolated chain `1 -> 2 -> 3 -> null`. This recurses all the way to the
end *before* reversing any links (post-order), so the call tree and the
link mutations on the way back up are:

| Frame | Call | Action taken on the way back up | Chain state after this frame returns |
|-------|------|----------------------------------|----------------------------------------|
| C (deepest) | `reverseListHelper(3, null)` | `successor == null` → just return `current = 3`, no mutation | `1 -> 2 -> 3 -> null` (unchanged) |
| B | `reverseListHelper(2, 3)` | `3.next = 2` | `1 -> 2 <- 3`, with `2.next` still stale (`-> 1`... actually still `3`, not yet touched) — concretely: `3.next=2`, `2.next=3` (untouched so far), `1.next=2` (untouched so far) |
| A (outermost) | `reverseListHelper(1, 2)` | `2.next = 1` | `3.next=2`, `2.next=1`, `1.next=2` (stale — still points at its old successor) |

Each frame only ever writes `successor.next = current`; it never touches
`current.next`, which is exactly Lemma 2's guarantee. `reverseListHelper`
returns `newHead = 3` all the way up to `reverseList`.

After `reverseListHelper` returns, the group's internal links are fully
reversed (`3 -> 2 -> 1`), but node `1`'s `.next` is still its *stale*
original value (`2`) — Lemma 2 deliberately leaves it alone. `reverseList`
now patches exactly that link:

- `head.next = nextHead` → `1.next = 4`.
- Returns `newHead = 3`.

### Step 4 — Unwind back to the outer call

`reverseKGroup(head=1, i=3)` returns whatever `reverseList` returned: **`3`**.

### Final structure

```
3 -> 2 -> 1 -> 4 -> 5 -> null
```

which matches the expected output `[3, 2, 1, 4, 5]`: the first full group
`[1,2,3]` is reversed to `[3,2,1]`, and the trailing partial group `[4,5]`
(only 2 nodes, fewer than `k = 3`) is left in its original order and
relative position.

---

## Optimization Journey

```
Iterative, Group-by-Group Reversal ─────────────────────────────────────►
       Scan ahead k nodes to confirm a full group exists, reverse it with
       three raw pointers, relink the previous group's tail to the new
       group head, and advance.
       Time: O(n)    Space: O(1) extra   ← meets the follow-up exactly

           ▼  Observation: the pointer bookkeeping needed to reverse a
              group *in place* while remembering the previous group's
              tail is fiddly to verify directly. If instead each group is
              *physically severed* into its own finite, null-terminated
              chain, a single clean recursive reversal (the same one used
              for whole-list reversal) can handle it — and processing the
              remainder *before* reversing the current group means the
              correct "next" pointer is already known when it's needed.

Recursive, Isolate-and-Reverse (ReverseNodesInKGroupImpl) ──────────────►
       Find the k-th node, sever the group, recurse on everything after
       it, then reverse the isolated group and attach it to the already-
       solved remainder.
       Time: O(n)    Space: O(n/k + k)  ← trades O(1) space for a much
                                           simpler correctness argument
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| Iterative, Group-by-Group Reversal | O(n) | O(1) | Satisfies the problem's constant-memory follow-up |
| Recursive, Isolate-and-Reverse | O(n) | O(n/k + k) | Implemented by `ReverseNodesInKGroupImpl`; degrades to O(n) when `k = 1` or `k = n` |

> **Best solution (for the follow-up):** the iterative group-by-group
> reversal, since it is the only one meeting `O(1)` extra space.
> **Best solution (for clarity/proof simplicity):** `ReverseNodesInKGroupImpl`
> — isolating each group into its own finite chain (`tail.next = null`)
> reduces the whole problem to two already-understood primitives
> (find-the-k-th-node, reverse-a-finite-chain), which is exactly what
> makes Lemmas 1–3 and the inductive Theorem above straightforward to
> state and verify, at the cost of `O(n/k + k)` recursion-stack space
> instead of `O(1)`.
