package dev.priyanshu.striver;

import dev.priyanshu.annotation.Striver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Striver(groups = "Striver's 180",
category="Hashing & Prefix Sums",
path="practice/dsa/longest-consecutive-sequence-in-an-array")
public interface LongestConsecutiveSequence {
    int longestConsecutive(int[] nums);
}


class LongestConsecutiveSequenceImpl implements LongestConsecutiveSequence{

    @Override
    public int longestConsecutive(int[] nums) {
        Set<Integer> unique = Arrays.stream(nums).boxed().collect(Collectors.toSet());
        int max = 0;
        for(int num: unique){
            if(!unique.contains(num-1)){
                int t = num;
                int len = 0;
                while(unique.contains(t)){
                    len++;
                    t++;
                }

                max = Math.max(max, len);
            }
        }
        return max;
    }
}
