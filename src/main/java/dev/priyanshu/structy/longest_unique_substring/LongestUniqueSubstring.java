package dev.priyanshu.structy.longest_unique_substring;

import java.util.HashSet;

public interface LongestUniqueSubstring {
    int longestUniqueSubstring(String s);
}


class NaiveSolution implements LongestUniqueSubstring {

    private boolean isUnique(String s){
        var set = new HashSet<Character>();
        for (char c: s.toCharArray()){
            if(set.contains(c))
                return false;

            set.add(c);
        }
        return true;
    }


    @Override
    public int longestUniqueSubstring(String s) {
        int maxLength = 0;
        for(int i=0; i<s.length(); i++){
            for(int j=i+1; j<=s.length(); j++){
                if(isUnique(s.substring(i, j))){
                    maxLength = Math.max(maxLength, j-i);
                }else{
                    break;
                }
            }
        }

        return maxLength;
    }
}

class OptimizedSolution implements LongestUniqueSubstring {


    @Override
    public int longestUniqueSubstring(String s) {
        int maxLength = 0;

        int l = 0;
        int r = 0;

        var set = new HashSet<Character>();
        while (r<s.length()){

            while(set.contains(s.charAt(r))){
                set.remove(s.charAt(l));
                l++;
            }
            set.add(s.charAt(r));
            maxLength = Math.max(maxLength, set.size());
            r++;
        }

        return maxLength;
    }
}
