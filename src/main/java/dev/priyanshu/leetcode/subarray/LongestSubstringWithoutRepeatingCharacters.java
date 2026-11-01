package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface LongestSubstringWithoutRepeatingCharacters {
    int lengthOfLongestSubstring(String s);
}

class LongestSubstringWithoutRepeatingCharactersImpl implements LongestSubstringWithoutRepeatingCharacters{

    private void add(Map<Character, Integer> freq, Character c){
        freq.put(c, freq.getOrDefault(c, 0)+1);
    }

    private void remove(Map<Character, Integer> freq, Character c){
        int v = freq.get(c);
        if(v == 1){
            freq.remove(c);
        }else{
            freq.put(c, v-1);
        }
    }

    @Override
    public int lengthOfLongestSubstring(String s) {
        int l = 0;
        Map<Character, Integer> freq = new HashMap<>();
        int longest = 0;

        for(int r=0; r<s.length(); r++){
            // add the leading element to sum
            add(freq, s.charAt(r));

            // remove trailing elements while constraint is violated
            while(freq.get(s.charAt(r)) > 1){
                remove(freq, s.charAt(l));
                l++;
            }

            // update max
            longest = Math.max(longest, r-l+1);
        }

        return longest;
    }
}
