package dev.priyanshu.leetcode.subarray;

import dev.priyanshu.annotation.Leetcode;

import java.util.HashMap;
import java.util.Map;

@Leetcode(id = 2730, name = "find-the-longest-semi-repetitive-substring")
public interface FindTheLongestSemiRepetitiveSubstring {
    int longestSemiRepetitiveSubstring(String s);
}

class FindTheLongestSemiRepetitiveSubstringImpl implements FindTheLongestSemiRepetitiveSubstring {

    @Override
    public int longestSemiRepetitiveSubstring(String s) {
        int l = 0;
        int max = 1;
        int cnt = 0;


        for(int r=1; r<s.length(); r++){ // window [l:r] l and r inclusive.

            // add leading element
            if(s.charAt(r) == s.charAt(r-1)){
                cnt++;
            }

            // remove trailing elements while constraint is violated
            while(cnt > 1){
                if(s.charAt(l) == s.charAt(l+1)){
                    cnt--;
                }
                l++;
            }

            // update max
            max = Math.max(max, r-l+1);
        }

        return max;
    }
}
