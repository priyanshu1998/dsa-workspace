## Why this is a Sliding Window problem

| What the question says | The signal it matches |
|---|---|
| "length of the longest contiguous subarray having all 1s" | the range is **contiguous**, and you want the **longest** one |
| "replace no more than" k "0s with 1s" | a budget that shrinking can repair |
| testing every subarray for how many zeros it holds | your first idea is **for each starting index, scan forward** |

Extend while the budget holds, shrink when it is exceeded. That is the [grow to the largest legal window variant](/course-play/grokking-the-coding-interview/doc/introduction-to-sliding-window-pattern).

**The closest alternative.** This is the previous problem with an alphabet of two. There the window tracked the most frequent letter among 26.

Here there are only 0s and 1s. The most frequent value is the count of 1s. The window is legal while its length minus that count is at most k. Recognizing it as the same problem is faster than deriving it again.


## **Solution**

This is Longest Substring with same Letters after Replacement with one difference. Here we only have two characters, 1s and 0s, in the input array.

Following a similar approach, we'll iterate through the array to add one number at a time in the window. We'll also keep track of the maximum number of repeating 1s in the current window (let's call it `maxOnesCount`). So at any time, we know that we can have a window with 1s repeating `maxOnesCount` time, so we should try to replace the remaining 0s. If we have more than `k` remaining 0s, we should shrink the window as we are not allowed to replace more than `k` 0s.

### Step-by-Step Algorithm

- Initialize `windowStart` to 0, `maxLength` to 0, and `maxOnesCount` to 0.
- Iterate over the array using `windowEnd` from 0 to the end of the array.
  - If the current element is 1, increment `maxOnesCount`.
  - Calculate the current window size as `windowEnd - windowStart + 1`.
  - If the number of 0s in the current window (`windowEnd - windowStart + 1 - maxOnesCount`) is greater than `k`, shrink the window from the start.
    - If the element at `windowStart` is 1, decrement `maxOnesCount`.
    - Increment `windowStart`.
  - Update `maxLength` to be the maximum of `maxLength` and the current window size.
- Return `maxLength` as the length of the longest subarray.

### Algorithm Walkthrough

![Slide 1](../assets/13_article_adhoc_img_ddc0524-e88-5736-dc6b-0bb7ff78f2ae.svg)
![Slide 2](../assets/13_article_adhoc_img_f7a061-7021-58f5-c34-dee8663d2e5.svg)
![Slide 3](../assets/13_article_adhoc_img_1ffab87-87a1-70dd-dab2-bac3d7525edd.svg)
![Slide 4](../assets/13_article_adhoc_img_821751f-e832-07e1-e205-6a402c35fb5a.svg)
![Slide 5](../assets/13_article_adhoc_img_3444d0-4f15-d12c-36e5-8a88073fcf.svg)
![Slide 6](../assets/13_article_adhoc_img_7bcc411-5e6b-f0d8-131d-05c117fbd1b.svg)
![Slide 7](../assets/13_article_adhoc_img_ddfc0a-87c3-4e50-24d-eab77a4c3.svg)
![Slide 8](../assets/13_article_adhoc_img_667cec8-e1d2-c32c-572f-426555d404e.svg)
![Slide 9](../assets/13_article_adhoc_img_5018cd-dc4-70ef-52cf-2a816affc3.svg)
![Slide 10](../assets/13_article_adhoc_img_26a88-ba86-cf05-845d-110c18d51162.svg)
![Slide 11](../assets/13_article_adhoc_img_dc8ef5e-e537-d304-df41-3837045d80d.svg)
