package dev.priyanshu.leetcode.subarray;

import java.util.HashMap;
import java.util.Map;

public interface CountOfSubstringsContainingEveryVowelAndKConsonants2 {
    long countOfSubstrings(String word, int k);
}

class CountOfSubstringsContainingEveryVowelAndKConsonants2Impl implements CountOfSubstringsContainingEveryVowelAndKConsonants2 {

    private boolean isVowel(Character c){
        return (c == 'a' || c=='e' || c=='i' || c == 'o' || c=='u');
    }

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


    private long countOfSubstringsAtLeastK(String word, int k) {
        Map<Character, Integer> vowels = new HashMap<>();
        int consonants = 0;
        int l = 0;

        int ways = 0;

        for(int r=0; r<word.length(); r++){
            if(isVowel(word.charAt(r))){
                add(vowels, word.charAt(r));
            }else{
                consonants++;
            }


            while(vowels.size() == 5 && consonants >= k){
                if(isVowel(word.charAt(l))){
                    remove(vowels, word.charAt(l));
                }else{
                    consonants--;
                }
                l++;
            }

            ways += l;
        }

        return ways;
    }

    @Override
    public long countOfSubstrings(String word, int k) {
        return countOfSubstringsAtLeastK(word, k) - countOfSubstringsAtLeastK(word, k+1);
    }
}
