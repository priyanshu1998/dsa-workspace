## Problem Statement

Given an array with positive numbers and a positive target number, find all of its contiguous subarrays whose **product is less than the target number**.

**Note:** *This problem is very similar to the previous one. Here, we are trying to find all the subarrays, whereas in the previous problem, we focused on finding only the count of such subarrays.*
**Example 1**:

```
Input: [2, 5, 3, 10], target=30                                              
Output: [2], [5], [2, 5], [3], [5, 3], [10]                           
Explanation: There are six contiguous subarrays whose product is less than the target.
```

**Example 2**:

```
Input: [8, 2, 6, 5], target=50                                              
Output: [8], [2], [8, 2], [6], [2, 6], [5], [6, 5]                         
Explanation: There are seven contiguous subarrays whose product is less than the target.
```

**Constraints:**


- 1 <= arr.length <= 3 * 10<sup>4</sup>
- `1 <= arr[i] <= 1000`
- 0 <= k <= 10<sup>6</sup>
