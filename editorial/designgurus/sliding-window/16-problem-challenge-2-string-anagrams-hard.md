## **Problem Statement**

Given a string and a pattern, find **all anagrams of the pattern in the given string**.

Every **anagram** is a **permutation** of a string. As we know, when we are not allowed to repeat characters while finding permutations of a string, we get $N!$ permutations (or anagrams) of a string having $N$ characters. For example, here are the six anagrams of the string `abc`:

1. abc
1. acb
1. bac
1. bca
1. cab
1. cba

Write a function to return a list of starting indices of the anagrams of the pattern in the given string.

**Example 1:**


```
Input: str="ppqp", pattern="pq"  
Output: [1, 2]  
Explanation: The two anagrams of the pattern in the given string are "pq" and "qp".
```

**Example 2:**

```
Input: str="abbcabc", pattern="abc"  
Output: [2, 3, 4]  
Explanation: The three anagrams of the pattern in the given string are "bca", "cab", and "abc".
```

**Constraints:**

- 1 <= s.length, p.length <= 3 * 10<sup>4</sup>
- `str` and `pattern` consist of lowercase English letters.
