---
name: dsa-correctness-proof
description: Formally prove algorithmic correctness, loop invariants, termination, and time/space complexity for DSA implementations.
---

# Formal Algorithmic Proof Protocol

When asked to prove or verify an algorithm's correctness, follow these mandatory steps:

1. **Loop Invariants (for Iterative Algorithms)**
    - **Initialization:** Prove the invariant holds prior to the first iteration.
    - **Maintenance:** Prove that if the invariant holds before iteration `k`, it holds before iteration `k+1`.
    - **Termination:** Show that upon termination, the invariant yields the exact goal property.

2. **Induction (for Recursive / Divide-and-Conquer)**
    - Define base cases clearly and prove correctness for `n = 0` or `n = 1`.
    - Formulate the inductive hypothesis `P(k)` and prove `P(k) ⟹ P(k+1)` or `P(<k) ⟹ P(k)`.

3. **Termination & Decreasing Variant**
    - Identify a strict well-founded ordering or non-negative integer measure function `f(x)` that strictly decreases (`f(x[k+1]) < f(x[k])`) at each step.

4. **Amortized Analysis (if applicable)**
    - Define an explicit potential function `Φ(D_i) ≥ Φ(D_0)` and calculate amortized cost `ĉ_i = c_i + Φ(D_i) - Φ(D_{i-1})`.

## Repository Layout Conventions

- For a problem `<Prob>`, the solution lives in a `<Prob>Impl` class (e.g. `src/main/.../<Prob>Impl.java`).
- Corresponding tests live in `<Prob>Test.java` under the mirrored path in `src/test/...`.
- Editorials (write-ups covering the above proof steps) are saved as `<Prob>.md` in the matching `editorial/<platform>/` folder (e.g. `editorial/leetcode/`, `editorial/structy/`).

## Test Execution Policy

- If running tests, always run only the specific `<Prob>Test.java` for the problem being worked on. Never run the entire test suite.