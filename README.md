# DSA Workspace

A Java-based workspace for practicing Data Structures & Algorithms problems from [LeetCode](https://leetcode.com/), [Codeforces](https://codeforces.com/), [HackerRank](https://www.hackerrank.com/), and [Structy](https://structy.net/).

## Tech Stack

| Tool | Version / Details |
|------|------------------|
| Language | Java |
| Build | Gradle (Kotlin DSL) |
| Test | JUnit 5 |
| Formatter | [Spotless](https://github.com/diffplug/spotless) + Google Java Format |

## Repository Structure

```
dsa-workspace/
├── src/
│   ├── main/java/dev/priyanshu/
│   │   ├── annotation/       # Custom source annotations (@Leetcode, @Structy, @Category, …)
│   │   ├── enums/            # Shared enums (Difficulty, Concept)
│   │   ├── codeforces/       # Codeforces problem solutions
│   │   ├── hackerrank/       # HackerRank problem solutions
│   │   ├── leetcode/         # LeetCode problem solutions (organised by topic)
│   │   └── structy/          # Structy problem solutions
│   └── test/java/dev/priyanshu/
│       └── ...               # JUnit 5 tests mirroring the main source tree
└── editorial/
    ├── codeforces/           # Markdown editorials for Codeforces problems
    └── structy/              # Markdown editorials & mind-maps for Structy problems
```

### LeetCode Topics Covered

| Topic | Examples |
|-------|---------|
| Backtracking / DP | Best Time to Buy and Sell Stock II, Minimum ASCII Delete Sum |
| Binary Tree | Maximum Product of Split Binary Tree, Smallest Subtree with Deepest Nodes |
| Combination / Counting | Four Sum, Number of Ways to Paint N×3 Grid |
| Design | Online Stock Span |
| Geometry | Maximize Area of Square Hole in Grid |
| Math | Four Divisors |
| Monotonic Stack | Daily Temperatures, Largest Rectangle in Histogram, Maximal Rectangle, Next Greater Element II |
| Search (Binary) | Find Peak Element |
| Search (Linear) | Divide Array into Subarrays with Minimum Cost |
| String | Lexicographically Smallest String after Deleting Duplicates |
| Subarray | Maximum Good Subarray Sum, Max Sum of Distinct Subarrays of Length K |
| XOR | Decode XORed Permutation, N-Repeated Element in 2N Array, Number of Alternating XOR Partitions |

### Structy Problems Covered

All Structy problems are sliding-window focused:

| Problem | Window Type |
|---------|------------|
| MaximumSubarraySum | Fixed |
| MaximumSubarrayProduct | Fixed |
| SubarrayTargetSumSizeK | Fixed |
| HasSubstringAnagram | Fixed |
| CountSubstringAnagrams | Fixed |
| FindSubarraySum | Variable |
| LongestSubarraySum | Variable |
| CountSubarrayProduct | Variable |
| LongestUniqueSubstring | Variable |
| LongestTwoCharacterSubstring | Variable |
| CountSubstringAtMostKDistinct | Variable |
| CountExactlyKDistinct | Variable |
| MaxOnesWithSingleFlip | Variable |
| SumOfLengths | — |
| Factorial / PalindromeRecursive / ReverseStringRecursive / SumNumbersRecursive | Recursion |

See [`editorial/structy/SlidingWindowMindmap.md`](editorial/structy/SlidingWindowMindmap.md) for an interactive Mermaid mind-map showing how each problem is a slight variation of the previous one.

### Codeforces Problems Covered

| Problem | Editorial |
|---------|-----------|
| NumberOfSegmentsWithBigSum | [📄](editorial/codeforces/NumberOfSegmentsWithBigSum.md) |
| NumberOfSegmentsWithSmallSum | [📄](editorial/codeforces/NumberOfSegmentsWithSmallSum.md) |
| SegmentWithBigSum | [📄](editorial/codeforces/SegmentWithBigSum.md) |
| SegmentWithSmallSum | [📄](editorial/codeforces/SegmentWithSmallSum.md) |
| SegmentsWithSmallSet | — |
| SegmentsWithSmallSpread | — |

## Getting Started

### Prerequisites

- JDK 17+ (or whatever version your local Gradle wrapper targets)
- No additional setup required — the Gradle wrapper is included

### Build

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

### Format Code

The project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Format. To check and apply formatting:

```bash
# Check
./gradlew spotlessCheck

# Apply
./gradlew spotlessApply
```

## Annotations

Custom source-level annotations are used to tag problems with metadata:

| Annotation | Purpose |
|------------|---------|
| `@Leetcode(id, name, value)` | Links a class to its LeetCode problem |
| `@Structy(value, tag)` | Links a class to its Structy problem |
| `@Category(value, concepts)` | Categorises the DSA concept(s) used |
| `@Idea` | Documents the core algorithmic idea |
| `@CornerCase` | Highlights important edge cases |

## Editorials

Markdown editorials and mind-maps live in the [`editorial/`](editorial/) directory alongside the solution code, making it easy to review the reasoning behind each solution.
