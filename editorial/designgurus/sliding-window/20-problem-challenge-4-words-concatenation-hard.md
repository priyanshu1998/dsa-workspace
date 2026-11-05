## **Problem Statement**
You're given a string `s` and a list of words `words`, where all words have the same length.

A **concatenated substring** is formed by joining all the words from any permutation of `words` - each used exactly once, without any extra characters in between.

For example, if `words = ["ab", "cd", "ef"]`, then valid concatenated strings include `"abcdef"`, `"abefcd"`, `"cdabef"`, `"cdefab"`, `"efabcd"`, and `"efcdab"`. A string like `"acdbef"` is **not valid** because it doesn't match any complete permutation of the given words.

Return **all starting indices** in `s` where such concatenated substrings appear. You can return the indices in any order.

**Example 1:**

```
Input: String="catfoxcat", Words=["cat", "fox"]  
Output: [0, 3]  
Explanation: The two substring containing both the words are "catfox" & "foxcat".
```

**Example 2:**

```
Input: String="catcatfoxfox", Words=["cat", "fox"]  
Output: [3]
Explanation: The only substring containing both the words is "catfox".
```

**Constraints:**

- 1 <= words.length <= 10<sup>4</sup>
- `1 <= words[i].length <= 30`
- `words[i]` consists of only lowercase English letters.
- All the strings of words are `unique`.
- 1 <= sum(words[i].length) <= 10<sup>5</sup>
