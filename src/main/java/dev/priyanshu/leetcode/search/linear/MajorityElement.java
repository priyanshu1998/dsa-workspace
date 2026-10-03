package dev.priyanshu.leetcode.search.linear;

public interface MajorityElement {
    int majorityElement(int[] nums);
}


class MajorityElementImpl implements MajorityElement{
    private int vote(int[] nums){
        int diff = 0;
        int majority = -1;

        for(int num: nums){
            if(diff == 0){
                majority = num;
            }

            if(num == majority){
                diff += 1;
            } else {
                diff -= 1;
            }
        }

        return majority;
    }

    @Override
    public int majorityElement(int[] nums) {
        int candidate = vote(nums);

        int count = 0;
        for(int num: nums){
            if(num == candidate){
                count += 1;
            }
        }

        if(count <= nums.length/2){
            return -1;
        }

        return candidate;
    }
}