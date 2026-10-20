package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface NumberOfSubstringsContainingAllThreeCharacters {
    int numberOfSubstrings(String s);
}

class NumberOfSubstringsContainingAllThreeCharactersImpl implements NumberOfSubstringsContainingAllThreeCharacters {


    private static void add(Character c, Map<Character, Integer> map){
        map.put(c, map.getOrDefault(c, 0)+1);
    }

    private static void remove(Character c, Map<Character, Integer> map){
        int v = map.get(c);
        if(v == 1){
            map.remove(c);
        }else{
            map.put(c, v-1);
        }
    }

    private static boolean check(Map<Character, Integer> map){
        return map.containsKey('a') && map.containsKey('b') && map.containsKey('c');
    }


    @Override
    public int numberOfSubstrings(String s) {
        int l = 0;
        int r = 0;
        int n = s.length();

        int count = 0;
        Map<Character, Integer> map = new HashMap<>();

        while(l < n){
            if(!check(map) && r < n){
                add(s.charAt(r), map);
                r+=1;
            }else{
//                System.out.printf("l=%d, r=%d, map=%s\n", l, r, map);
                if(check(map)) count += n-r+1;

                remove(s.charAt(l), map);
                l++;
            }
        }


        return count;
    }
}
