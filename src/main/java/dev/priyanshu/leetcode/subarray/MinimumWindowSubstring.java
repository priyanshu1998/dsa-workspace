package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface MinimumWindowSubstring {
    String minWindow(String s, String t);
}

class MinimumWindowSubstringImpl implements MinimumWindowSubstring {

    int l = 0;
    int r = 0;

    private void add(Character c, Map<Character, Integer> map) {
        var v = map.getOrDefault(c, 0);
        map.put(c, v+1);
        r++;
    }

    private void remove(Character c, Map<Character, Integer> map){
        var v = map.getOrDefault(c, 0);
        if(v == 1){
            map.remove(c);
        }else{
            map.put(c, v-1);
        }
        l++;
    }

    private static boolean check(Map<Character, Integer> freqS, Map<Character, Integer> freqT){
        for(char c: freqT.keySet()){
            if(freqS.getOrDefault(c, 0) < freqT.get(c)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String minWindow(String s, String t) {

        var freqT = new HashMap<Character, Integer>();
        updateFreq(t, freqT);

        var freqS = new HashMap<Character, Integer>();
        int startingPos = -1;
        int len = s.length()+1;

        while(r < s.length()){
            add(s.charAt(r), freqS);
            while(check(freqS, freqT)){

                if(r-l < len){
                    len = r-l;
                    startingPos = l;
                }
                remove(s.charAt(l), freqS);
            }
        }

        int endingPos = startingPos + len;

        return startingPos!=-1?s.substring(startingPos, endingPos):"";
    }

    private void updateFreq(String t, HashMap<Character, Integer> map) {
        for(char c: t.toCharArray()){
            var v = map.getOrDefault(c, 0);
            map.put(c, v+1);        }
    }
}
