## Problem Statement

Given an array `nums` with positive numbers and a positive integer `target`, return the **count** of contiguous subarrays whose **product is less than the target number**.


### Examples

#### Example 1:


- **Input:** nums = [2, 5, 3, 10], target=30                                              
- **Output:** `6`                       
- **Explanation:** There are six contiguous subarrays ([2], [5], [2, 5], [3], [5, 3], [10]) whose product is less than the target. 


#### Example 2:

- **Input:** nums = [8, 2, 6, 5], target=50                                              
- **Output:** `7`                        
- **Explanation:** There are seven contiguous subarrays ([8], [2], [8, 2], [6], [2, 6], [5], [6, 5]) whose product is less than the target.


#### Example 3:
- **Input**: nums = [10, 5, 2, 6], k = 0
- **Expected Output**: `0`
- **Explanation**: Subarrays with product less than `0` doesn't exists. 


**Constraints:**


- 1 <= nums.length <= 3 * 10<sup>4</sup>
- `1 <= nums[i] <= 1000`
- 0 <= k <= 10<sup>6</sup>
