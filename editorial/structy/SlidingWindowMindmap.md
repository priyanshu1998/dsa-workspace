# Sliding Window Problems – Mindmap

> Nodes = problems · Edges = "slight variation of" relationships.
> Problems are coloured by **window type**: 🟦 fixed-size window, 🟩 variable-size window.

```mermaid
mindmap
  root((Sliding Window))
    Fixed Size Window
      MaximumSubarraySum
        SubarrayTargetSumSizeK
        MaximumSubarrayProduct
        HasSubstringAnagram
          CountSubstringAnagrams
    Variable Size Window
      FindSubarraySum
        LongestSubarraySum
        CountSubarrayProduct
      LongestUniqueSubstring
        LongestTwoCharSubstring
          CountSubstringAtMostKDistinct
            CountExactlyKDistinct
        MaxOnesWithSingleFlip
```

---

## Relationship Graph (detailed edges)

```mermaid
graph LR
    classDef fixed fill:#d0e0ff,stroke:#3366cc
    classDef variable fill:#d0ffd0,stroke:#33aa33

    MSS["MaximumSubarraySum\n(fixed k, max sum)"]:::fixed
    MSP["MaximumSubarrayProduct\n(fixed k, max product)"]:::fixed
    STSK["SubarrayTargetSumSizeK\n(fixed k, count target)"]:::fixed
    HSA["HasSubstringAnagram\n(fixed k, bool anagram)"]:::fixed
    CSA["CountSubstringAnagrams\n(fixed k, count anagrams)"]:::fixed

    FSS["FindSubarraySum\n(var, find target sum)"]:::variable
    LSS["LongestSubarraySum\n(var, longest target sum)"]:::variable
    CSP["CountSubarrayProduct\n(var, count product < t)"]:::variable
    LUS["LongestUniqueSubstring\n(var, longest all-unique)"]:::variable
    LTCS["LongestTwoCharSubstring\n(var, longest 2-distinct)"]:::variable
    CSAKD["CountSubstringAtMostKDistinct\n(var, count ≤k distinct)"]:::variable
    CEKD["CountExactlyKDistinct\n(var, count =k distinct)"]:::variable
    MOSF["MaxOnesWithSingleFlip\n(var, longest ≤1 zero)"]:::variable

    %% ── Fixed-window variations ──
    MSS -- "sum → product" --> MSP
    MSS -- "max → count target" --> STSK
    MSS -- "sum → freq-match" --> HSA
    HSA -- "bool → count" --> CSA

    %% ── Variable-window sum / product variations ──
    FSS -- "find first → find longest" --> LSS
    FSS -- "sum → product,\nfind → count" --> CSP

    %% ── Variable-window distinct / constraint variations ──
    LUS -- "all-unique → exactly 2\n(generalize constraint)" --> LTCS
    LTCS -- "longest → count,\n2 → k" --> CSAKD
    CSAKD -- "atMost(k) − atMost(k−1)" --> CEKD
    LUS -- "unique chars → at-most-one-zero\n(same longest + constraint)" --> MOSF

    %% ── Cross-group bridges ──
    MSS -. "fixed → variable\n(drop fixed k)" .-> FSS
    STSK -. "fixed → variable\n(drop fixed k)" .-> FSS
    CSP -. "product counting\n(var ↔ fixed)" .-> MSP
```

---

## Legend

| Colour | Meaning |
|--------|---------|
| 🟦 Blue | **Fixed-size** sliding window (window length `k` is given) |
| 🟩 Green | **Variable-size** sliding window (window shrinks/grows by constraint) |
| **Solid arrow** | Direct, slight variation |
| **Dashed arrow** | Cross-group bridge (fixed ↔ variable) |

---

## Variation Summary

| From → To | What changes |
|-----------|--------------|
| MaximumSubarraySum → MaximumSubarrayProduct | aggregation: **sum → product** |
| MaximumSubarraySum → SubarrayTargetSumSizeK | objective: **maximize → count matches** |
| MaximumSubarraySum → HasSubstringAnagram | window check: **sum → frequency-match** |
| HasSubstringAnagram → CountSubstringAnagrams | output: **boolean → count** |
| FindSubarraySum → LongestSubarraySum | objective: **find any → find longest** |
| FindSubarraySum → CountSubarrayProduct | aggregation: **sum → product**, objective: **find → count** |
| LongestUniqueSubstring → LongestTwoCharSubstring | constraint: **all unique → exactly 2 distinct** |
| LongestUniqueSubstring → MaxOnesWithSingleFlip | constraint: **all unique chars → at most one zero** (same "longest window with constraint" pattern) |
| LongestTwoCharSubstring → CountSubstringAtMostKDistinct | objective: **longest → count**, constraint: **2 → k** |
| CountSubstringAtMostKDistinct → CountExactlyKDistinct | trick: **atMost(k) − atMost(k−1)** reuses the same core |
| MaximumSubarraySum → FindSubarraySum | window type: **fixed → variable** (remove fixed `k`) |
| CountSubarrayProduct ↔ MaximumSubarrayProduct | window type: **variable ↔ fixed**, both use product |

