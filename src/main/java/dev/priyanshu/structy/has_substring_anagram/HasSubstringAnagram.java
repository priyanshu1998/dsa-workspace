package dev.priyanshu.structy.has_substring_anagram;

import java.util.HashMap;
import java.util.Map;

public interface HasSubstringAnagram {
    boolean hasSubstringAnagram(String s, String anagram);
}

class NaiveSolution implements HasSubstringAnagram{

    private boolean compareKeysAndValues(Map<Character, Integer> map1, Map<Character, Integer>map2){
        if(!map1.equals(map2)) return false;

        for(var entry: map1.entrySet()){
            if(!map2.getOrDefault(entry.getKey(), 0).equals(entry.getValue()))
                return false;
        }

        return true;
    }


    private boolean compareAnagram(String substring, String anagram){
        Map<Character, Integer> freq1 = new HashMap<>();
        Map<Character, Integer> freq2 = new HashMap<>();

        for(char c: substring.toCharArray()){
            freq1.put(c, freq1.getOrDefault(c, 0)+1);
        }

        for(char c: anagram.toCharArray()){
            freq2.put(c, freq2.getOrDefault(c, 0)+1);
        }

        return compareKeysAndValues(freq1, freq2);
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();
        for(int i=0; i<=s.length()-k; i++){
            var substring  = s.substring(i, i+k);
            if(compareAnagram(substring, anagram)){
                return true;
            }
        }
        return false;
    }
}

class CompareOptimized implements HasSubstringAnagram {

    Map<Character, Integer> freq = new HashMap<>();

    private boolean optimizedCompareAnagram(String subString, Map<Character, Integer> freq2){
        for(char c: subString.toCharArray()){
            var count = freq2.getOrDefault(c, 0);
            if(count == 0) return false;
            freq2.put(c, count-1);

        }
        return true;
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();

        for(char c: anagram.toCharArray()){
            freq.put(c, freq.getOrDefault(c, 0)+1);
        }

        for(int i=0; i<=s.length()-k; i++){
            var substring  = s.substring(i, i+k);
            var anagramFreq = new HashMap<>(freq);
            if(optimizedCompareAnagram(substring, anagramFreq)){
                return true;
            }
        }
        return false;
    }
}


class OptimizedSolution implements HasSubstringAnagram {

    Map<Character, Integer> anagramFreq = new HashMap<>();
    int mismatches = 0;

    private void compareNext(char popped, char added, Map<Character, Integer> freq){
        if(anagramFreq.containsKey(popped)){
            int cnt = freq.getOrDefault(popped, 0);
            if(cnt-anagramFreq.get(popped) == 1) mismatches -= 1;
            else if(cnt-anagramFreq.get(popped) == 0) mismatches += 1;
            freq.put(popped, cnt-1);
        }

        if(anagramFreq.containsKey(added)){
            int cnt = freq.getOrDefault(added, 0);
            if(anagramFreq.get(added)-cnt == 1) mismatches -= 1;
            else if(anagramFreq.get(added)-cnt==0) mismatches += 1;
            freq.put(added, cnt+1);
        }
    }

    private boolean compareKeysAndValues(Map<Character, Integer> map1, Map<Character, Integer>map2){

        for(var entry: map2.entrySet()){
            if(!map1.getOrDefault(entry.getKey(), 0).equals(entry.getValue())){
                mismatches += 1;
            }
        }

        return mismatches == 0;
    }

    private boolean compareAnagram(String substring, String anagram){
        Map<Character, Integer> freq1 = new HashMap<>();
        Map<Character, Integer> freq2 = new HashMap<>();

        for(char c: substring.toCharArray()){
            freq1.put(c, freq1.getOrDefault(c, 0)+1);
        }

        for(char c: anagram.toCharArray()){
            freq2.put(c, freq2.getOrDefault(c, 0)+1);
        }

        return compareKeysAndValues(freq1, freq2);
    }

    @Override
    public boolean hasSubstringAnagram(String s, String anagram) {
        int k = anagram.length();

        for(char c: anagram.toCharArray()){
            anagramFreq.put(c, anagramFreq.getOrDefault(c, 0)+1);
        }

        if(compareAnagram(s.substring(0, k), anagram))
            return true;


        Map<Character, Integer> freq = new HashMap<>();
        for(char c: s.substring(0, k).toCharArray()){
            freq.put(c, freq.getOrDefault(c, 0)+1);
        }

        int l = 0;
        int r = k;

        while (r<s.length()){
            compareNext(s.charAt(l), s.charAt(r), freq);
            if(mismatches == 0)return true;
            l++;
            r++;
        }

        return false;
    }
}