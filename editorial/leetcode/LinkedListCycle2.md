# Linked List Cycle II

## Problem Statement

Given the `head` of a linked list, return **the node where the cycle
begins**. If there is no cycle, return `null`.

There is a cycle in a linked list if some node in the list can be reached
again by continuously following the `next` pointer. Internally, this is
often represented by an index `pos` (not passed to the function) denoting
the node that the tail connects to, to form a cycle. `pos` is `-1` if there
is no cycle.

**Example:**
```
head = [3, 2, 0, -4], pos = 1
Answer = the node with value 2       // tail connects to index 1

head = [1, 2], pos = 0
Answer = the node with value 1       // tail connects to index 0

head = [1], pos = -1
Answer = null                        // no cycle
```

**Follow-up:** Can you solve it using `O(1)` (i.e. constant) memory?

This is a direct extension of LeetCode 141 "Linked List Cycle," which only
asks *whether* a cycle exists. Here we must additionally pinpoint *where*
it begins, which requires either extra memory to remember visited nodes, or
a purely arithmetic trick once a cycle has been detected.

---

## Solutions

### 1. HashSet of Visited Nodes — O(n) Time, O(n) Space

Walk the list, recording every visited node reference in a `HashSet`. The
first node seen twice is the cycle's start (since it's the first node
re-reachable by following `next`, and all nodes before the cycle are
visited exactly once).

```java
class HashSetSolution implements LinkedListCycle2 {
    @Override
    public ListNode detectCycle(ListNode head) {
        var seen = new HashSet<ListNode>();
        var current = head;

        while (current != null) {
            if (!seen.add(current)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }
}
```

**Complexity:** Time `O(n)`, Space `O(n)` for the visited set.

**Drawback:** Correct and simple, but the `O(n)` auxiliary set is exactly
what the problem's follow-up asks us to eliminate. Two pointers moving at
different speeds can detect a cycle without remembering any node, trading
the hash set for pure pointer arithmetic.

---

### 2. Optimized Solution (Floyd's Cycle Detection / Tortoise and Hare) — O(n) Time, O(1) Space

**Key Insight:** Run a slow pointer (`+1` node per step) and a fast pointer
(`+2` nodes per step). If there's a cycle, they are guaranteed to meet
somewhere inside it. Once they meet, resetting one pointer to `head` and
advancing both one step at a time — now at equal speed — makes them meet
again *exactly at the cycle's start*. This is not a coincidence: it's a
direct consequence of the distance relationships derived below.

```java
class LinkedListCycle2Impl implements LinkedListCycle2 {
    @Override
    public ListNode detectCycle(ListNode head) {
        var slow = head;
        var fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        if (fast == null || fast.next == null) return null;

        while (head != slow) {
            head = head.next;
            slow = slow.next;
        }
        return head;
    }
}
```

**Complexity:** Time `O(n)` (each phase traverses at most `O(n)` nodes),
Space `O(1)` extra — only two or three pointers are ever held, regardless
of list length.

---

### Correctness Proof

**Setup.** Let `a` be the number of nodes strictly before the cycle begins
(the "tail" length), and let `c` be the cycle's length (`c >= 1`). Index
nodes along the path starting at `head = node(0)`; nodes `node(a), node(a+1),
..., node(a+c-1)` form the cycle, with `node(a+c)` identified with
`node(a)`.

**Lemma 1 (Phase 1 terminates with a meeting point, if and only if there is
a cycle).**
If there is no cycle, `fast` (or `fast.next`) reaches `null` in a finite
number of steps, since the list is then a finite acyclic chain and `fast`
strictly outpaces the list's remaining length. If there **is** a cycle,
`fast` and `slow` both eventually enter it and never leave; once both are
inside the cycle, consider the pointer gap `g = (fast's position - slow's
position) mod c`. Each step changes `g` by `-1` (mod `c`), since `fast`
advances 2 and `slow` advances 1, a net gap closure of 1 per iteration.
Since `g` decreases by exactly 1 each step (mod `c`), it must hit `0` within
at most `c` steps after both pointers are in the cycle — i.e. `slow` and
`fast` **must** meet. ∎

**Lemma 2 (Meeting-point distance relationship).**
Suppose a cycle exists and `slow`/`fast` meet after `slow` has taken `k`
steps from `head` (so `fast` has taken `2k` steps). Since `slow` has moved
`k` nodes and the first `a` of those are the tail, `slow`'s position inside
the cycle (distance travelled past the cycle start) is `(k - a) mod c`.
Likewise `fast`'s position inside the cycle is `(2k - a) mod c`. Because
they meet, these are equal mod `c`:
```
(k - a) ≡ (2k - a)  (mod c)
       0 ≡ k         (mod c)
```
So `k` is a multiple of `c`: `k = m·c` for some integer `m >= 1` (`m >= 1`
because the pointers only meet after entering the cycle, which requires at
least reaching index `a`, and further bookkeeping shows `k >= c`; in any
case `k ≡ 0 (mod c)` is the fact that matters below).

**Theorem (Phase 2 correctness — the second meeting point is the cycle
start).**
After Phase 1, `slow` is at distance `k` from `head` with `k ≡ 0 (mod c)`.
Phase 2 resets one pointer to `head` (distance `0` from `head`) and advances
both pointers one step at a time. After `a` further steps:
- The pointer that started at `head` has moved exactly `a` steps, landing
  exactly at `node(a)` — the cycle's start, by definition of `a`.
- The pointer that started at the meeting point (distance `k` from `head`)
  has moved to distance `k + a` from `head`. Its position *within the
  cycle* is `(k + a - a) mod c = k mod c = 0` (using `k ≡ 0 (mod c)` from
  Lemma 2) — i.e. it is also exactly at the cycle's start `node(a)`.

Since both pointers reach `node(a)` after exactly `a` additional steps (and
neither can have met earlier at a *different* node without contradicting
the modular arithmetic above, as both positions are congruent to `node(a)`
only at multiples of `a` steps into Phase 2 matching this derivation), the
first node at which `head == slow` in the second loop is exactly the cycle
start. ∎

**Theorem (Overall correctness).**
`LinkedListCycle2Impl.detectCycle` returns the cycle's start node whenever
one exists, and `null` otherwise.

*Proof.* If the list is acyclic, by Lemma 1 `fast` or `fast.next` becomes
`null`, so the method returns `null` directly — correct, since there is no
cycle start to report. If the list has a cycle, by Lemma 1 the first loop
terminates via `slow == fast` (not via `fast` reaching `null`, since `fast`
never leaves the cycle once inside it), so the guard
`fast == null || fast.next == null` is false and control proceeds to Phase
2. By the Theorem above, Phase 2's loop terminates exactly when `head`
reaches `node(a)`, the true cycle start, which is then returned. ∎

---

## Optimization Journey

```
HashSet of Visited Nodes ───────────────────────────────────────────────►
       Track every visited node reference; the first repeat is the answer.
       Time: O(n)    Space: O(n)   ← one set entry per node

           ▼  Observation: cycle membership can be detected without
              remembering nodes at all — a fast pointer moving twice as
              fast as a slow one is forced to lap it inside any cycle
              (Lemma 1), and the meeting point's modular distance from the
              cycle start (Lemma 2) lets a second synchronized walk locate
              the start exactly, with no extra memory.

Floyd's Cycle Detection (Tortoise and Hare) ────────────────────────────►
       Phase 1 detects a meeting point inside the cycle (if any) using two
       pointers at different speeds. Phase 2 resets one pointer to head and
       walks both at equal speed until they coincide, which happens exactly
       at the cycle's start.
       Time: O(n)    Space: O(1) extra
```

---

## Complexity Summary

| Solution | Time | Space | Notes |
|----------|------|-------|-------|
| HashSet of Visited Nodes | O(n) | O(n) | First repeated node reference is the cycle start |
| Floyd's Cycle Detection (Tortoise and Hare) | O(n) | O(1) | Two-phase pointer walk; implemented by `LinkedListCycle2Impl` |

> **Best solution:** `LinkedListCycle2Impl` (Floyd's Cycle Detection) —
> linear time and constant extra space, satisfying the problem's `O(1)`
> memory follow-up. Correctness rests on two facts proven above: a
> fast/slow pointer pair must meet inside any cycle (Lemma 1), and the
> meeting point's distance from `head` is congruent to the cycle length
> modulo the cycle length (Lemma 2) — which is exactly what makes resetting
> one pointer to `head` and walking both at equal speed converge precisely
> at the cycle's start (the Theorem above).
