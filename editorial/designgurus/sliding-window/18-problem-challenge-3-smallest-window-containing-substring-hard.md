## **Problem Statement**

Given a string and a pattern, find the **smallest substring** in the given string which has **all the character occurrences of the given pattern**.




**Example 1:**

```
Input: String="aabdec", Pattern="abc"  
Output: "abdec"  
Explanation: The smallest substring having all characters of the pattern is "abdec"
```

**Example 2:**

```
Input: String="aabdec", Pattern="abac"  
Output: "aabdec"  
Explanation: The smallest substring having all characters occurrences of the pattern is "aabdec"
```

**Example 3:**
```
Input: String="abdbca", Pattern="abc"  
Output: "bca"  
Explanation: The smallest substring having all characters of the pattern is "bca".
```

**Example 4:**
```
Input: String="adcad", Pattern="abc"  
Output: ""  
Explanation: No substring in the given string has all characters of the pattern
```

**Constraints:**

- m == String.length
- n == Pattern.length
- 1 <= m, n <= 10<sup>5</sup>
- `String` and `Pattern` consist of uppercase and lowercase English letters.
